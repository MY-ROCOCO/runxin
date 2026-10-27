package com.rxinyun.member.payments.modular.payments.service;

import com.alipay.api.AlipayApiException;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.member.payments.modular.payments.entity.PaymentsRecord;
import com.rxinyun.member.payments.modular.payments.param.BalanceRechargeParam;
import com.rxinyun.member.payments.modular.payments.param.PaymentsRecordPageParam;
import com.rxinyun.payments.api.pojo.PendingUsdtPaymentDto;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

public interface PaymentsService {
   Page<PaymentsRecord> page(PaymentsRecordPageParam param);

   CommonResult recharge(BalanceRechargeParam balanceRechargeParam);

   CommonResult pay(Integer payType, String platform, String subject, Long memberNo, String orderNo, BigDecimal totalAmount);

   String alipayNotify(HttpServletRequest request) throws AlipayApiException;

   CommonResult wechatPayNotify(HttpServletRequest request) throws IOException, ParseException;

   String yipayNotify(HttpServletRequest request);

   CommonResult confirmUsdtPayment(String orderNo, Date payTime, String txHash);

   List<PendingUsdtPaymentDto> getPendingUsdtPayments();
}
