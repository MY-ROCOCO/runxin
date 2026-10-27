package com.rxinyun.admin.dev.modular.update.controller;

import com.rxinyun.admin.dev.modular.update.param.CheckUpdateParam;
import com.rxinyun.admin.dev.modular.update.param.DownloadParam;
import com.rxinyun.admin.dev.modular.update.param.InstallParam;
import com.rxinyun.admin.dev.modular.update.result.DownloadResult;
import com.rxinyun.admin.dev.modular.update.result.InstallResult;
import com.rxinyun.admin.dev.modular.update.result.UpdateCheckResult;
import com.rxinyun.admin.dev.modular.update.result.UpdateProgressResult;
import com.rxinyun.admin.dev.modular.update.service.UpdateService;
import com.rxinyun.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "系统更新控制器"
)
@RestController
@RequestMapping({"/admin/dev/update"})
public class UpdateController {
   private final UpdateService updateService;

   @Operation(
      summary = "检查更新"
   )
   @PostMapping({"/check"})
   public CommonResult<UpdateCheckResult> checkUpdate(@RequestBody @Validated CheckUpdateParam param) {
      return CommonResult.data(this.updateService.checkUpdate(param));
   }

   @Operation(
      summary = "下载更新包"
   )
   @PostMapping({"/download"})
   public CommonResult<DownloadResult> download(@RequestBody @Validated DownloadParam param) {
      return CommonResult.data(this.updateService.download(param));
   }

   @Operation(
      summary = "安装更新"
   )
   @PostMapping({"/install"})
   public CommonResult<InstallResult> install(@RequestBody @Validated InstallParam param) {
      return CommonResult.data(this.updateService.install(param));
   }

   @Operation(
      summary = "获取更新进度"
   )
   @GetMapping({"/progress"})
   public CommonResult<UpdateProgressResult> getUpdateProgress() {
      return CommonResult.data(this.updateService.getUpdateProgress());
   }

   @Operation(
      summary = "获取更新状态"
   )
   @GetMapping({"/status"})
   public CommonResult<Map<String, Object>> getUpdateStatus() {
      Map<String, Object> data = new LinkedHashMap<>();
      data.put("status", "UP");
      data.put("message", "服务可用");
      data.put("timestamp", System.currentTimeMillis());
      return CommonResult.data(data);
   }

   public UpdateController(final UpdateService updateService) {
      this.updateService = updateService;
   }
}
