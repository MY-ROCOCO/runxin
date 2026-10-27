package com.rxinyun.admin.business.modular.independent.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.business.modular.esc.param.ServerTransferRecordAddParam;
import com.rxinyun.admin.business.modular.esc.param.ServerTransferRecordIdParam;
import com.rxinyun.admin.business.modular.esc.service.ServerTransferRecordService;
import com.rxinyun.admin.business.modular.independent.entity.Independent;
import com.rxinyun.admin.business.modular.independent.param.IndependentAddEndDaysParam;
import com.rxinyun.admin.business.modular.independent.param.IndependentEditParam;
import com.rxinyun.admin.business.modular.independent.param.IndependentEndTimeParam;
import com.rxinyun.admin.business.modular.independent.param.IndependentIdListParam;
import com.rxinyun.admin.business.modular.independent.param.IndependentIdParam;
import com.rxinyun.admin.business.modular.independent.param.IndependentLockParam;
import com.rxinyun.admin.business.modular.independent.param.IndependentPageParam;
import com.rxinyun.admin.business.modular.independent.param.IndependentPasswordEditParam;
import com.rxinyun.admin.business.modular.independent.param.IndependentReinstallEditParam;
import com.rxinyun.admin.business.modular.independent.param.IndependentRenewAutoParam;
import com.rxinyun.admin.business.modular.independent.param.IndependentRenewPriceParam;
import com.rxinyun.admin.business.modular.independent.param.RemarkEditParam;
import com.rxinyun.admin.business.modular.independent.response.IndependentResponse;
import com.rxinyun.admin.business.modular.independent.service.IndependentService;
import com.rxinyun.common.annotation.CommonLog;
import com.rxinyun.common.enums.order.ProductTypeEnum;
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
   name = "独立服务器管理控制器"
)
@RestController
@RequestMapping({"/admin/independent"})
@Validated
public class IndependentController {
   @Resource
   private IndependentService independentService;
   @Resource
   private ServerTransferRecordService serverTransferRecordService;

   @Operation(
      summary = "获取独立服务器分页"
   )
   @GetMapping({"/page"})
   public CommonResult<Page<IndependentResponse>> page(IndependentPageParam pageParam) {
      return CommonResult.data(this.independentService.page(pageParam));
   }

   @Operation(
      summary = "根据IDList查询独立服务器列表"
   )
   @PostMapping({"/list"})
   public CommonResult<List<Independent>> list(@RequestBody IndependentIdListParam memberServerIdListParam) {
      return CommonResult.data(this.independentService.list(memberServerIdListParam));
   }

   @Operation(
      summary = "获取独立服务器详情"
   )
   @GetMapping({"/detail"})
   public CommonResult<IndependentResponse> detail(@Valid IndependentIdParam memberServerIdParam) {
      return CommonResult.data(this.independentService.detail(memberServerIdParam));
   }

   @Operation(
      summary = "编辑独立服务器"
   )
   @CommonLog("编辑独立服务器")
   @PostMapping({"/edit"})
   public CommonResult<String> edit(@RequestBody IndependentEditParam independentEditParam) {
      this.independentService.edit(independentEditParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "编辑独立服务器备注（管理员）"
   )
   @CommonLog("编辑独立服务器备注")
   @PostMapping({"/editRemark"})
   public CommonResult<String> editRemark(@RequestBody RemarkEditParam remarkEditParam) {
      this.independentService.editRemark(remarkEditParam);
      return CommonResult.ok("修改备注成功");
   }

   @Operation(
      summary = "修改独立服务器续费价格"
   )
   @CommonLog("修改独立服务器续费价格")
   @PostMapping({"/updateRenewPrice"})
   public CommonResult updateRenewPrice(@RequestBody IndependentRenewPriceParam param) {
      this.independentService.updateRenewPrice(param);
      return CommonResult.ok();
   }

   @Operation(
      summary = "修改独立服务器自动续费"
   )
   @CommonLog("修改独立服务器自动续费")
   @PostMapping({"/isRenew"})
   public CommonResult<Boolean> isRenew(@RequestBody IndependentRenewAutoParam param) {
      this.independentService.isRenew(param);
      return CommonResult.ok("自动续费修改成功");
   }

   @Operation(
      summary = "修改独立服务器到期时间"
   )
   @CommonLog("修改独立服务器到期时间")
   @PostMapping({"/updateEndTime"})
   public CommonResult updateEndTime(@RequestBody IndependentEndTimeParam param) {
      this.independentService.updateEndTime(param);
      return CommonResult.ok();
   }

   @Operation(
      summary = "批量增加独立服务器到期天数"
   )
   @CommonLog("批量增加独立服务器到期天数")
   @PostMapping({"/batchAddEndDays"})
   public CommonResult batchAddEndDays(@RequestBody IndependentAddEndDaysParam param) {
      this.independentService.batchAddEndDays(param);
      return CommonResult.ok();
   }

   @Operation(
      summary = "同步上游独立服务器订单"
   )
   @CommonLog("同步上游独立服务器订单")
   @PostMapping({"/syncUpstreamOrder"})
   public CommonResult syncUpstreamOrder(@RequestBody IndependentIdParam param) {
      return this.independentService.syncUpstreamOrder(param);
   }

   @Operation(
      summary = "独立服务器修改密码"
   )
   @CommonLog("独立服务器修改密码")
   @PostMapping({"/updatePassword"})
   public CommonResult updatePassword(@RequestBody IndependentPasswordEditParam param) {
      return this.independentService.updatePasswordServer(param);
   }

   @Operation(
      summary = "独立服务器开机"
   )
   @CommonLog("独立服务器开机")
   @PostMapping({"/start"})
   public CommonResult start(@RequestBody IndependentIdParam param) {
      return this.independentService.adminStartIndependent(param);
   }

   @Operation(
      summary = "独立服务器关机"
   )
   @CommonLog("独立服务器关机")
   @PostMapping({"/stop"})
   public CommonResult stop(@RequestBody IndependentIdParam param) {
      return this.independentService.adminStopIndependent(param);
   }

   @Operation(
      summary = "独立服务器重启"
   )
   @CommonLog("独立服务器重启")
   @PostMapping({"/restart"})
   public CommonResult restart(@RequestBody IndependentIdParam param) {
      return this.independentService.adminRestartIndependent(param);
   }

   @Operation(
      summary = "独立服务器重装系统"
   )
   @CommonLog("独立服务器重装系统")
   @PostMapping({"/reinstall"})
   public CommonResult reinstall(@RequestBody IndependentReinstallEditParam param) {
      return this.independentService.adminReinstallIndependent(param);
   }

   @Operation(
      summary = "独立服务器断开电源"
   )
   @CommonLog("独立服务器断开电源")
   @PostMapping({"/power"})
   public CommonResult power(@RequestBody IndependentIdParam param) {
      return this.independentService.adminPowerIndependent(param);
   }

   @Operation(
      summary = "独立服务器锁定"
   )
   @CommonLog("独立服务器锁定")
   @PostMapping({"/lock"})
   public CommonResult lock(@RequestBody IndependentLockParam param) {
      return this.independentService.adminLockIndependent(param);
   }

   @Operation(
      summary = "独立服务器解锁"
   )
   @CommonLog("独立服务器解锁")
   @PostMapping({"/unlock"})
   public CommonResult unlock(@RequestBody IndependentIdParam param) {
      return this.independentService.adminUnlockIndependent(param);
   }

   @Operation(
      summary = "独立服务器过户申请"
   )
   @CommonLog("独立服务器过户")
   @PostMapping({"/transfer/plus"})
   public CommonResult transferPlus(@RequestBody ServerTransferRecordAddParam param) {
      param.setProductType(ProductTypeEnum.INDEPENDENT_SERVER.getCode());
      return this.serverTransferRecordService.add(param);
   }

   @Operation(
      summary = "独立服务器过户审核"
   )
   @CommonLog("独立服务器过户审核")
   @PostMapping({"/transfer/updateStatus"})
   public CommonResult transferUpdateStatus(@RequestBody ServerTransferRecordIdParam param) {
      return this.serverTransferRecordService.updateStatus(param);
   }
}
