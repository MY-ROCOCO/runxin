package com.rxinyun.admin.order.modular.ecs.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.order.modular.ecs.entity.Order;
import com.rxinyun.admin.order.modular.ecs.entity.ServerRefundRecord;
import com.rxinyun.admin.order.modular.ecs.param.OrderIdParam;
import com.rxinyun.admin.order.modular.ecs.param.OrderPageParam;
import com.rxinyun.admin.order.modular.ecs.param.ServerRefundParam;
import com.rxinyun.admin.order.modular.ecs.param.ServerRefundRecordIdParam;
import com.rxinyun.admin.order.modular.ecs.param.ServerRefundRecordPageParam;
import com.rxinyun.admin.order.modular.ecs.response.OrderResponse;
import com.rxinyun.admin.order.modular.ecs.response.ServerRefundPreviewResponse;
import com.rxinyun.admin.order.modular.ecs.service.OrderService;
import com.rxinyun.admin.order.modular.ecs.service.ServerRefundRecordService;
import com.rxinyun.admin.order.modular.ecs.service.ServerRefundService;
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
   name = "订单管理控制器"
)
@RestController
@RequestMapping({"/admin"})
@Validated
public class OrderController {
   @Resource
   private OrderService orderService;
   @Resource
   private ServerRefundService serverRefundService;
   @Resource
   private ServerRefundRecordService serverRefundRecordService;

   @Operation(
      summary = "获取订单分页"
   )
   @GetMapping({"order/page"})
   public CommonResult<Page<Order>> page(OrderPageParam pageParam) {
      return CommonResult.data(this.orderService.page(pageParam));
   }

   @Operation(
      summary = "获取服务器订单详情"
   )
   @GetMapping({"order/detail"})
   public CommonResult<OrderResponse> detail(@Valid OrderIdParam orderIdParam) {
      return this.orderService.detail(orderIdParam);
   }

   @Operation(
      summary = "退款订单计价"
   )
   @PostMapping({"/order/server/refund/preview"})
   public CommonResult<ServerRefundPreviewResponse> serverRefundPreview(@Valid @RequestBody ServerRefundParam param) {
      return this.serverRefundService.preview(param);
   }

   @Operation(
      summary = "订单退款"
   )
   @PostMapping({"/order/server/refund/execute"})
   public CommonResult<String> serverRefundExecute(@Valid @RequestBody ServerRefundParam param) {
      return this.serverRefundService.execute(param);
   }

   @Operation(
      summary = "退款申请记录分页"
   )
   @GetMapping({"/order/server/refund/record/page"})
   public CommonResult<Page<ServerRefundRecord>> serverRefundRecordPage(ServerRefundRecordPageParam pageParam) {
      return CommonResult.data(this.serverRefundRecordService.page(pageParam));
   }

   @Operation(
      summary = "审核退款申请（同意/拒绝）"
   )
   @PostMapping({"/order/server/refund/record/approve"})
   public CommonResult<String> approveRefundRecord(@Valid @RequestBody ServerRefundRecordIdParam param) {
      return this.serverRefundRecordService.approve(param);
   }
}
