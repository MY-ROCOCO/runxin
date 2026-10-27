package com.rxinyun.admin.dev.modular.job.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.dev.modular.job.entity.DevJob;
import com.rxinyun.admin.dev.modular.job.param.DevJobAddParam;
import com.rxinyun.admin.dev.modular.job.param.DevJobEditParam;
import com.rxinyun.admin.dev.modular.job.param.DevJobIdParam;
import com.rxinyun.admin.dev.modular.job.param.DevJobListParam;
import com.rxinyun.admin.dev.modular.job.param.DevJobPageParam;
import com.rxinyun.admin.dev.modular.job.result.DevJobActionClassResult;
import com.rxinyun.admin.dev.modular.job.service.DevJobService;
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
   name = "定时任务控制器"
)
@RestController
@Validated
public class DevJobController {
   @Resource
   private DevJobService devJobService;

   @Operation(
      summary = "获取定时任务分页"
   )
   @GetMapping({"/admin/dev/job/page"})
   public CommonResult<Page<DevJob>> page(DevJobPageParam devJobPageParam) {
      return CommonResult.data(this.devJobService.page(devJobPageParam));
   }

   @Operation(
      summary = "获取定时任务列表"
   )
   @GetMapping({"/admin/dev/job/list"})
   public CommonResult<List<DevJob>> list(DevJobListParam devJobListParam) {
      return CommonResult.data(this.devJobService.list(devJobListParam));
   }

   @Operation(
      summary = "添加定时任务"
   )
   @CommonLog("添加定时任务")
   @PostMapping({"/admin/dev/job/add"})
   public CommonResult<String> add(@RequestBody @Valid DevJobAddParam devJobAddParam) {
      this.devJobService.add(devJobAddParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "编辑定时任务"
   )
   @CommonLog("编辑定时任务")
   @PostMapping({"/admin/dev/job/edit"})
   public CommonResult<String> edit(@RequestBody @Valid DevJobEditParam devJobEditParam) {
      this.devJobService.edit(devJobEditParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "删除定时任务"
   )
   @CommonLog("删除定时任务")
   @PostMapping({"/admin/dev/job/delete"})
   public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空") List<DevJobIdParam> devJobIdParamList) {
      this.devJobService.delete(devJobIdParamList);
      return CommonResult.ok();
   }

   @Operation(
      summary = "获取定时任务详情"
   )
   @GetMapping({"/admin/dev/job/detail"})
   public CommonResult<DevJob> detail(@Valid DevJobIdParam devJobIdParam) {
      return CommonResult.data(this.devJobService.detail(devJobIdParam));
   }

   @Operation(
      summary = "停止定时任务"
   )
   @CommonLog("停止定时任务")
   @PostMapping({"/admin/dev/job/stopJob"})
   public CommonResult<String> stopJob(@RequestBody DevJobIdParam devJobIdParam) {
      this.devJobService.stopJob(devJobIdParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "运行定时任务"
   )
   @CommonLog("运行定时任务")
   @PostMapping({"/admin/dev/job/runJob"})
   public CommonResult<String> runJob(@RequestBody @Valid DevJobIdParam devJobIdParam) {
      this.devJobService.runJob(devJobIdParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "立即运行定时任务"
   )
   @CommonLog("立即运行定时任务")
   @PostMapping({"/admin/dev/job/runJobNow"})
   public CommonResult<String> runJobNow(@RequestBody @Valid DevJobIdParam devJobIdParam) {
      this.devJobService.runJobNow(devJobIdParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "获取定时任务类"
   )
   @GetMapping({"/admin/dev/job/getActionClass"})
   public CommonResult<List<DevJobActionClassResult>> getActionClass() {
      return CommonResult.data(this.devJobService.getActionClass());
   }
}
