package com.rxinyun.member.workorder.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.common.annotation.CommonLog;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.member.workorder.pojo.dto.WorkOrderAddParam;
import com.rxinyun.member.workorder.pojo.dto.WorkOrderPageParam;
import com.rxinyun.member.workorder.pojo.vo.WorkOrderFindVO;
import com.rxinyun.member.workorder.pojo.vo.WorkOrderPageResponse;
import com.rxinyun.member.workorder.service.WorkOrderMemberService;
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
   name = "用户端工单管理控制器"
)
@RestController
@RequestMapping({"/member/workorder"})
public class WorkOrderMemberController {
   @Resource
   private WorkOrderMemberService workOrderMemberService;

   @CommonLog("用户提交工单")
   @Operation(
      summary = "用户提交工单"
   )
   @PostMapping({"/save"})
   public CommonResult save(@RequestBody WorkOrderAddParam workOrderAddParam) {
      return this.workOrderMemberService.add(workOrderAddParam);
   }

   @CommonLog("工单分页查询")
   @Operation(
      summary = "工单分页查询"
   )
   @GetMapping({"/page"})
   public CommonResult<Page<WorkOrderPageResponse>> page(WorkOrderPageParam workOrderPageParam) {
      return CommonResult.data(this.workOrderMemberService.page(workOrderPageParam));
   }

   @CommonLog("用户端设置工单已解决")
   @Operation(
      summary = "用户端设置工单已解决"
   )
   @PostMapping({"/setStatusOut/{id}"})
   public CommonResult setStatusOut(@PathVariable @NotBlank String id) {
      return this.workOrderMemberService.setStatusOut(id);
   }

   @CommonLog("用户端查询工单详情")
   @Operation(
      summary = "用户端查询工单详情"
   )
   @GetMapping({"/findMessage/{id}"})
   public CommonResult<WorkOrderFindVO> findMessage(@PathVariable @NotBlank String id) {
      return this.workOrderMemberService.getMessById(id);
   }
}
