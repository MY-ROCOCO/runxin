package com.rxinyun.admin.business.modular.task.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.business.modular.task.entity.AutoTask;
import com.rxinyun.admin.business.modular.task.param.AutoTaskIdParam;
import com.rxinyun.admin.business.modular.task.param.AutoTaskPageParam;
import com.rxinyun.admin.business.modular.task.service.AutoTaskTaskService;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "自动化任务管理控制器"
)
@RestController
@RequestMapping({"/admin/task"})
@Validated
public class AutoTaskController {
   @Resource
   private AutoTaskTaskService orderAsyncTaskService;

   @Operation(
      summary = "获取任务分页列表"
   )
   @GetMapping({"/page"})
   public CommonResult<Page<AutoTask>> page(@Valid AutoTaskPageParam pageParam) {
      return CommonResult.data(this.orderAsyncTaskService.page(pageParam));
   }

   @Operation(
      summary = "获取待处理任务列表"
   )
   @GetMapping({"/pending"})
   public CommonResult<List<AutoTask>> getPendingTasks() {
      return CommonResult.data(this.orderAsyncTaskService.queryPendingTaskList());
   }

   @Operation(
      summary = "手动重试失败的任务"
   )
   @PostMapping({"/retry"})
   public CommonResult<String> retryFailedTask(@RequestBody AutoTaskIdParam autoTaskIdParam) {
      return this.orderAsyncTaskService.retryFailedTask(autoTaskIdParam);
   }
}
