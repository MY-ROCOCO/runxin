package com.rxinyun.admin.business.modular.task.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.business.modular.task.entity.TaskExecutionLog;
import com.rxinyun.admin.business.modular.task.param.TaskExecutionLogParam;
import com.rxinyun.admin.business.modular.task.service.TaskExecutionLogService;
import com.rxinyun.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "自动化任务管理控制器"
)
@RestController
@RequestMapping({"/admin/task/log"})
@Validated
public class TaskExecutionLogController {
   @Resource
   private TaskExecutionLogService taskExecutionLogService;

   @Operation(
      summary = "获取任务日志分页列表"
   )
   @GetMapping({"/page"})
   public CommonResult<Page<TaskExecutionLog>> page(@Valid TaskExecutionLogParam pageParam) {
      return CommonResult.data(this.taskExecutionLogService.page(pageParam));
   }
}
