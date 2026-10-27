package com.rxinyun.member.order.modular.invoice.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.common.annotation.CommonLog;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.member.order.modular.invoice.param.MemberInvoicableOrderPageParam;
import com.rxinyun.member.order.modular.invoice.param.MemberOrderInvoiceApplyParam;
import com.rxinyun.member.order.modular.invoice.param.MemberOrderInvoiceIdParam;
import com.rxinyun.member.order.modular.invoice.param.MemberOrderInvoicePageParam;
import com.rxinyun.member.order.modular.invoice.response.MemberInvoicableOrderResponse;
import com.rxinyun.member.order.modular.invoice.response.MemberOrderInvoiceResponse;
import com.rxinyun.member.order.modular.invoice.response.MemberOrderInvoiceTitleResponse;
import com.rxinyun.member.order.modular.invoice.service.MemberOrderInvoiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "会员订单发票"
)
@RestController
@RequestMapping({"/member"})
@Validated
public class MemberOrderInvoiceController {
   @Resource
   private MemberOrderInvoiceService memberOrderInvoiceService;

   @Operation(
      summary = "可开票订单分页"
   )
   @GetMapping({"/order/invoice/available/page"})
   public CommonResult<Page<MemberInvoicableOrderResponse>> availablePage(MemberInvoicableOrderPageParam pageParam) {
      return CommonResult.data(this.memberOrderInvoiceService.availablePage(pageParam));
   }

   @Operation(
      summary = "最近一次开票抬头"
   )
   @GetMapping({"/order/invoice/last"})
   public CommonResult<MemberOrderInvoiceTitleResponse> lastTitle() {
      return CommonResult.data(this.memberOrderInvoiceService.lastTitle());
   }

   @Operation(
      summary = "申请开票"
   )
   @CommonLog("会员申请开票")
   @PostMapping({"/order/invoice/apply"})
   public CommonResult<MemberOrderInvoiceResponse> apply(@Valid @RequestBody MemberOrderInvoiceApplyParam param) {
      return CommonResult.data(this.memberOrderInvoiceService.apply(param));
   }

   @Operation(
      summary = "我的发票分页"
   )
   @GetMapping({"/order/invoice/page"})
   public CommonResult<Page<MemberOrderInvoiceResponse>> page(MemberOrderInvoicePageParam pageParam) {
      return CommonResult.data(this.memberOrderInvoiceService.page(pageParam));
   }

   @Operation(
      summary = "我的发票详情"
   )
   @GetMapping({"/order/invoice/detail"})
   public CommonResult<MemberOrderInvoiceResponse> detail(@Valid MemberOrderInvoiceIdParam param) {
      return CommonResult.data(this.memberOrderInvoiceService.detail(param));
   }
}
