package com.rxinyun.admin.production.modular.independent.machine.controller;

import com.rxinyun.admin.production.modular.independent.machine.entity.MachineLine;
import com.rxinyun.admin.production.modular.independent.machine.service.MachineLineService;
import com.rxinyun.common.annotation.CommonLog;
import com.rxinyun.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "独立机型绑定线路关系管理控制器"
)
@RestController
@RequestMapping({"/admin/independent/machine/line"})
public class MachineLineController {
   @Resource
   MachineLineService machineLineService;

   @CommonLog("根据机型ID查询关联的线路")
   @Operation(
      summary = "根据机型ID查询关联的线路"
   )
   @GetMapping({"/find/{id}"})
   public CommonResult<List<MachineLine>> findLineMachine(@PathVariable @NotBlank String id) {
      return CommonResult.data(this.machineLineService.findLineMachine(id));
   }
}
