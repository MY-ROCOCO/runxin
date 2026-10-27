package com.rxinyun.member.payments.modular.payments.service;

import com.rxinyun.common.pojo.CommonResult;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;

public interface WechatPayService {
   CommonResult pay(String platform, String subject, Long memberNo, String orderNo, BigDecimal totalAmount);

   CommonResult notify(HttpServletRequest request) throws IOException, ParseException;
}
