package com.rxinyun.member.order.modular.vhost.service.impl;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.rxinyun.common.enums.UpgradeItemEnum;
import com.rxinyun.common.enums.order.OrderStatusEnum;
import com.rxinyun.common.enums.order.OrderTypeEnum;
import com.rxinyun.common.enums.order.ProductTypeEnum;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.common.util.OrderNoGenerator;
import com.rxinyun.member.core.util.StpClientLoginUserUtil;
import com.rxinyun.member.member.pojo.SaBaseClientLoginUser;
import com.rxinyun.member.order.modular.coupon.OrderCouponSupportService;
import com.rxinyun.member.order.modular.ecs.entity.MemberOrder;
import com.rxinyun.member.order.modular.ecs.service.MemberOrderService;
import com.rxinyun.member.order.modular.vhost.entity.VhostOrderConfig;
import com.rxinyun.member.order.modular.vhost.param.VhostOrderCreateParam;
import com.rxinyun.member.order.modular.vhost.param.VhostRenewOrderParam;
import com.rxinyun.member.order.modular.vhost.param.VhostUpgradeTrafficOrderParam;
import com.rxinyun.member.order.modular.vhost.response.VhostCalculatePriceResponse;
import com.rxinyun.member.order.modular.vhost.service.VhostCalculatePriceService;
import com.rxinyun.member.order.modular.vhost.service.VhostOrderConfigService;
import com.rxinyun.member.order.modular.vhost.service.VhostOrderService;
import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VhostOrderServiceImpl implements VhostOrderService {
   @Resource
   private VhostCalculatePriceService vhostCalculatePriceService;
   @Resource
   private VhostOrderConfigService vhostOrderConfigService;
   @Resource
   private MemberOrderService memberOrderService;
   @Resource
   private OrderCouponSupportService orderCouponSupportService;

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public CommonResult<MemberOrder> create(VhostOrderCreateParam param) {
      VhostCalculatePriceResponse price = this.vhostCalculatePriceService.calculateNewPurchase(param);
      return this.saveOrder(price, param.getPayType(), param.getCouponCode());
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public CommonResult<MemberOrder> renew(VhostRenewOrderParam param) {
      VhostCalculatePriceResponse price = this.vhostCalculatePriceService.calculateRenew(param);
      return this.saveOrder(price, param.getPayType(), param.getCouponCode());
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public CommonResult<MemberOrder> upgradeTraffic(VhostUpgradeTrafficOrderParam param) {
      VhostCalculatePriceResponse price = this.vhostCalculatePriceService.calculateUpgradeTraffic(param);
      return this.saveOrder(price, param.getPayType(), param.getCouponCode());
   }

   private CommonResult<MemberOrder> saveOrder(VhostCalculatePriceResponse price, Integer payType, String couponCode) {
      SaBaseClientLoginUser loginUser = StpClientLoginUserUtil.getClientLoginUser();
      String orderNo = OrderNoGenerator.generateOrderNumber(ProductTypeEnum.VHOST.getCode());
      MemberOrder order = new MemberOrder();
      order.setOrderNo(orderNo);
      order.setBillingMode(1);
      order.setProductType(ProductTypeEnum.VHOST.getCode());
      order.setOrderType(price.getOrderType());
      if (Integer.valueOf(OrderTypeEnum.UPGRADE.getCode()).equals(price.getOrderType())) {
         order.setUpgradeItem(UpgradeItemEnum.UPGRADE_TRAFFIC.getCode());
      }

      order.setOrderStatus(OrderStatusEnum.UNPAID.getCode());
      order.setMemberNo(loginUser.getId());
      order.setMemberName(loginUser.getName());
      order.setMemberPhone(loginUser.getPhone());
      order.setTotalPrice(price.getTotalPrice());
      order.setDiscountPrice(price.getDiscountPrice() == null ? BigDecimal.ZERO : price.getDiscountPrice());
      order.setPayPrice(price.getPayPrice());
      order.setCouponCode(couponCode);
      order.setCouponPrice(price.getCouponPrice() == null ? BigDecimal.ZERO : price.getCouponPrice());
      order.setCouponUseCount(0);
      order.setCreateTime(new Date());
      order.setUpdateTime(new Date());
      order.setPayType(payType);
      this.orderCouponSupportService
         .lockCoupon(
            order,
            loginUser.getId(),
            ProductTypeEnum.VHOST.getCode(),
            price.getOrderType(),
            price.getLineId(),
            couponCode,
            this.orderCouponSupportService.resolveOrderAmountBeforeCoupon(order.getPayPrice(), order.getCouponPrice())
         );
      this.memberOrderService.save(order);
      price.setCouponPrice(order.getCouponPrice());
      price.setPayPrice(order.getPayPrice());
      this.vhostOrderConfigService.save(this.buildConfig(price, orderNo));
      return CommonResult.data(order);
   }

   private VhostOrderConfig buildConfig(VhostCalculatePriceResponse price, String orderNo) {
      VhostOrderConfig config = new VhostOrderConfig();
      config.setId(IdWorker.getIdStr());
      config.setOrderNo(orderNo);
      config.setOrderType(price.getOrderType() == null ? OrderTypeEnum.NEW_PURCHASE.getCode() : price.getOrderType());
      config.setHostId(price.getHostId());
      config.setVersionId(price.getVersionId());
      config.setVersionName(price.getVersionName());
      config.setRegionId(price.getRegionId());
      config.setRegionName(price.getRegionName());
      config.setLineId(price.getLineId());
      config.setLineName(price.getLineName());
      config.setMonths(price.getMonths());
      config.setNumber(price.getNumber() == null ? 1 : price.getNumber());
      config.setRemainingDays(price.getRemainingDays());
      config.setTrafficSize(price.getTrafficSize());
      config.setUpgradeTraffic(price.getUpgradeTraffic());
      config.setTrafficFreeSize(price.getTrafficFreeSize());
      config.setBillableTraffic(price.getBillableTraffic());
      config.setMonthPrice(price.getMonthPrice());
      config.setTrafficPrice(price.getTrafficPrice());
      config.setTrafficDayPrice(price.getTrafficDayPrice());
      config.setMonthAmount(price.getMonthAmount() == null ? BigDecimal.ZERO : price.getMonthAmount());
      config.setTrafficAmount(price.getTrafficAmount() == null ? BigDecimal.ZERO : price.getTrafficAmount());
      config.setTotalPrice(price.getTotalPrice() == null ? BigDecimal.ZERO : price.getTotalPrice());
      config.setDiscountTemplateId(price.getDiscountTemplateId());
      config.setDiscountRate(price.getDiscountRate());
      config.setDiscountPrice(price.getDiscountPrice() == null ? BigDecimal.ZERO : price.getDiscountPrice());
      config.setCouponPrice(price.getCouponPrice() == null ? BigDecimal.ZERO : price.getCouponPrice());
      config.setPayPrice(price.getPayPrice() == null ? BigDecimal.ZERO : price.getPayPrice());
      config.setPhpVersions(price.getPhpVersions());
      config.setPhpVersion(price.getPhpVersion());
      config.setDomains(price.getDomains());
      config.setMysqlVersions(price.getMysqlVersions());
      return config;
   }
}
