package com.rxinyun.admin.order.modular.invoice.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.order.modular.invoice.param.AdminOrderInvoiceIdParam;
import com.rxinyun.admin.order.modular.invoice.param.AdminOrderInvoicePageParam;
import com.rxinyun.admin.order.modular.invoice.param.AdminOrderInvoiceRejectParam;
import com.rxinyun.admin.order.modular.invoice.response.AdminOrderInvoiceResponse;
import com.rxinyun.admin.order.modular.invoice.service.OrderInvoiceService;
import com.rxinyun.common.annotation.CommonLog;
import com.rxinyun.common.pojo.CommonResult;
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
   name = "订单发票审核"
)
@RestController
@RequestMapping({"/admin"})
@Validated
public class AdminOrderInvoiceController {
   @Resource
   private OrderInvoiceService orderInvoiceService;

   @Operation(
      summary = "发票申请分页"
   )
   @GetMapping({"/order/invoice/page"})
   public CommonResult<Page<AdminOrderInvoiceResponse>> page(AdminOrderInvoicePageParam pageParam) {
      return CommonResult.data(this.orderInvoiceService.page(pageParam));
   }

   @Operation(
      summary = "发票申请详情"
   )
   @GetMapping({"/order/invoice/detail"})
   public CommonResult<AdminOrderInvoiceResponse> detail(@Valid AdminOrderInvoiceIdParam param) {
      return CommonResult.data(this.orderInvoiceService.detail(param));
   }

   @Operation(
      summary = "开具发票"
   )
   @CommonLog("开具发票")
   @PostMapping({"/order/invoice/approve"})
   public CommonResult<String> approve(@Valid @RequestBody AdminOrderInvoiceIdParam param) {
      this.orderInvoiceService.approve(param);
      return CommonResult.ok();
   }

   @Operation(
      summary = "拒绝发票申请"
   )
   @CommonLog("拒绝发票申请")
   @PostMapping({"/order/invoice/reject"})
   public CommonResult<String> reject(@Valid @RequestBody AdminOrderInvoiceRejectParam param) {
      this.orderInvoiceService.reject(param);
      return CommonResult.ok();
   }
}
