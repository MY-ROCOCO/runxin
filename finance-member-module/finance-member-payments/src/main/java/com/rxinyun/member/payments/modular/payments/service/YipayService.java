package com.rxinyun.member.payments.modular.payments.service;

import com.rxinyun.common.pojo.CommonResult;
import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

public interface YipayService {
   CommonResult pay(Integer payType, String subject, Long memberNo, String orderNo, BigDecimal totalAmount);

   String notify(HttpServletRequest request);

   boolean isRouteEnabled(Integer payType);
}
