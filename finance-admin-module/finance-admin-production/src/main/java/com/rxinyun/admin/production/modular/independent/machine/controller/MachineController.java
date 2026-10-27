package com.rxinyun.admin.production.modular.independent.machine.controller;

import com.rxinyun.admin.production.modular.independent.machine.param.MachineAddParam;
import com.rxinyun.admin.production.modular.independent.machine.param.MachineEditParam;
import com.rxinyun.admin.production.modular.independent.machine.param.MachinePageParam;
import com.rxinyun.admin.production.modular.independent.machine.param.MachineVO;
import com.rxinyun.admin.production.modular.independent.machine.service.MachineService;
import com.rxinyun.common.annotation.CommonLog;
import com.rxinyun.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "独立机型管理控制器"
)
@RestController
@RequestMapping({"/admin/independent/machine"})
public class MachineController {
   @Resource
   private MachineService machineService;

   @CommonLog("添加机型")
   @Operation(
      summary = "添加机型"
   )
   @PostMapping({"/add"})
   public CommonResult add(@RequestBody MachineAddParam machineAddParam) {
      this.machineService.addMachine(machineAddParam);
      return CommonResult.ok();
   }

   @CommonLog("机型分页查询")
   @Operation(
      summary = "机型分页查询"
   )
   @GetMapping({"/page"})
   public CommonResult page(MachinePageParam machinePageParam) {
      return CommonResult.data(this.machineService.page(machinePageParam));
   }

   @CommonLog("修改机型")
   @Operation(
      summary = "修改机型"
   )
   @PostMapping({"/edit"})
   public CommonResult edit(@RequestBody MachineEditParam machineEditParam) {
      this.machineService.editMachine(machineEditParam);
      return CommonResult.ok();
   }

   @CommonLog("修改权值")
   @Operation(
      summary = "修改权值"
   )
   @PostMapping({"/editWeight"})
   public CommonResult editWeight(@NotBlank String id, @NotBlank String weight) {
      this.machineService.editMachineWeight(id, weight);
      return CommonResult.ok();
   }

   @CommonLog("查询机型")
   @Operation(
      summary = "查询机型"
   )
   @GetMapping({"/find/{id}"})
   public CommonResult<MachineVO> find(@PathVariable @NotBlank String id) {
      return CommonResult.data(this.machineService.findMachine(id));
   }

   @CommonLog("删除机型")
   @Operation(
      summary = "删除机型"
   )
   @DeleteMapping({"/delete/{id}"})
   public CommonResult delete(@PathVariable @NotBlank String id) {
      this.machineService.deleteMachine(id);
      return CommonResult.ok();
   }
}
