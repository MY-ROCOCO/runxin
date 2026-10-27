package com.rxinyun.admin.dev.modular.config.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.dev.modular.config.entity.DevConfig;
import com.rxinyun.admin.dev.modular.config.param.DevConfigAddParam;
import com.rxinyun.admin.dev.modular.config.param.DevConfigBatchParam;
import com.rxinyun.admin.dev.modular.config.param.DevConfigEditParam;
import com.rxinyun.admin.dev.modular.config.param.DevConfigIdParam;
import com.rxinyun.admin.dev.modular.config.param.DevConfigKeyEditParam;
import com.rxinyun.admin.dev.modular.config.param.DevConfigListParam;
import com.rxinyun.admin.dev.modular.config.param.DevConfigPageParam;
import com.rxinyun.admin.dev.modular.config.service.DevConfigService;
import com.rxinyun.common.annotation.CommonLog;
import com.rxinyun.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "配置控制器"
)
@RestController
@Validated
public class DevConfigController {
   @Resource
   private DevConfigService devConfigService;

   @Operation(
      summary = "获取配置分页"
   )
   @GetMapping({"/admin/dev/config/page"})
   public CommonResult<Page<DevConfig>> page(DevConfigPageParam devConfigPageParam) {
      return CommonResult.data(this.devConfigService.page(devConfigPageParam));
   }

   @Operation(
      summary = "获取系统基础配置"
   )
   @GetMapping({"/admin/dev/config/sysBaseList"})
   public CommonResult<List<DevConfig>> sysBaseList() {
      return CommonResult.data(this.devConfigService.sysBaseList());
   }

   @Operation(
      summary = "获取配置列表"
   )
   @GetMapping({"/admin/dev/config/list"})
   public CommonResult<List<DevConfig>> list(DevConfigListParam devConfigListParam) {
      return CommonResult.data(this.devConfigService.list(devConfigListParam));
   }

   @Operation(
      summary = "添加配置"
   )
   @CommonLog("添加配置")
   @PostMapping({"/admin/dev/config/add"})
   public CommonResult<String> add(@RequestBody @Valid DevConfigAddParam devConfigAddParam) {
      this.devConfigService.add(devConfigAddParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "编辑配置"
   )
   @CommonLog("编辑配置")
   @PostMapping({"/admin/dev/config/edit"})
   public CommonResult<String> edit(@RequestBody @Valid DevConfigEditParam devConfigEditParam) {
      this.devConfigService.edit(devConfigEditParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "根据KEY修改配置"
   )
   @CommonLog("根据KEY修改配置")
   @PostMapping({"/admin/dev/config/updateByKey"})
   public CommonResult<String> updateByConfigKey(@RequestBody @Valid DevConfigKeyEditParam devConfigKeyEditParam) {
      this.devConfigService.updateByConfigKey(devConfigKeyEditParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "删除配置"
   )
   @CommonLog("删除配置")
   @PostMapping({"/admin/dev/config/delete"})
   public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空") List<DevConfigIdParam> devConfigIdParamList) {
      this.devConfigService.delete(devConfigIdParamList);
      return CommonResult.ok();
   }

   @Operation(
      summary = "获取配置详情"
   )
   @GetMapping({"/admin/dev/config/detail"})
   public CommonResult<DevConfig> detail(@Valid DevConfigIdParam devConfigIdParam) {
      return CommonResult.data(this.devConfigService.detail(devConfigIdParam));
   }

   @Operation(
      summary = "配置批量更新"
   )
   @CommonLog("配置批量更新")
   @PostMapping({"/admin/dev/config/editBatch"})
   public CommonResult<String> editBatch(@RequestBody @Valid @NotEmpty(message = "集合不能为空") List<DevConfigBatchParam> devConfigBatchParamList) {
      this.devConfigService.editBatch(devConfigBatchParamList);
      return CommonResult.ok();
   }
}
