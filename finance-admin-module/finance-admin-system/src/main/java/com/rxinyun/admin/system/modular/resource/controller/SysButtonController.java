package com.rxinyun.admin.system.modular.resource.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.system.modular.resource.entity.SysButton;
import com.rxinyun.admin.system.modular.resource.param.button.SysButtonAddParam;
import com.rxinyun.admin.system.modular.resource.param.button.SysButtonEditParam;
import com.rxinyun.admin.system.modular.resource.param.button.SysButtonIdParam;
import com.rxinyun.admin.system.modular.resource.param.button.SysButtonPageParam;
import com.rxinyun.admin.system.modular.resource.service.SysButtonService;
import com.rxinyun.common.annotation.CommonLog;
import com.rxinyun.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "按钮控制器"
)
@RestController
@Validated
public class SysButtonController {
   @Resource
   private SysButtonService sysButtonService;

   @Operation(
      summary = "获取按钮分页"
   )
   @GetMapping({"/admin/sys/button/page"})
   public CommonResult<Page<SysButton>> page(SysButtonPageParam sysButtonPageParam) {
      return CommonResult.data(this.sysButtonService.page(sysButtonPageParam));
   }

   @Operation(
      summary = "添加按钮"
   )
   @CommonLog("添加按钮")
   @PostMapping({"/admin/sys/button/add"})
   public CommonResult<String> add(@RequestBody @Valid SysButtonAddParam sysButtonAddParam) {
      this.sysButtonService.add(sysButtonAddParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "编辑按钮"
   )
   @CommonLog("编辑按钮")
   @PostMapping({"/admin/sys/button/edit"})
   public CommonResult<String> edit(@RequestBody @Valid SysButtonEditParam sysButtonEditParam) {
      this.sysButtonService.edit(sysButtonEditParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "删除按钮"
   )
   @CommonLog("删除按钮")
   @PostMapping({"/admin/sys/button/delete"})
   public CommonResult<String> delete(@RequestBody @Valid List<SysButtonIdParam> sysButtonIdParamList) {
      this.sysButtonService.delete(sysButtonIdParamList);
      return CommonResult.ok();
   }

   @Operation(
      summary = "获取按钮详情"
   )
   @GetMapping({"/admin/sys/button/detail"})
   public CommonResult<SysButton> detail(@Valid SysButtonIdParam sysButtonIdParam) {
      return CommonResult.data(this.sysButtonService.detail(sysButtonIdParam));
   }
}
