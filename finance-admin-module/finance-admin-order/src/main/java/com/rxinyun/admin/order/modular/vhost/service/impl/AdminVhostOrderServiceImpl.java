package com.rxinyun.admin.order.modular.vhost.service.impl;

import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.rxinyun.admin.order.modular.ecs.entity.Order;
import com.rxinyun.admin.order.modular.ecs.service.OrderService;
import com.rxinyun.admin.order.modular.manual.response.ManualOrderResult;
import com.rxinyun.admin.order.modular.vhost.entity.AdminVhostOrderConfig;
import com.rxinyun.admin.order.modular.vhost.mapper.AdminVhostOrderConfigMapper;
import com.rxinyun.admin.order.modular.vhost.param.AdminVhostCreateOrderParam;
import com.rxinyun.admin.order.modular.vhost.param.AdminVhostRenewOrderParam;
import com.rxinyun.admin.order.modular.vhost.param.AdminVhostUpgradeOrderParam;
import com.rxinyun.admin.order.modular.vhost.response.AdminVhostPriceResponse;
import com.rxinyun.admin.order.modular.vhost.service.AdminVhostCalculatePriceService;
import com.rxinyun.admin.order.modular.vhost.service.AdminVhostOrderService;
import com.rxinyun.common.enums.UpgradeItemEnum;
import com.rxinyun.common.enums.business.PayTypeEnum;
import com.rxinyun.common.enums.order.OrderStatusEnum;
import com.rxinyun.common.enums.order.OrderTypeEnum;
import com.rxinyun.common.enums.order.ProductTypeEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.common.util.OrderNoGenerator;
import com.rxinyun.common.util.SnowflakeIdGenerator;
import com.rxinyun.member.admin.api.BalanceApi;
import com.rxinyun.member.admin.pojo.BaseBalance;
import com.rxinyun.member.member.api.MemberApi;
import com.rxinyun.member.member.pojo.BaseMember;
import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
public class AdminVhostOrderServiceImpl implements AdminVhostOrderService {
   private static final int DEDUCT_MODE_NONE = 1;
   private static final int DEDUCT_MODE_AUTO = 2;
   private static final int DEDUCT_MODE_MANUAL = 3;
   @Resource
   private AdminVhostCalculatePriceService adminVhostCalculatePriceService;
   @Resource
   private OrderService orderService;
   @Resource
   private AdminVhostOrderConfigMapper adminVhostOrderConfigMapper;
   @Resource
   private MemberApi memberApi;
   @Resource
   private BalanceApi balanceApi;

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public CommonResult<ManualOrderResult> create(AdminVhostCreateOrderParam param) {
      AdminVhostPriceResponse price = this.adminVhostCalculatePriceService.calculateNewPurchase(param);
      return this.saveOrder(price, param.getMemberId(), param.getDeductMode(), param.getDeductAmount(), null, "后台手动开通虚拟主机", param);
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public CommonResult<ManualOrderResult> renew(AdminVhostRenewOrderParam param) {
      AdminVhostPriceResponse price = this.adminVhostCalculatePriceService.calculateRenew(param);
      return this.saveOrder(price, param.getMemberId(), param.getDeductMode(), param.getDeductAmount(), null, "后台手动续费虚拟主机", param);
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public CommonResult<ManualOrderResult> upgradeTraffic(AdminVhostUpgradeOrderParam param) {
      AdminVhostPriceResponse price = this.adminVhostCalculatePriceService.calculateUpgradeTraffic(param);
      return this.saveOrder(
         price, param.getMemberId(), param.getDeductMode(), param.getDeductAmount(), UpgradeItemEnum.UPGRADE_TRAFFIC.getCode(), "后台手动升级虚拟主机流量", param
      );
   }

   private CommonResult<ManualOrderResult> saveOrder(
      AdminVhostPriceResponse price, Long memberId, Integer deductMode, BigDecimal deductAmount, Integer upgradeItem, String remark, Object requestParam
   ) {
      BaseMember member = this.memberApi.detail(memberId);
      if (member == null) {
         throw new CommonException("会员不存在", new Object[0]);
      } else {
         String orderNo = OrderNoGenerator.generateOrderNumber(ProductTypeEnum.VHOST.getCode());
         BigDecimal settledAmount = this.resolveSettledAmount(deductMode, deductAmount, price.getPayPrice());
         Order order = new Order();
         order.setId(SnowflakeIdGenerator.generateId());
         order.setOrderNo(orderNo);
         order.setBillingMode(1);
         order.setProductType(ProductTypeEnum.VHOST.getCode());
         order.setOrderType(price.getOrderType());
         order.setUpgradeItem(upgradeItem);
         order.setOrderStatus(OrderStatusEnum.UNPAID.getCode());
         order.setMemberNo(member.getId());
         order.setMemberName(member.getName());
         order.setMemberPhone(member.getPhone());
         order.setTotalPrice(this.defaultAmount(price.getTotalPrice()));
         order.setPayPrice(settledAmount);
         if (order.getTotalPrice().compareTo(order.getPayPrice()) >= 0) {
            order.setDiscountPrice(order.getTotalPrice().subtract(order.getPayPrice()));
         } else {
            order.setDiscountPrice(BigDecimal.ZERO);
         }

         order.setCouponPrice(BigDecimal.ZERO);
         order.setCouponUseCount(0);
         order.setOriginalOrderNo(price.getOriginalOrderNo());
         order.setCreateTime(new Date());
         order.setUpdateTime(new Date());
         order.setPayType(PayTypeEnum.BALANCE.getCode());
         this.orderService.save(order);
         this.adminVhostOrderConfigMapper.insert(this.buildConfig(price, orderNo, settledAmount));
         this.settleAndFulfill(order, memberId, settledAmount, remark, requestParam);
         ManualOrderResult result = new ManualOrderResult();
         result.setOrderNo(order.getOrderNo());
         result.setMemberId(order.getMemberNo());
         result.setProductType(order.getProductType());
         result.setOrderType(order.getOrderType());
         result.setOrderStatus(OrderStatusEnum.PAID.getCode());
         result.setTotalPrice(order.getTotalPrice());
         result.setPayPrice(settledAmount);
         return CommonResult.data(result);
      }
   }

   private AdminVhostOrderConfig buildConfig(AdminVhostPriceResponse price, String orderNo, BigDecimal settledAmount) {
      AdminVhostOrderConfig config = new AdminVhostOrderConfig();
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
      config.setMonthAmount(this.defaultAmount(price.getMonthAmount()));
      config.setTrafficAmount(this.defaultAmount(price.getTrafficAmount()));
      config.setTotalPrice(this.defaultAmount(price.getTotalPrice()));
      config.setDiscountTemplateId(price.getDiscountTemplateId());
      config.setDiscountRate(price.getDiscountRate());
      BigDecimal discountPrice = this.defaultAmount(price.getTotalPrice()).subtract(settledAmount);
      config.setDiscountPrice(discountPrice.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : discountPrice);
      config.setCouponPrice(BigDecimal.ZERO);
      config.setPayPrice(settledAmount);
      config.setPhpVersions(price.getPhpVersions());
      config.setPhpVersion(price.getPhpVersion());
      config.setDomains(price.getDomains());
      config.setMysqlVersions(price.getMysqlVersions());
      return config;
   }

   private void settleAndFulfill(Order order, Long memberId, BigDecimal settledAmount, String remark, Object requestParam) {
      Date payTime = new Date();
      if (settledAmount.compareTo(BigDecimal.ZERO) > 0) {
         BaseBalance balance = this.balanceApi.selectByMemberNo(memberId);
         if (balance == null) {
            throw new CommonException("账号余额记录不存在", new Object[0]);
         }

         if (balance.getBalance().compareTo(settledAmount) < 0) {
            throw new CommonException("账号余额不足，当前余额：" + balance.getBalance() + "，需要金额：" + settledAmount, new Object[0]);
         }

         this.balanceApi.deductAndRecord(memberId, settledAmount, order.getOrderNo(), remark, JSONUtil.toJsonStr(requestParam), payTime);
      }

      order.setOrderStatus(OrderStatusEnum.PAID.getCode());
      order.setPayTime(payTime);
      order.setPayType(PayTypeEnum.BALANCE.getCode());
      order.setUpdateTime(payTime);
      this.orderService.updateById(order);
      if (TransactionSynchronizationManager.isSynchronizationActive()) {
         TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            public void afterCommit() {
               ((OrderService)SpringUtil.getBean(OrderService.class)).handlePaidOrderAsync(order.getOrderNo());
            }
         });
      } else {
         ((OrderService)SpringUtil.getBean(OrderService.class)).handlePaidOrderAsync(order.getOrderNo());
      }
   }

   private BigDecimal resolveSettledAmount(Integer deductMode, BigDecimal manualAmount, BigDecimal autoAmount) {
      if (deductMode == null) {
         throw new CommonException("扣费模式不能为空", new Object[0]);
      } else if (1 == deductMode) {
         return BigDecimal.ZERO;
      } else if (2 == deductMode) {
         return this.defaultAmount(autoAmount);
      } else if (3 == deductMode) {
         if (manualAmount != null && manualAmount.compareTo(BigDecimal.ZERO) >= 0) {
            return manualAmount;
         } else {
            throw new CommonException("手动扣费金额不能为空且不能小于0", new Object[0]);
         }
      } else {
         throw new CommonException("不支持的扣费模式", new Object[0]);
      }
   }

   private BigDecimal defaultAmount(BigDecimal amount) {
      return amount == null ? BigDecimal.ZERO : amount;
   }
}
