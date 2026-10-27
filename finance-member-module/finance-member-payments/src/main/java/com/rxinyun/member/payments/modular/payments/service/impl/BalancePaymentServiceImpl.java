package com.rxinyun.member.payments.modular.payments.service.impl;

import com.rxinyun.common.cache.CommonCacheOperator;
import com.rxinyun.common.event.OrderEvent;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.member.auth.modular.user.entity.MemberBalance;
import com.rxinyun.member.auth.modular.user.service.MemberBalanceService;
import com.rxinyun.member.core.util.StpClientLoginUserUtil;
import com.rxinyun.member.member.pojo.SaBaseClientLoginUser;
import com.rxinyun.member.payments.modular.payments.entity.PaymentsRecord;
import com.rxinyun.member.payments.modular.payments.mapper.PaymentsRecordMapper;
import com.rxinyun.member.payments.modular.payments.service.BalancePaymentService;
import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class BalancePaymentServiceImpl implements BalancePaymentService {
   private static final Logger log = LoggerFactory.getLogger(BalancePaymentServiceImpl.class);
   @Autowired
   private ApplicationContext applicationContext;
   @Autowired
   private PaymentsRecordMapper paymentsRecordMapper;
   @Autowired
   private MemberBalanceService memberBalanceService;
   @Autowired
   private CommonCacheOperator commonCacheOperator;

   @Override
   public CommonResult pay(String subject, Long memberNo, String orderNo, BigDecimal totalAmount, String businessType) {
      long startTime = System.currentTimeMillis();
      SaBaseClientLoginUser clientLoginUser = StpClientLoginUserUtil.getClientLoginUser();
      String lockKey = "balance_lock:" + clientLoginUser.getId();
      String requestId = UUID.randomUUID().toString();
      int expireTime = 10000;

      CommonResult var16;
      try {
         log.info("开始处理余额支付，订单号: {}", orderNo);
         if (totalAmount == null || totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return CommonResult.error("支付金额不合法");
         }

         if (!this.commonCacheOperator.tryGetDistributedLock(lockKey, requestId, expireTime)) {
            log.error("获取锁失败，用户ID: {}", clientLoginUser.getId());
            return CommonResult.error("操作繁忙，请稍后再试");
         }

         MemberBalance memberBalance = this.memberBalanceService.selectByMemberNo(clientLoginUser.getId());
         if (memberBalance == null) {
            log.error("用户余额记录不存在，用户ID: {}", clientLoginUser.getId());
            return CommonResult.error("用户余额记录不存在");
         }

         if (memberBalance.getBalance().compareTo(totalAmount) < 0) {
            log.error("用户余额不足，用户ID: {}, 当前余额: {}, 支付金额: {}", new Object[]{clientLoginUser.getId(), memberBalance.getBalance(), totalAmount});
            return CommonResult.error("用户余额不足");
         }

         memberBalance.setBalance(memberBalance.getBalance().subtract(totalAmount));
         this.memberBalanceService.edit(memberBalance);

         try {
            String paymentSerialNo = this.generatePaymentSerialNo();
            log.info("余额支付成功，用户ID: {}, 订单号: {}, 支付金额: {}", new Object[]{clientLoginUser.getId(), orderNo, totalAmount});
            this.savePaymentRecord(memberNo, subject, orderNo, paymentSerialNo, totalAmount, 4);
            OrderEvent event = new OrderEvent(this, orderNo, new Date(), paymentSerialNo, 4);
            this.applicationContext.publishEvent(event);
            log.info("已同步发布支付成功事件，订单号: {}", orderNo);
            long duration = System.currentTimeMillis() - startTime;
            log.info("余额支付处理完成，订单号: {}, 耗时: {}ms", orderNo, duration);
            return CommonResult.data(paymentSerialNo);
         } catch (Exception var21) {
            long durationx = System.currentTimeMillis() - startTime;
            log.error("余额支付处理异常，订单号: {}, 错误: {}, 耗时: {}ms", new Object[]{orderNo, var21.getMessage(), durationx});
            memberBalance.setBalance(memberBalance.getBalance().add(totalAmount));
            this.memberBalanceService.edit(memberBalance);
            var16 = CommonResult.error("支付处理失败，请稍后重试");
         }
      } finally {
         this.commonCacheOperator.releaseDistributedLock(lockKey, requestId);
      }

      return var16;
   }

   private String generatePaymentSerialNo() {
      return UUID.randomUUID().toString().replace("-", "");
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
      MemberBalance after = this.memberBalanceService.selectByMemberNo(memberNo);
      record.setMemberBalanceAfter(after != null ? after.getBalance() : null);
      this.paymentsRecordMapper.insert(record);
   }

   private void publishOrderEvent(String outTradeNo, Date payTime, String alipayPayNo, Integer payType) {
      try {
         log.info("订单支付事件发布中，订单号:{}", outTradeNo);
         OrderEvent event = new OrderEvent(this, outTradeNo, payTime, alipayPayNo, payType);
         this.applicationContext.publishEvent(event);
         log.info("订单支付事件发布成功，订单号:{}", outTradeNo);
      } catch (Exception var6) {
         log.error("订单支付事件发布异常，订单号:{}", outTradeNo);
      }
   }
}
