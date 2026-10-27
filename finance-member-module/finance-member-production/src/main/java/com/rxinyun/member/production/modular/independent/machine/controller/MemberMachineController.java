package com.rxinyun.member.production.modular.independent.machine.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.common.annotation.CommonLog;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.member.production.modular.independent.machine.entity.MemberMachine;
import com.rxinyun.member.production.modular.independent.machine.param.MemberMachineIdParam;
import com.rxinyun.member.production.modular.independent.machine.param.MemberMachineLineQueryParam;
import com.rxinyun.member.production.modular.independent.machine.param.MemberMachinePageParam;
import com.rxinyun.member.production.modular.independent.machine.param.MemberMachineVO;
import com.rxinyun.member.production.modular.independent.machine.service.MemberMachineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "独立机型管理控制器"
)
@RestController
@RequestMapping({"/member/independent/machine"})
public class MemberMachineController {
   @Resource
   private MemberMachineService memberMachineService;

   @CommonLog("机型分页查询")
   @Operation(
      summary = "机型分页查询"
   )
   @GetMapping({"/page"})
   public CommonResult<Page<MemberMachineVO>> page(MemberMachinePageParam machinePageParam) {
      return CommonResult.data(this.memberMachineService.page(machinePageParam));
   }

   @CommonLog("根据线路ID查询机型")
   @Operation(
      summary = "根据线路ID查询机型"
   )
   @GetMapping({"/find/line"})
   public CommonResult<List<MemberMachine>> findByLine(MemberMachineLineQueryParam queryParam) {
      return CommonResult.data(this.memberMachineService.findByLine(queryParam));
   }

   @CommonLog("查询机型详情")
   @Operation(
      summary = "查询机型详情"
   )
   @GetMapping({"/detail"})
   public CommonResult<MemberMachineVO> detail(MemberMachineIdParam machineIdParam) {
      return CommonResult.data(this.memberMachineService.findMachine(machineIdParam));
   }
}
