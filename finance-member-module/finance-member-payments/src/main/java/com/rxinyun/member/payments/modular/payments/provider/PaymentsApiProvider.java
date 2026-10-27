package com.rxinyun.member.payments.modular.payments.provider;

import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.member.payments.modular.payments.service.PaymentsService;
import com.rxinyun.payments.api.PaymentsApi;
import com.rxinyun.payments.api.pojo.PendingUsdtPaymentDto;
import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PaymentsApiProvider implements PaymentsApi {
   private static final Logger log = LoggerFactory.getLogger(PaymentsApiProvider.class);
   @Resource
   private PaymentsService paymentsService;

   public CommonResult pay(Integer payType, String platform, String subject, Long memberNo, String orderNo, BigDecimal totalAmount) {
      return this.paymentsService.pay(payType, platform, subject, memberNo, orderNo, totalAmount);
   }

   public CommonResult confirmUsdtPayment(String orderNo, Date payTime, String txHash) {
      return this.paymentsService.confirmUsdtPayment(orderNo, payTime, txHash);
   }

   public List<PendingUsdtPaymentDto> getPendingUsdtPayments() {
      return this.paymentsService.getPendingUsdtPayments();
   }
}
