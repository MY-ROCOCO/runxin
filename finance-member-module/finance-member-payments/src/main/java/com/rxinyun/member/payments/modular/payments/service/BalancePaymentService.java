package com.rxinyun.member.payments.modular.payments.service;

import com.rxinyun.common.pojo.CommonResult;
import java.math.BigDecimal;

public interface BalancePaymentService {
   CommonResult pay(String subject, Long memberNo, String orderNo, BigDecimal totalAmount, String businessType);
}
