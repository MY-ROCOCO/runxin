package com.rxinyun.member.workorder.controller;

import com.rxinyun.common.annotation.CommonLog;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.member.workorder.pojo.vo.WorkOrderTypeList;
import com.rxinyun.member.workorder.service.WorkOrderTypeMemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "用户端工单类型管理控制器"
)
@RestController
@RequestMapping({"/member/workorder/type"})
public class OrderTypeMemberController {
   @Resource
   private WorkOrderTypeMemberService workOrderTypeService;

   @CommonLog("查询下拉框可见工单类型")
   @Operation(
      summary = "查询下拉框可见工单类型"
   )
   @GetMapping({"/list"})
   public CommonResult<WorkOrderTypeList> getTypeName() {
      return this.workOrderTypeService.findList();
   }
}
