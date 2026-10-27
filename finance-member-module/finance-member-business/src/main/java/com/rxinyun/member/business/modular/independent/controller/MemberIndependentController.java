package com.rxinyun.member.business.modular.independent.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.common.annotation.CommonLog;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.member.business.modular.esc.param.MemberServerTransferRecordAddParam;
import com.rxinyun.member.business.modular.esc.param.MemberServerTransferRecordIdParam;
import com.rxinyun.member.business.modular.independent.entity.MemberIndependent;
import com.rxinyun.member.business.modular.independent.param.MemberIndependentIdListParam;
import com.rxinyun.member.business.modular.independent.param.MemberIndependentIdParam;
import com.rxinyun.member.business.modular.independent.param.MemberIndependentPageParam;
import com.rxinyun.member.business.modular.independent.param.MemberIndependentPasswordEditParam;
import com.rxinyun.member.business.modular.independent.param.MemberIndependentRenewAutoParam;
import com.rxinyun.member.business.modular.independent.param.MemberRemarkEditParam;
import com.rxinyun.member.business.modular.independent.param.ReinstallEditParam;
import com.rxinyun.member.business.modular.independent.response.MemberIndependentResponse;
import com.rxinyun.member.business.modular.independent.service.MemberIndependentService;
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
@RequestMapping({"/member/independent"})
@Validated
public class MemberIndependentController {
   @Resource
   private MemberIndependentService memberIndependentService;

   @Operation(
      summary = "获取独立服务器分页"
   )
   @GetMapping({"/page"})
   public CommonResult<Page<MemberIndependentResponse>> page(MemberIndependentPageParam pageParam) {
      return CommonResult.data(this.memberIndependentService.page(pageParam));
   }

   @Operation(
      summary = "根据IDList查询独立服务器列表"
   )
   @PostMapping({"/list"})
   public CommonResult<List<MemberIndependent>> list(@RequestBody MemberIndependentIdListParam memberServerIdListParam) {
      return CommonResult.data(this.memberIndependentService.list(memberServerIdListParam));
   }

   @Operation(
      summary = "获取独立服务器详情"
   )
   @GetMapping({"/detail"})
   public CommonResult<MemberIndependentResponse> detail(@Valid MemberIndependentIdParam memberServerIdParam) {
      return CommonResult.data(this.memberIndependentService.detail(memberServerIdParam));
   }

   @Operation(
      summary = "用户编辑独立服务器备注"
   )
   @CommonLog("用户编辑独立服务器备注")
   @PostMapping({"/editRemark"})
   public CommonResult<String> editRemark(@RequestBody MemberRemarkEditParam memberRemarkEditParam) {
      this.memberIndependentService.editRemark(memberRemarkEditParam);
      return CommonResult.ok("修改备注成功");
   }

   @Operation(
      summary = "修改独立服务器自动续费"
   )
   @CommonLog("修改独立服务器自动续费")
   @PostMapping({"/isRenew"})
   public CommonResult<Boolean> isRenew(@RequestBody MemberIndependentRenewAutoParam param) {
      this.memberIndependentService.isRenew(param);
      return CommonResult.ok("自动续费修改成功");
   }

   @Operation(
      summary = "独立服务器修改密码"
   )
   @CommonLog("独立服务器修改密码")
   @PostMapping({"/updatePassword"})
   public CommonResult updatePassword(@RequestBody MemberIndependentPasswordEditParam passwordEditParam) {
      return this.memberIndependentService.updatePasswordServer(passwordEditParam);
   }

   @Operation(
      summary = "独立服务器开机"
   )
   @CommonLog("独立服务器开机")
   @PostMapping({"/start"})
   public CommonResult start(@RequestBody MemberIndependentIdParam memberIndependentIdParam) {
      return this.memberIndependentService.startServer(memberIndependentIdParam);
   }

   @Operation(
      summary = "独立服务器关机"
   )
   @CommonLog("独立服务器关机")
   @PostMapping({"/stop"})
   public CommonResult stop(@RequestBody MemberIndependentIdParam memberIndependentIdParam) {
      return this.memberIndependentService.closeServer(memberIndependentIdParam);
   }

   @Operation(
      summary = "独立服务器重装系统"
   )
   @CommonLog("独立服务器重装系统")
   @PostMapping({"/reinstall"})
   public CommonResult reinstall(@RequestBody ReinstallEditParam reinstallEditParam) {
      return this.memberIndependentService.reinstallServer(reinstallEditParam);
   }

   @Operation(
      summary = "独立服务器断开电源"
   )
   @CommonLog("独立服务器断开电源")
   @PostMapping({"/power"})
   public CommonResult power(@RequestBody MemberIndependentIdParam serverIdParam) {
      return this.memberIndependentService.powerServer(serverIdParam);
   }

   @Operation(
      summary = "独立服务器重启"
   )
   @CommonLog("独立服务器重启")
   @PostMapping({"/restart"})
   public CommonResult restart(@RequestBody MemberIndependentIdParam serverIdParam) {
      return this.memberIndependentService.restartServer(serverIdParam);
   }

   @Operation(
      summary = "用户服务器过户"
   )
   @CommonLog("用户服务器过户")
   @PostMapping({"/plus"})
   public CommonResult plus(@RequestBody MemberServerTransferRecordAddParam memberServerTransferRecordAddParam) {
      return this.memberIndependentService.plus(memberServerTransferRecordAddParam);
   }

   @Operation(
      summary = "转让服务器 通过/拒绝"
   )
   @CommonLog("转让服务器 通过/拒绝")
   @PostMapping({"/updateStatus"})
   public CommonResult updateStatus(@RequestBody MemberServerTransferRecordIdParam memberServerTransferRecordEditParam) {
      return this.memberIndependentService.updateStatus(memberServerTransferRecordEditParam);
   }
}
