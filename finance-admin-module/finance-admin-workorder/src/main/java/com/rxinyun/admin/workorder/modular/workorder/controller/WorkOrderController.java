package com.rxinyun.admin.workorder.modular.workorder.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.workorder.modular.workorder.entity.param.WorkOrderPageParam;
import com.rxinyun.admin.workorder.modular.workorder.response.WorkOrderFindResponse;
import com.rxinyun.admin.workorder.modular.workorder.response.WorkOrderPageResponse;
import com.rxinyun.admin.workorder.modular.workorder.service.WorkOrderService;
import com.rxinyun.common.annotation.CommonLog;
import com.rxinyun.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "工单管理控制器"
)
@RestController
@RequestMapping({"/admin/workorder"})
public class WorkOrderController {
   @Resource
   private WorkOrderService workOrderService;

   @CommonLog("查询工单详情")
   @Operation(
      summary = "查询工单详情"
   )
   @PostMapping({"/findMessage/{id}"})
   public CommonResult<WorkOrderFindResponse> findMessage(@PathVariable @NotBlank String id) {
      return this.workOrderService.findMessage(id);
   }

   @CommonLog("设置为已解决")
   @Operation(
      summary = "设置为已解决"
   )
   @PostMapping({"/setStatusOut/{id}"})
   public CommonResult setStatusOut(@PathVariable @NotBlank String id) {
      return this.workOrderService.setStatusOut(id);
   }

   @CommonLog("工单分页查询")
   @Operation(
      summary = "工单分页查询"
   )
   @GetMapping({"/page"})
   public CommonResult<Page<WorkOrderPageResponse>> page(WorkOrderPageParam workOrderPageParam) {
      return CommonResult.data(this.workOrderService.page(workOrderPageParam));
   }
}
