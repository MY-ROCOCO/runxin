package com.rxinyun.admin.workbench.controller;

import com.rxinyun.admin.workbench.pojo.vo.BusinessVO;
import com.rxinyun.admin.workbench.pojo.vo.DisplayPanelVO;
import com.rxinyun.admin.workbench.pojo.vo.FinanceVO;
import com.rxinyun.admin.workbench.pojo.vo.MemberVO;
import com.rxinyun.admin.workbench.service.BenchWorkOrderService;
import com.rxinyun.common.annotation.CommonLog;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.workorder.pojo.WorkOrderStatisticsResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "工作台管理控制器"
)
@RestController
@RequestMapping({"/admin/workbench/statistics"})
public class BenchWorkOrderController {
   @Resource
   BenchWorkOrderService benchWorkOrderService;

   @CommonLog("工作台顶部展示栏信息")
   @Operation(
      summary = "工作台顶部展示栏信息"
   )
   @GetMapping({"/top"})
   public CommonResult<DisplayPanelVO> getTop() {
      return CommonResult.data(this.benchWorkOrderService.getTop());
   }

   @CommonLog("工作台统计工单信息")
   @Operation(
      summary = "工作台统计工单信息"
   )
   @GetMapping({"/workorder"})
   public CommonResult<WorkOrderStatisticsResponse> getWorkOrder(@RequestParam @Parameter(description = "时间范围参数，可选值：昨天, 今天, 本周, 上周, 本月, 上月, 全部") String time) {
      return CommonResult.data(this.benchWorkOrderService.statisticsWorkOrder(time));
   }

   @CommonLog("工作台统计会员信息")
   @Operation(
      summary = "工作台统计会员信息"
   )
   @GetMapping({"/member"})
   public CommonResult<MemberVO> getReal(@RequestParam @Parameter(description = "时间范围参数，可选值：昨天, 今天, 本周, 上周, 本月, 上月, 全部") String time) {
      return CommonResult.data(this.benchWorkOrderService.statisticsReal(time));
   }

   @CommonLog("工作台统计财务信息")
   @Operation(
      summary = "工作台统计财务信息"
   )
   @GetMapping({"/finance"})
   public CommonResult<FinanceVO> getFinance(@RequestParam @Parameter(description = "时间范围参数，可选值：昨天, 今天, 本周, 上周, 本月, 上月, 全部") String time) {
      return CommonResult.data(this.benchWorkOrderService.finance(time));
   }

   @CommonLog("工作台统计业务信息")
   @Operation(
      summary = "工作台统计业务信息"
   )
   @GetMapping({"/business"})
   public CommonResult<BusinessVO> getBussiness(@RequestParam @Parameter(description = "时间范围参数，可选值：昨天, 今天, 本周, 上周, 本月, 上月, 全部") String time) {
      return CommonResult.data(this.benchWorkOrderService.getBusiness(time));
   }
}
