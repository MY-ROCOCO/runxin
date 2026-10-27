package com.rxinyun.admin.dev.modular.log.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.dev.modular.log.param.IndependentLogPageParam;
import com.rxinyun.admin.dev.modular.log.result.ServerOperationLogResult;
import com.rxinyun.admin.dev.modular.log.service.IndependentLogService;
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
   name = "独立服务器日志控制器"
)
@RestController
@RequestMapping({"/admin/dev/independent/log"})
@Validated
public class IndependentLogController {
   @Resource
   private IndependentLogService independentLogService;

   @Operation(
      summary = "获取独立服务器日志分页"
   )
   @GetMapping({"/page"})
   public CommonResult<Page<ServerOperationLogResult>> page(@Valid IndependentLogPageParam independentLogPageParam) {
      return CommonResult.data(this.independentLogService.page(independentLogPageParam));
   }
}
