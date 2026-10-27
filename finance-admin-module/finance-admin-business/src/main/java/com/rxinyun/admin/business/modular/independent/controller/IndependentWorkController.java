package com.rxinyun.admin.business.modular.independent.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.business.modular.independent.param.IndependentWorkIdParam;
import com.rxinyun.admin.business.modular.independent.param.IndependentWorkPageParam;
import com.rxinyun.admin.business.modular.independent.param.IndependentWorkStatusUpdateParam;
import com.rxinyun.admin.business.modular.independent.response.IndependentWorkResponse;
import com.rxinyun.admin.business.modular.independent.service.IndependentWorkService;
import com.rxinyun.common.annotation.CommonLog;
import com.rxinyun.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "独立服务器人工服务管理控制器"
)
@RestController
@RequestMapping({"/admin/independent"})
@Validated
public class IndependentWorkController {
   private static final Logger log = LoggerFactory.getLogger(IndependentWorkController.class);
   @Resource
   private IndependentWorkService independentWorkService;

   @Operation(
      summary = "获取人工服务分页"
   )
   @GetMapping({"/work/page"})
   public CommonResult<Page<IndependentWorkResponse>> page(IndependentWorkPageParam pageParam) {
      return CommonResult.data(this.independentWorkService.page(pageParam));
   }

   @Operation(
      summary = "获取人工服务工单详情"
   )
   @GetMapping({"/work/detail"})
   public CommonResult<IndependentWorkResponse> detail(@Valid IndependentWorkIdParam idParam) {
      return CommonResult.data(this.independentWorkService.detail(idParam));
   }

   @Operation(
      summary = "变更处理状态"
   )
   @CommonLog("变更处理状态")
   @PostMapping({"/updateStatus"})
   public CommonResult<String> updateStatus(@RequestBody IndependentWorkStatusUpdateParam updateParam) {
      this.independentWorkService.updateStatus(updateParam);
      return CommonResult.ok();
   }
}
