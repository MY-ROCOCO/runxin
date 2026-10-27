package com.rxinyun.payments.api;

import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.payments.api.pojo.PendingUsdtPaymentDto;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface PaymentsApi {
   CommonResult pay(Integer payType, String platform, String subject, Long memberNo, String orderNo, BigDecimal totalAmount);

   CommonResult confirmUsdtPayment(String orderNo, Date payTime, String txHash);

   List<PendingUsdtPaymentDto> getPendingUsdtPayments();
}
