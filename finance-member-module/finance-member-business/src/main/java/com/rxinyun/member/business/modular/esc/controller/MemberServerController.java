package com.rxinyun.member.business.modular.esc.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.common.annotation.CommonLog;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.monitor.ai.MonitorAiChatResult;
import com.rxinyun.common.monitor.ai.MonitorAiDiagnosisResult;
import com.rxinyun.common.monitor.ai.MonitorAiDiagnosisService;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.common.qzsystem.result.HostMonitorResult;
import com.rxinyun.member.business.modular.esc.entity.MemberServer;
import com.rxinyun.member.business.modular.esc.param.MemberRemarkEditParam;
import com.rxinyun.member.business.modular.esc.param.MemberServerBackupCreateParam;
import com.rxinyun.member.business.modular.esc.param.MemberServerBackupIdParam;
import com.rxinyun.member.business.modular.esc.param.MemberServerCpuHistoryResult;
import com.rxinyun.member.business.modular.esc.param.MemberServerIdListParam;
import com.rxinyun.member.business.modular.esc.param.MemberServerIdParam;
import com.rxinyun.member.business.modular.esc.param.MemberServerNetworkHistoryResult;
import com.rxinyun.member.business.modular.esc.param.MemberServerPageParam;
import com.rxinyun.member.business.modular.esc.param.MemberServerRenewAutoParam;
import com.rxinyun.member.business.modular.esc.param.MemberServerSnapshotCreateParam;
import com.rxinyun.member.business.modular.esc.param.MemberServerSnapshotIdParam;
import com.rxinyun.member.business.modular.esc.param.MemberServerTransferRecordAddParam;
import com.rxinyun.member.business.modular.esc.param.MemberServerTransferRecordIdParam;
import com.rxinyun.member.business.modular.esc.param.PasswordEditParam;
import com.rxinyun.member.business.modular.esc.param.ReinstallEditParam;
import com.rxinyun.member.business.modular.esc.service.MemberServerService;
import com.rxinyun.member.core.util.StpClientLoginUserUtil;
import com.rxinyun.member.member.pojo.SaBaseClientLoginUser;
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
   name = "云服务器管理控制器"
)
@RestController
@RequestMapping({"/member/cloud/server"})
@Validated
public class MemberServerController {
   @Resource
   private MemberServerService memberServerService;
   @Resource
   private MonitorAiDiagnosisService monitorAiDiagnosisService;

   @Operation(
      summary = "获取云服务器分页"
   )
   @GetMapping({"/page"})
   public CommonResult<Page<MemberServer>> page(MemberServerPageParam pageParam) {
      return CommonResult.data(this.memberServerService.page(pageParam));
   }

   @Operation(
      summary = "根据IDList查询服务器列表"
   )
   @PostMapping({"/list"})
   public CommonResult<List<MemberServer>> list(@RequestBody MemberServerIdListParam memberServerIdListParam) {
      return CommonResult.data(this.memberServerService.list(memberServerIdListParam));
   }

   @Operation(
      summary = "获取云服务器详情"
   )
   @GetMapping({"/detail"})
   public CommonResult<MemberServer> detail(@Valid MemberServerIdParam memberServerIdParam) {
      return CommonResult.data(this.memberServerService.detail(memberServerIdParam));
   }

   @Operation(
      summary = "云服务器开机"
   )
   @CommonLog("云服务器开机")
   @PostMapping({"/start"})
   public CommonResult start(@RequestBody MemberServerIdParam memberServerIdParam) {
      return this.memberServerService.startServer(memberServerIdParam);
   }

   @Operation(
      summary = "云服务器关机"
   )
   @CommonLog("云服务器关机")
   @PostMapping({"/stop"})
   public CommonResult stop(@RequestBody MemberServerIdParam memberServerIdParam) {
      return this.memberServerService.closeServer(memberServerIdParam);
   }

   @Operation(
      summary = "云服务器监控"
   )
   @CommonLog("云服务器监控")
   @PostMapping({"/monitor"})
   public CommonResult<HostMonitorResult> monitor(@RequestBody MemberServerIdParam memberServerIdParam) {
      return this.memberServerService.monitorServer(memberServerIdParam);
   }

   @Operation(
      summary = "云服务器监控AI诊断"
   )
   @CommonLog("云服务器监控AI诊断")
   @PostMapping({"/monitor/ai/diagnose"})
   public CommonResult<MonitorAiDiagnosisResult> monitorAiDiagnose(@RequestBody Object requestBody) {
      this.requireClientLogin();
      return this.monitorAiDiagnosisService.diagnose(requestBody);
   }

   @Operation(
      summary = "云服务器监控AI对话"
   )
   @CommonLog("云服务器监控AI对话")
   @PostMapping({"/monitor/ai/chat"})
   public CommonResult<MonitorAiChatResult> monitorAiChat(@RequestBody Object requestBody) {
      this.requireClientLogin();
      return this.monitorAiDiagnosisService.chat(requestBody);
   }

   @Operation(
      summary = "服务器重装系统"
   )
   @CommonLog("服务器重装系统")
   @PostMapping({"/reinstall"})
   public CommonResult reinstall(@RequestBody ReinstallEditParam reinstallEditParam) {
      return this.memberServerService.reinstallServer(reinstallEditParam);
   }

   @Operation(
      summary = "服务器重建"
   )
   @CommonLog("服务器重建")
   @PostMapping({"/rebuild"})
   public CommonResult rebuild(@RequestBody MemberServerIdParam memberServerIdParam) {
      return this.memberServerService.rebuildServer(memberServerIdParam);
   }

   @Operation(
      summary = "服务器修改密码"
   )
   @CommonLog("服务器修改密码")
   @PostMapping({"/updatePassword"})
   public CommonResult updatePassword(@RequestBody PasswordEditParam passwordEditParam) {
      return this.memberServerService.updatePasswordServer(passwordEditParam);
   }

   @Operation(
      summary = "服务器重启"
   )
   @CommonLog("服务器重启")
   @PostMapping({"/restart"})
   public CommonResult restart(@RequestBody MemberServerIdParam serverIdParam) {
      return this.memberServerService.restartServer(serverIdParam);
   }

   @Operation(
      summary = "服务器断开电源"
   )
   @CommonLog("服务器断开电源")
   @PostMapping({"/power"})
   public CommonResult power(@RequestBody MemberServerIdParam serverIdParam) {
      return this.memberServerService.powerServer(serverIdParam);
   }

   @Operation(
      summary = "获取主机运行图片"
   )
   @CommonLog("获取主机运行图片")
   @PostMapping({"/thumbnail"})
   public CommonResult thumbnail(@RequestBody MemberServerIdParam serverIdParam) {
      return this.memberServerService.thumbnailServer(serverIdParam);
   }

   @Operation(
      summary = "同步服务器状态"
   )
   @CommonLog("同步服务器状态")
   @PostMapping({"/sync"})
   public CommonResult sync(@RequestBody MemberServerIdParam serverIdParam) {
      return this.memberServerService.syncStatusServer(serverIdParam);
   }

   @Operation(
      summary = "断网"
   )
   @CommonLog("断网")
   @PostMapping({"/disconnect"})
   public CommonResult disconnect(@RequestBody MemberServerIdParam serverIdParam) {
      return this.memberServerService.disconnectServer(serverIdParam);
   }

   @Operation(
      summary = "恢复断网"
   )
   @CommonLog("恢复断网")
   @PostMapping({"/reconnect"})
   public CommonResult reconnect(@RequestBody MemberServerIdParam serverIdParam) {
      return this.memberServerService.reconnectServer(serverIdParam);
   }

   @Operation(
      summary = "获取vnc连接地址"
   )
   @CommonLog("获取vnc连接地址")
   @PostMapping({"/vnc"})
   public CommonResult vnc(@RequestBody MemberServerIdParam serverIdParam) {
      return this.memberServerService.vncUrl(serverIdParam);
   }

   @Operation(
      summary = "创建云服务器备份"
   )
   @CommonLog("创建云服务器备份")
   @PostMapping({"/backup/create"})
   public CommonResult createBackup(@RequestBody MemberServerBackupCreateParam memberServerBackupCreateParam) {
      return this.memberServerService.createBackup(memberServerBackupCreateParam);
   }

   @Operation(
      summary = "还原云服务器备份"
   )
   @CommonLog("还原云服务器备份")
   @PostMapping({"/backup/restore"})
   public CommonResult restoreBackup(@RequestBody MemberServerBackupIdParam memberServerBackupIdParam) {
      return this.memberServerService.restoreBackup(memberServerBackupIdParam);
   }

   @Operation(
      summary = "删除云服务器备份"
   )
   @CommonLog("删除云服务器备份")
   @PostMapping({"/backup/delete"})
   public CommonResult deleteBackup(@RequestBody MemberServerBackupIdParam memberServerBackupIdParam) {
      return this.memberServerService.deleteBackup(memberServerBackupIdParam);
   }

   @Operation(
      summary = "查询云服务器备份列表"
   )
   @PostMapping({"/backup/list"})
   public CommonResult listBackup(@RequestBody MemberServerIdParam memberServerIdParam) {
      return this.memberServerService.listBackup(memberServerIdParam);
   }

   @Operation(
      summary = "创建云服务器快照"
   )
   @CommonLog("创建云服务器快照")
   @PostMapping({"/snapshot/create"})
   public CommonResult createSnapshot(@RequestBody MemberServerSnapshotCreateParam memberServerSnapshotCreateParam) {
      return this.memberServerService.createSnapshot(memberServerSnapshotCreateParam);
   }

   @Operation(
      summary = "还原云服务器快照"
   )
   @CommonLog("还原云服务器快照")
   @PostMapping({"/snapshot/restore"})
   public CommonResult restoreSnapshot(@RequestBody MemberServerSnapshotIdParam memberServerSnapshotIdParam) {
      return this.memberServerService.restoreSnapshot(memberServerSnapshotIdParam);
   }

   @Operation(
      summary = "删除云服务器快照"
   )
   @CommonLog("删除云服务器快照")
   @PostMapping({"/snapshot/delete"})
   public CommonResult deleteSnapshot(@RequestBody MemberServerSnapshotIdParam memberServerSnapshotIdParam) {
      return this.memberServerService.deleteSnapshot(memberServerSnapshotIdParam);
   }

   @Operation(
      summary = "查询云服务器快照列表"
   )
   @PostMapping({"/snapshot/list"})
   public CommonResult listSnapshot(@RequestBody MemberServerIdParam memberServerIdParam) {
      return this.memberServerService.listSnapshot(memberServerIdParam);
   }

   @Operation(
      summary = "用户服务器过户"
   )
   @CommonLog("用户服务器过户")
   @PostMapping({"/plus"})
   public CommonResult plus(@RequestBody MemberServerTransferRecordAddParam memberServerTransferRecordAddParam) {
      return this.memberServerService.plus(memberServerTransferRecordAddParam);
   }

   @Operation(
      summary = "转让服务器 通过/拒绝"
   )
   @CommonLog("转让服务器 通过/拒绝")
   @PostMapping({"/updateStatus"})
   public CommonResult updateStatus(@RequestBody MemberServerTransferRecordIdParam memberServerTransferRecordEditParam) {
      return this.memberServerService.updateStatus(memberServerTransferRecordEditParam);
   }

   @Operation(
      summary = "用户编辑备注"
   )
   @CommonLog("用户编辑云服务器备注")
   @PostMapping({"/editRemark"})
   public CommonResult<String> editRemark(@RequestBody MemberRemarkEditParam memberRemarkEditParam) {
      this.memberServerService.editRemark(memberRemarkEditParam);
      return CommonResult.ok("修改备注成功");
   }

   @Operation(
      summary = "是否开启续费"
   )
   @CommonLog("修改云服务器自动续费")
   @PostMapping({"/isRenew"})
   public CommonResult<Boolean> isRenew(@RequestBody MemberServerRenewAutoParam memberServerRenewAutoParam) {
      this.memberServerService.isRenew(memberServerRenewAutoParam);
      return CommonResult.ok("自动续费修改成功");
   }

   @Operation(
      summary = "查询云服务器网络历史数据"
   )
   @CommonLog("查询云服务器网络历史数据")
   @PostMapping({"/networkHistory"})
   public CommonResult<List<MemberServerNetworkHistoryResult>> networkHistory(@RequestBody MemberServerIdParam memberServerIdParam) {
      return this.memberServerService.getNetworkHistory(memberServerIdParam);
   }

   @Operation(
      summary = "查询云服务器CPU历史数据"
   )
   @CommonLog("查询云服务器CPU历史数据")
   @PostMapping({"/cpuHistory"})
   public CommonResult<List<MemberServerCpuHistoryResult>> cpuHistory(@RequestBody MemberServerIdParam memberServerIdParam) {
      return this.memberServerService.getCpuHistory(memberServerIdParam);
   }

   private void requireClientLogin() {
      SaBaseClientLoginUser clientLoginUser = StpClientLoginUserUtil.getClientLoginUser();
      if (clientLoginUser == null || clientLoginUser.getId() == null) {
         throw new CommonException("请先登录", new Object[0]);
      }
   }
}
