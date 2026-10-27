package com.rxinyun.admin.business.modular.vhost.task.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.business.modular.vhost.task.entity.VhostAutoTask;
import com.rxinyun.admin.business.modular.vhost.task.param.VhostAutoTaskIdParam;
import com.rxinyun.admin.business.modular.vhost.task.param.VhostAutoTaskPageParam;
import com.rxinyun.admin.business.modular.vhost.task.service.VhostAutoTaskService;
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
   name = "后台虚拟主机自动化任务"
)
@RestController
@RequestMapping({"/admin/vhost/task"})
@Validated
public class AdminVhostTaskController {
   @Resource
   private VhostAutoTaskService vhostAutoTaskService;

   @Operation(
      summary = "虚拟主机自动化任务分页"
   )
   @GetMapping({"/page"})
   public CommonResult<Page<VhostAutoTask>> page(VhostAutoTaskPageParam pageParam) {
      return CommonResult.data(this.vhostAutoTaskService.page(pageParam));
   }

   @Operation(
      summary = "重试失败的虚拟主机自动化任务"
   )
   @PostMapping({"/retry"})
   public CommonResult<String> retry(@RequestBody @Valid VhostAutoTaskIdParam idParam) {
      return this.vhostAutoTaskService.retry(idParam);
   }
}
