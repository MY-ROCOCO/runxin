package com.rxinyun.member.workorder.controller;

import com.rxinyun.common.annotation.CommonLog;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.member.workorder.pojo.dto.WorkOrderMessageAddParam;
import com.rxinyun.member.workorder.service.WorkOrderMessageMemberService;
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
   name = "用户端工单聊天管理控制器"
)
@RestController
@RequestMapping({"/member/workorder/message"})
public class WorkOrderMessageMemberController {
   @Resource
   private WorkOrderMessageMemberService workOrderMessageMemberService;

   @CommonLog("用户添加工单聊天信息")
   @Operation(
      summary = "用户添加工单聊天信息"
   )
   @PostMapping({"/add"})
   public CommonResult add(@RequestBody WorkOrderMessageAddParam workOrderMessageAddParam) {
      return this.workOrderMessageMemberService.add(workOrderMessageAddParam);
   }

   @CommonLog("用户查询工单聊天信息")
   @Operation(
      summary = "用户查询工单聊天信息"
   )
   @GetMapping({"/findMessage/{workOrderId}"})
   public CommonResult findMessage(@PathVariable @NotBlank String workOrderId) {
      return CommonResult.data(this.workOrderMessageMemberService.findMessage(workOrderId));
   }
}
