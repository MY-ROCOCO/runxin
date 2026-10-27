package com.rxinyun.member.payments.modular.payments.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.common.annotation.CommonLog;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.member.payments.modular.payments.entity.PaymentsRecord;
import com.rxinyun.member.payments.modular.payments.param.BalanceRechargeParam;
import com.rxinyun.member.payments.modular.payments.param.PaymentsRecordPageParam;
import com.rxinyun.member.payments.modular.payments.service.PaymentsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "订单支付"
)
@RestController
@RequestMapping({"/member/pay"})
@Validated
public class PaymentsController {
   private static final Logger log = LoggerFactory.getLogger(PaymentsController.class);
   @Resource
   private PaymentsService paymentsService;

   @Operation(
      summary = "获取收支明细分页"
   )
   @GetMapping({"/page"})
   public CommonResult<Page<PaymentsRecord>> page(PaymentsRecordPageParam param) {
      return CommonResult.data(this.paymentsService.page(param));
   }

   @Operation(
      summary = "用户充值"
   )
   @PostMapping({"/recharge"})
   public CommonResult recharge(@RequestBody BalanceRechargeParam balanceRechargeParam) {
      return this.paymentsService.recharge(balanceRechargeParam);
   }

   @CommonLog("支付宝支付回调")
   @Operation(
      summary = "支付宝请求回调"
   )
   @PostMapping({"/alipay/notify"})
   public String alipayNotify(HttpServletRequest request) throws Exception {
      log.info("支付请求回调：{}", request);
      return this.paymentsService.alipayNotify(request);
   }

   @CommonLog("微信支付回调")
   @Operation(
      summary = "微信请求回调"
   )
   @PostMapping({"/wechat/notify"})
   public CommonResult wechatPayNotify(HttpServletRequest request) throws Exception {
      log.info("支付请求回调：{}", request);
      return this.paymentsService.wechatPayNotify(request);
   }

   @CommonLog("易支付回调")
   @Operation(
      summary = "易支付请求回调"
   )
   @PostMapping({"/yipay/notify"})
   public String yipayNotify(HttpServletRequest request) {
      log.info("易支付请求回调：{}", request);
      return this.paymentsService.yipayNotify(request);
   }
}
