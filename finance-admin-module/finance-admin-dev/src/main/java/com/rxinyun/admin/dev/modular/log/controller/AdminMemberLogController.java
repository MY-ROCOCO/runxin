package com.rxinyun.admin.dev.modular.log.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.dev.modular.log.entity.MemberLog;
import com.rxinyun.admin.dev.modular.log.param.LogDeleteParam;
import com.rxinyun.admin.dev.modular.log.param.LogIdParam;
import com.rxinyun.admin.dev.modular.log.param.LogPageParam;
import com.rxinyun.admin.dev.modular.log.result.DevLogOpBarChartDataResult;
import com.rxinyun.admin.dev.modular.log.result.DevLogOpPieChartDataResult;
import com.rxinyun.admin.dev.modular.log.result.DevLogVisLineChartDataResult;
import com.rxinyun.admin.dev.modular.log.result.DevLogVisPieChartDataResult;
import com.rxinyun.admin.dev.modular.log.service.AdminMemberLogService;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "会员日志控制器"
)
@RestController
@RequestMapping({"/admin/member/log"})
@Validated
public class AdminMemberLogController {
   @Resource
   private AdminMemberLogService logService;

   @Operation(
      summary = "获取日志分页"
   )
   @GetMapping({"/page"})
   public CommonResult<Page<MemberLog>> page(LogPageParam devLogPageParam) {
      return CommonResult.data(this.logService.page(devLogPageParam));
   }

   @Operation(
      summary = "清空日志"
   )
   @CommonLog("清空日志")
   @PostMapping({"/delete"})
   public CommonResult<String> delete(@RequestBody LogDeleteParam devLogDeleteParam) {
      this.logService.delete(devLogDeleteParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "获取访问日志折线图数据"
   )
   @GetMapping({"/vis/lineChartData"})
   public CommonResult<List<DevLogVisLineChartDataResult>> visLogLineChartData() {
      return CommonResult.data(this.logService.visLogLineChartData());
   }

   @Operation(
      summary = "获取访问日志饼状图数据"
   )
   @GetMapping({"/vis/pieChartData"})
   public CommonResult<List<DevLogVisPieChartDataResult>> visLogPieChartData() {
      return CommonResult.data(this.logService.visLogPieChartData());
   }

   @Operation(
      summary = "获取操作日志柱状图数据"
   )
   @GetMapping({"/op/barChartData"})
   public CommonResult<List<DevLogOpBarChartDataResult>> opLogBarChartData() {
      return CommonResult.data(this.logService.opLogBarChartData());
   }

   @Operation(
      summary = "获取操作日志饼状图数据"
   )
   @GetMapping({"/op/pieChartData"})
   public CommonResult<List<DevLogOpPieChartDataResult>> opLogPieChartData() {
      return CommonResult.data(this.logService.opLogPieChartData());
   }

   @Operation(
      summary = "依据id获取日志详情"
   )
   @GetMapping({"/detail"})
   public CommonResult<MemberLog> detail(@Valid LogIdParam devLogIdParam) {
      return CommonResult.data(this.logService.detail(devLogIdParam));
   }
}
