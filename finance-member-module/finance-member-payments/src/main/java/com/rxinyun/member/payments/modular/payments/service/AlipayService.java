package com.rxinyun.member.payments.modular.payments.service;

import com.rxinyun.common.pojo.CommonResult;
import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;

public interface AlipayService {
   CommonResult pay(String platform, String subject, Long memberNo, String orderNo, BigDecimal totalAmount);

   String notify(HttpServletRequest request);

   void rechargeSuccess(String outTradeNo, Date payTime, String alipayPayNo);
}
