package com.rxinyun.member.order.modular.dns.service.impl;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.rxinyun.common.enums.order.OrderStatusEnum;
import com.rxinyun.common.enums.order.OrderTypeEnum;
import com.rxinyun.common.enums.order.ProductTypeEnum;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.common.util.OrderNoGenerator;
import com.rxinyun.member.core.util.StpClientLoginUserUtil;
import com.rxinyun.member.member.pojo.SaBaseClientLoginUser;
import com.rxinyun.member.order.modular.coupon.OrderCouponSupportService;
import com.rxinyun.member.order.modular.dns.entity.DnsOrderConfig;
import com.rxinyun.member.order.modular.dns.param.DnsOrderCreateParam;
import com.rxinyun.member.order.modular.dns.param.DnsRenewOrderParam;
import com.rxinyun.member.order.modular.dns.param.DnsUpgradeQuotaOrderParam;
import com.rxinyun.member.order.modular.dns.response.DnsCalculatePriceResponse;
import com.rxinyun.member.order.modular.dns.service.DnsCalculatePriceService;
import com.rxinyun.member.order.modular.dns.service.DnsOrderConfigService;
import com.rxinyun.member.order.modular.dns.service.DnsOrderService;
import com.rxinyun.member.order.modular.ecs.entity.MemberOrder;
import com.rxinyun.member.order.modular.ecs.service.MemberOrderService;
import com.rxinyun.production.api.DnsPackageInstanceApi;
import com.rxinyun.production.api.dto.DnsPackageInstanceVO;
import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DnsOrderServiceImpl implements DnsOrderService {
   @Resource
   private DnsCalculatePriceService dnsCalculatePriceService;
   @Resource
   private DnsOrderConfigService dnsOrderConfigService;
   @Resource
   private MemberOrderService memberOrderService;
   @Resource
   private OrderCouponSupportService orderCouponSupportService;
   @Resource
   private DnsPackageInstanceApi dnsPackageInstanceApi;

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public CommonResult<MemberOrder> create(DnsOrderCreateParam param) {
      DnsCalculatePriceResponse price = this.dnsCalculatePriceService.calculateNewPurchase(param);
      return this.createOrder(price, OrderTypeEnum.NEW_PURCHASE.getCode(), param.getPayType(), param.getCouponCode(), null);
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public CommonResult<MemberOrder> renew(DnsRenewOrderParam param) {
      this.updateOrderStatusByInstanceId(param.getInstanceId());
      DnsCalculatePriceResponse price = this.dnsCalculatePriceService.calculateRenew(param);
      DnsPackageInstanceVO instance = this.dnsPackageInstanceApi.getMemberInstance(param.getInstanceId(), this.currentMemberId());
      return this.createOrder(price, OrderTypeEnum.RENEW.getCode(), param.getPayType(), param.getCouponCode(), instance.getSourceOrderNo());
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public CommonResult<MemberOrder> upgradeQuota(DnsUpgradeQuotaOrderParam param) {
      this.updateOrderStatusByInstanceId(param.getInstanceId());
      DnsCalculatePriceResponse price = this.dnsCalculatePriceService.calculateUpgradeQuota(param);
      DnsPackageInstanceVO instance = this.dnsPackageInstanceApi.getMemberInstance(param.getInstanceId(), this.currentMemberId());
      return this.createOrder(price, OrderTypeEnum.UPGRADE.getCode(), param.getPayType(), param.getCouponCode(), instance.getSourceOrderNo());
   }

   private CommonResult<MemberOrder> createOrder(DnsCalculatePriceResponse price, Integer orderType, Integer payType, String couponCode, String originalOrderNo) {
      SaBaseClientLoginUser loginUser = StpClientLoginUserUtil.getClientLoginUser();
      String orderNo = OrderNoGenerator.generateOrderNumber(ProductTypeEnum.DNS.getCode());
      MemberOrder order = new MemberOrder();
      order.setOrderNo(orderNo);
      order.setBillingMode(1);
      order.setProductType(ProductTypeEnum.DNS.getCode());
      order.setOrderType(orderType);
      order.setOrderStatus(OrderStatusEnum.UNPAID.getCode());
      order.setMemberNo(loginUser.getId());
      order.setMemberName(loginUser.getName());
      order.setMemberPhone(loginUser.getPhone());
      order.setTotalPrice(price.getTotalPrice());
      order.setPayPrice(price.getPayPrice());
      order.setDiscountPrice(BigDecimal.ZERO);
      order.setCouponCode(couponCode);
      order.setCouponPrice(price.getCouponPrice() == null ? BigDecimal.ZERO : price.getCouponPrice());
      order.setCouponUseCount(0);
      order.setOriginalOrderNo(originalOrderNo);
      order.setCreateTime(new Date());
      order.setUpdateTime(new Date());
      order.setPayType(payType);
      this.orderCouponSupportService
         .lockCoupon(
            order,
            loginUser.getId(),
            ProductTypeEnum.DNS.getCode(),
            orderType,
            null,
            couponCode,
            this.orderCouponSupportService.resolveOrderAmountBeforeCoupon(order.getPayPrice(), order.getCouponPrice())
         );
      this.memberOrderService.save(order);
      price.setCouponPrice(order.getCouponPrice());
      price.setPayPrice(order.getPayPrice());
      DnsOrderConfig config = this.buildConfig(price, orderNo, orderType);
      this.dnsOrderConfigService.save(config);
      return CommonResult.data(order);
   }

   private DnsOrderConfig buildConfig(DnsCalculatePriceResponse price, String orderNo, Integer orderType) {
      DnsOrderConfig config = new DnsOrderConfig();
      config.setId(IdWorker.getIdStr());
      config.setOrderNo(orderNo);
      config.setOrderType(orderType);
      config.setInstanceId(price.getInstanceId());
      config.setPackageId(price.getPackageId());
      config.setPackageName(price.getPackageName());
      config.setLineGroupId(price.getLineGroupId());
      config.setSingleDomainPrice(price.getSingleDomainPrice());
      config.setMinTtl(price.getMinTtl());
      config.setDomainQuota(price.getDomainQuota());
      config.setUpgradeDomainQuota(price.getUpgradeDomainQuota());
      config.setPeriodMonths(price.getPeriodMonths());
      config.setRemainingDays(price.getRemainingDays());
      config.setTotalPrice(price.getTotalPrice());
      config.setPayPrice(price.getPayPrice());
      return config;
   }

   private void updateOrderStatusByInstanceId(String instanceId) {
      if (!StrUtil.isBlank(instanceId)) {
         List<DnsOrderConfig> configs = this.dnsOrderConfigService
            .list((Wrapper<DnsOrderConfig>)((LambdaQueryWrapper<DnsOrderConfig>)new LambdaQueryWrapper<DnsOrderConfig>().eq(DnsOrderConfig::getInstanceId, instanceId)).isNotNull(DnsOrderConfig::getOrderNo));
         if (configs != null && !configs.isEmpty()) {
            List<String> orderNos = configs.stream().map(DnsOrderConfig::getOrderNo).filter(CharSequenceUtil::isNotBlank).distinct().toList();
            if (!orderNos.isEmpty()) {
               for (MemberOrder memberOrder : this.memberOrderService
                  .list(
                     (Wrapper<MemberOrder>)((LambdaQueryWrapper<MemberOrder>)new LambdaQueryWrapper<MemberOrder>().in(MemberOrder::getOrderNo, orderNos))
                        .eq(MemberOrder::getOrderStatus, OrderStatusEnum.UNPAID.getCode())
                  )) {
                  if (memberOrder.getOrderStatus().equals(OrderStatusEnum.UNPAID.getCode())) {
                     memberOrder.setOrderStatus(OrderStatusEnum.CANCELLED.getCode());
                     this.memberOrderService.updateById(memberOrder);
                     this.orderCouponSupportService.releaseCoupon(memberOrder.getOrderNo());
                  }
               }
            }
         }
      }
   }

   private String currentMemberId() {
      return String.valueOf(StpClientLoginUserUtil.getClientLoginUser().getId());
   }
}
