package com.rxinyun.admin.business.modular.esc.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.business.modular.esc.entity.ServerStrategy;
import com.rxinyun.admin.business.modular.esc.param.ServerStrategyAddParam;
import com.rxinyun.admin.business.modular.esc.param.ServerStrategyEditParam;
import com.rxinyun.admin.business.modular.esc.param.ServerStrategyIdParam;
import com.rxinyun.admin.business.modular.esc.param.ServerStrategyPageParam;
import com.rxinyun.admin.business.modular.esc.service.ServerStrategyService;
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
   name = "云服务器策略管理控制器"
)
@RestController
@RequestMapping({"/admin/server/strategy"})
@Validated
public class ServerStrategyController {
   @Resource
   private ServerStrategyService serverStrategyService;

   @Operation(
      summary = "查询云服务器策略分页"
   )
   @GetMapping({"/page"})
   public CommonResult<Page<ServerStrategy>> page(ServerStrategyPageParam pageParam) {
      return CommonResult.data(this.serverStrategyService.page(pageParam));
   }

   @Operation(
      summary = "新增云服务器策略"
   )
   @PostMapping({"/add"})
   public CommonResult<ServerStrategy> add(@RequestBody ServerStrategyAddParam serverStrategyAddParam) {
      return this.serverStrategyService.add(serverStrategyAddParam);
   }

   @Operation(
      summary = "查询云服务器策略详情"
   )
   @GetMapping({"/detail"})
   public CommonResult<ServerStrategy> detail(@Valid ServerStrategyIdParam serverStrategyIdParam) {
      return CommonResult.data(this.serverStrategyService.detail(serverStrategyIdParam));
   }

   @Operation(
      summary = "编辑云服务器策略"
   )
   @CommonLog("编辑云服务器策略")
   @PostMapping({"/edit"})
   public CommonResult<String> edit(@RequestBody ServerStrategyEditParam serverStrategyEditParam) {
      this.serverStrategyService.edit(serverStrategyEditParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "删除云服务器策略"
   )
   @CommonLog("删除云服务器策略")
   @PostMapping({"/delete"})
   public CommonResult delete(@RequestBody ServerStrategyIdParam serverStrategyIdParam) {
      this.serverStrategyService.delete(serverStrategyIdParam);
      return CommonResult.ok();
   }
}
