package com.rxinyun.admin.workorder.modular.workorder.controller;

import com.rxinyun.admin.workorder.modular.workorder.entity.param.WorkOrderMessageAddParam;
import com.rxinyun.admin.workorder.modular.workorder.service.WorkOrderMessageService;
import com.rxinyun.common.annotation.CommonLog;
import com.rxinyun.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "工单对话管理控制器"
)
@RestController
@RequestMapping({"/admin/workorder/message"})
public class WorkOrderMessageController {
   @Resource
   private WorkOrderMessageService workOrderMessageService;

   @CommonLog("管理员添加工单聊天信息")
   @Operation(
      summary = "管理员添加工单聊天信息"
   )
   @PostMapping({"/add"})
   public CommonResult add(@RequestBody WorkOrderMessageAddParam workOrderMessageAddParam) {
      return this.workOrderMessageService.add(workOrderMessageAddParam);
   }

   @CommonLog("管理员获取聊天记录")
   @Operation(
      summary = "管理员获取聊天记录"
   )
   @GetMapping({"/getMessage/{workOrderId}"})
   public CommonResult getMessage(@PathVariable @NotBlank String workOrderId) {
      return CommonResult.data(this.workOrderMessageService.getMessage(workOrderId));
   }
}
