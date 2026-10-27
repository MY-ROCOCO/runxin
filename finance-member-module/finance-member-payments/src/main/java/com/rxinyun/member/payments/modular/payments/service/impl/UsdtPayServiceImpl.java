package com.rxinyun.member.payments.modular.payments.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rxinyun.common.enums.business.PayTypeEnum;
import com.rxinyun.common.event.OrderEvent;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.dev.api.DevConfigApi;
import com.rxinyun.member.auth.modular.user.entity.MemberBalance;
import com.rxinyun.member.auth.modular.user.entity.Recharge;
import com.rxinyun.member.auth.modular.user.service.MemberBalanceService;
import com.rxinyun.member.auth.modular.user.service.RechargeService;
import com.rxinyun.member.payments.modular.payments.entity.PaymentsRecord;
import com.rxinyun.member.payments.modular.payments.mapper.PaymentsRecordMapper;
import com.rxinyun.member.payments.modular.payments.service.AlipayService;
import com.rxinyun.member.payments.modular.payments.service.UsdtPayService;
import com.rxinyun.order.api.OrderApi;
import com.rxinyun.order.core.pojo.BaseOrder;
import com.rxinyun.payments.api.pojo.PendingUsdtPaymentDto;
import com.rxinyun.payments.api.pojo.UsdtPayResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class UsdtPayServiceImpl implements UsdtPayService {
   private static final Logger logger = LoggerFactory.getLogger(UsdtPayServiceImpl.class);
   private static final String USDT_PAY_WALLET_ADDRESS = "USDT_PAY_WALLET_ADDRESS";
   private static final String USDT_PAY_RATE = "USDT_PAY_RATE";
   private static final String USDT_PAY_CHAIN = "USDT_PAY_CHAIN";
   private static final String PAYMENT_ORDER_EXPIRATION_TIME = "PAYMENT_ORDER_EXPIRATION_TIME";
   private static final String DEFAULT_CHAIN = "TRC20";
   private static final int USDT_SCALE = 4;
   private static final long DEFAULT_EXPIRE_MS = 1800000L;
   @Autowired
   private DevConfigApi devConfigApi;
   @Autowired
   private RechargeService rechargeService;
   @Autowired
   private OrderApi orderApi;
   @Autowired
   private PaymentsRecordMapper paymentsRecordMapper;
   @Autowired
   private MemberBalanceService memberBalanceService;
   @Autowired
   private AlipayService alipayService;
   @Autowired
   private ApplicationContext applicationContext;

   @Override
   public CommonResult pay(String platform, String subject, Long memberNo, String orderNo, BigDecimal totalAmount) {
      if (totalAmount != null && totalAmount.compareTo(BigDecimal.ZERO) > 0) {
         String walletAddress = this.devConfigApi.getValueByKey("USDT_PAY_WALLET_ADDRESS");
         if (StrUtil.isBlank(walletAddress)) {
            return CommonResult.error("USDT 收款地址未配置，请联系管理员");
         } else {
            String rateStr = this.devConfigApi.getValueByKey("USDT_PAY_RATE");
            if (StrUtil.isBlank(rateStr)) {
               return CommonResult.error("USDT 汇率未配置，请联系管理员");
            } else {
               BigDecimal rate;
               try {
                  rate = new BigDecimal(rateStr.trim());
               } catch (NumberFormatException var12) {
                  logger.warn("USDT 汇率配置非法: {}", rateStr);
                  return CommonResult.error("USDT 汇率配置错误");
               }

               if (rate.compareTo(BigDecimal.ZERO) <= 0) {
                  return CommonResult.error("USDT 汇率必须大于 0");
               } else {
                  String chain = this.devConfigApi.getValueByKey("USDT_PAY_CHAIN");
                  if (StrUtil.isBlank(chain)) {
                     chain = "TRC20";
                  }

                  BigDecimal amountUsdt = this.resolveExistingExpectedAmount(subject, orderNo);
                  if (amountUsdt == null || amountUsdt.compareTo(BigDecimal.ZERO) <= 0) {
                     amountUsdt = this.generateUniqueExpectedUsdt(totalAmount, rate);
                  }

                  UsdtPayResponse response = new UsdtPayResponse();
                  response.setWalletAddress(walletAddress.trim());
                  response.setAmountUsdt(amountUsdt);
                  response.setOrderNo(orderNo);
                  response.setChain(chain.trim());
                  response.setRate(rate);
                  if (amountUsdt != null) {
                     if ("用户充值".equals(subject)) {
                        this.rechargeService.updateExpectedUsdtAmount(orderNo, amountUsdt);
                     } else {
                        this.orderApi.updateExpectedUsdtAmount(orderNo, amountUsdt);
                     }
                  }

                  logger.info(
                     "USDT 支付信息 orderNo={} amountCny={} amountUsdt={} rate={} walletAddress={}",
                     new Object[]{orderNo, totalAmount, amountUsdt, rate, walletAddress != null ? "已配置" : "未配置"}
                  );
                  return CommonResult.data(response);
               }
            }
         }
      } else {
         return CommonResult.error("支付金额不合法");
      }
   }

   private BigDecimal resolveExistingExpectedAmount(String subject, String orderNo) {
      if (StrUtil.isBlank(orderNo)) {
         return null;
      } else if ("用户充值".equals(subject)) {
         try {
            Recharge recharge = this.rechargeService.queryEntity(orderNo);
            if (recharge != null
               && Integer.valueOf(1).equals(recharge.getStatus())
               && recharge.getExpectedUsdtAmount() != null
               && recharge.getExpectedUsdtAmount().compareTo(BigDecimal.ZERO) > 0) {
               return recharge.getExpectedUsdtAmount().setScale(4, RoundingMode.HALF_UP);
            }
         } catch (Exception var4) {
            logger.debug("充值单不存在或暂不可复用USDT金额 orderNo={}", orderNo);
         }

         return null;
      } else {
         BaseOrder order = this.orderApi.getByOrderNo(orderNo);
         return order != null
               && Integer.valueOf(0).equals(order.getOrderStatus())
               && order.getExpectedAmountUsdt() != null
               && order.getExpectedAmountUsdt().compareTo(BigDecimal.ZERO) > 0
            ? order.getExpectedAmountUsdt().setScale(4, RoundingMode.HALF_UP)
            : null;
      }
   }

   private BigDecimal generateUniqueExpectedUsdt(BigDecimal totalAmount, BigDecimal rate) {
      BigDecimal baseUsdt = totalAmount.divide(rate, 2, RoundingMode.HALF_UP);
      Set<String> used = this.collectPendingExpectedUsdt();

      for (int i = 0; i < 80; i++) {
         int tail = ThreadLocalRandom.current().nextInt(1, 100);
         BigDecimal amount = baseUsdt.add(BigDecimal.valueOf(tail, 4)).setScale(4, RoundingMode.HALF_UP);
         if (!used.contains(amount.toPlainString())) {
            return amount;
         }
      }

      int fallback = (int)(System.nanoTime() % 9999L) + 1;
      return baseUsdt.add(BigDecimal.valueOf(fallback, 4)).setScale(4, RoundingMode.HALF_UP);
   }

   private Set<String> collectPendingExpectedUsdt() {
      Set<String> used = new HashSet<>();
      Date afterTime = this.resolvePendingAfterTime();
      List<Recharge> recharges = this.rechargeService.listByStatusAndPayTypeAndCreateTimeAfter(1, PayTypeEnum.USDT.getCode(), afterTime);
      if (recharges != null) {
         for (Recharge recharge : recharges) {
            if (recharge != null && recharge.getExpectedUsdtAmount() != null && recharge.getExpectedUsdtAmount().compareTo(BigDecimal.ZERO) > 0) {
               used.add(recharge.getExpectedUsdtAmount().setScale(4, RoundingMode.HALF_UP).toPlainString());
            }
         }
      }

      List<BaseOrder> orders = this.orderApi.queryUnpaidOrder();
      if (orders != null) {
         for (BaseOrder order : orders) {
            if (order != null
               && this.isStillPending(order.getOrderCreateTime(), afterTime)
               && order.getExpectedAmountUsdt() != null
               && order.getExpectedAmountUsdt().compareTo(BigDecimal.ZERO) > 0) {
               used.add(order.getExpectedAmountUsdt().setScale(4, RoundingMode.HALF_UP).toPlainString());
            }
         }
      }

      return used;
   }

   private Date resolvePendingAfterTime() {
      long expireMs = 1800000L;
      String expireMinutes = this.devConfigApi.getValueByKey("PAYMENT_ORDER_EXPIRATION_TIME");
      if (StrUtil.isNotBlank(expireMinutes)) {
         try {
            expireMs = Long.parseLong(expireMinutes.trim()) * 60L * 1000L;
         } catch (NumberFormatException var5) {
            expireMs = 1800000L;
         }
      }

      return new Date(System.currentTimeMillis() - expireMs);
   }

   private boolean isStillPending(Date createTime, Date afterTime) {
      return createTime == null || !createTime.before(afterTime);
   }

   @Override
   public CommonResult confirmUsdtPayment(String orderNo, Date payTime, String txHash) {
      if (orderNo != null && !orderNo.isEmpty() && txHash != null && !txHash.isEmpty()) {
         long count = this.paymentsRecordMapper.selectCount((Wrapper<PaymentsRecord>)new LambdaQueryWrapper<PaymentsRecord>().eq(PaymentsRecord::getTradeNo, txHash));
         if (count > 0L) {
            logger.info("USDT 交易已处理过，跳过 txHash={}", txHash);
            return CommonResult.ok();
         } else {
            try {
               Recharge recharge = this.rechargeService.queryEntity(orderNo);
               if (recharge.getStatus() != null && recharge.getStatus() == 1) {
                  this.alipayService.rechargeSuccess(orderNo, payTime, txHash);
                  this.savePaymentRecord(recharge.getMemberNo(), "用户充值", orderNo, txHash, recharge.getAmount(), PayTypeEnum.USDT.getCode());
                  logger.info("USDT 充值确认成功 orderNo={}", orderNo);
                  return CommonResult.ok();
               }
            } catch (Exception var7) {
            }

            BaseOrder order = this.orderApi.getByOrderNo(orderNo);
            if (order != null && order.getOrderStatus() != null && order.getOrderStatus() == 0) {
               this.savePaymentRecord(
                  order.getMemberNo(),
                  "订单支付",
                  orderNo,
                  txHash,
                  order.getOrderPayPrice() != null ? order.getOrderPayPrice() : BigDecimal.ZERO,
                  PayTypeEnum.USDT.getCode()
               );
               this.publishOrderEvent(orderNo, payTime, txHash, PayTypeEnum.USDT.getCode());
               logger.info("USDT 订单支付确认成功 orderNo={}", orderNo);
               return CommonResult.ok();
            } else {
               logger.warn("USDT 到账确认未匹配到待支付记录 orderNo={}", orderNo);
               return CommonResult.error("未找到待支付的充值或订单");
            }
         }
      } else {
         return CommonResult.error("参数不能为空");
      }
   }

   @Override
   public List<PendingUsdtPaymentDto> getPendingUsdtPayments() {
      String rateStr = this.devConfigApi.getValueByKey("USDT_PAY_RATE");
      if (rateStr != null && !rateStr.isBlank()) {
         BigDecimal rate;
         try {
            rate = new BigDecimal(rateStr.trim());
         } catch (NumberFormatException var11) {
            return Collections.emptyList();
         }

         if (rate.compareTo(BigDecimal.ZERO) <= 0) {
            return Collections.emptyList();
         } else {
            List<PendingUsdtPaymentDto> list = new ArrayList<>();
            Date afterTime = this.resolvePendingAfterTime();

            for (Recharge r : this.rechargeService.listByStatusAndPayTypeAndCreateTimeAfter(1, PayTypeEnum.USDT.getCode(), afterTime)) {
               PendingUsdtPaymentDto dto = new PendingUsdtPaymentDto();
               dto.setOrderNo(r.getId());
               dto.setExpectedAmountUsdt(
                  r.getExpectedUsdtAmount() != null
                     ? r.getExpectedUsdtAmount()
                     : (r.getAmount() != null ? r.getAmount().divide(rate, 4, RoundingMode.HALF_UP) : BigDecimal.ZERO)
               );
               dto.setCreateTime(r.getCreateTime());
               list.add(dto);
            }

            for (BaseOrder o : this.orderApi.queryUnpaidOrder()) {
               if (this.isStillPending(o.getOrderCreateTime(), afterTime)) {
                  boolean isUsdtOrder = o.getOrderPayType() != null && o.getOrderPayType() == PayTypeEnum.USDT.getCode()
                     || o.getExpectedAmountUsdt() != null && o.getExpectedAmountUsdt().compareTo(BigDecimal.ZERO) > 0;
                  if (isUsdtOrder) {
                     PendingUsdtPaymentDto dto = new PendingUsdtPaymentDto();
                     dto.setOrderNo(o.getOrderNo());
                     dto.setExpectedAmountUsdt(
                        o.getExpectedAmountUsdt() != null
                           ? o.getExpectedAmountUsdt()
                           : (o.getOrderPayPrice() != null ? o.getOrderPayPrice().divide(rate, 4, RoundingMode.HALF_UP) : BigDecimal.ZERO)
                     );
                     dto.setCreateTime(o.getOrderCreateTime());
                     list.add(dto);
                  }
               }
            }

            return list;
         }
      } else {
         return Collections.emptyList();
      }
   }

   private void savePaymentRecord(Long memberNo, String subject, String orderNo, String outTradeNo, BigDecimal totalAmount, Integer payType) {
      PaymentsRecord record = new PaymentsRecord();
      record.setMemberNo(memberNo);
      record.setOrderNo(orderNo);
      record.setTradeNo(outTradeNo);
      record.setBusinessType(subject);
      record.setRemark(subject);
      record.setPayTime(new Date());
      record.setPayAmount(totalAmount);
      record.setPayType(payType);
      MemberBalance mb = this.memberBalanceService.selectByMemberNo(memberNo);
      record.setMemberBalanceAfter(mb != null ? mb.getBalance() : null);
      this.paymentsRecordMapper.insert(record);
   }

   private void publishOrderEvent(String outTradeNo, Date payTime, String txHash, Integer payType) {
      try {
         logger.info("订单支付事件发布中，订单号:{}", outTradeNo);
         OrderEvent event = new OrderEvent(this, outTradeNo, payTime, txHash, payType);
         this.applicationContext.publishEvent(event);
         logger.info("订单支付事件发布成功，订单号:{}", outTradeNo);
      } catch (Exception var6) {
         logger.error("订单支付事件发布异常，订单号:{}", outTradeNo, var6);
      }
   }
}
