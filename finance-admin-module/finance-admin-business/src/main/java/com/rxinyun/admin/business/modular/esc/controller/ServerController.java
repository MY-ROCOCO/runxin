package com.rxinyun.admin.business.modular.esc.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.business.modular.esc.entity.Server;
import com.rxinyun.admin.business.modular.esc.param.PasswordEditParam;
import com.rxinyun.admin.business.modular.esc.param.ReinstallEditParam;
import com.rxinyun.admin.business.modular.esc.param.ServerAddEndDaysParam;
import com.rxinyun.admin.business.modular.esc.param.ServerBackupCreateParam;
import com.rxinyun.admin.business.modular.esc.param.ServerBackupIdParam;
import com.rxinyun.admin.business.modular.esc.param.ServerCpuHistoryResult;
import com.rxinyun.admin.business.modular.esc.param.ServerEditParam;
import com.rxinyun.admin.business.modular.esc.param.ServerEndTimeParam;
import com.rxinyun.admin.business.modular.esc.param.ServerIdParam;
import com.rxinyun.admin.business.modular.esc.param.ServerLockParam;
import com.rxinyun.admin.business.modular.esc.param.ServerMonitorParam;
import com.rxinyun.admin.business.modular.esc.param.ServerNetworkHistoryResult;
import com.rxinyun.admin.business.modular.esc.param.ServerPageParam;
import com.rxinyun.admin.business.modular.esc.param.ServerRemarkEditParam;
import com.rxinyun.admin.business.modular.esc.param.ServerRenewAutoParam;
import com.rxinyun.admin.business.modular.esc.param.ServerRenewPriceParam;
import com.rxinyun.admin.business.modular.esc.param.ServerSnapshotCreateParam;
import com.rxinyun.admin.business.modular.esc.param.ServerSnapshotIdParam;
import com.rxinyun.admin.business.modular.esc.param.ServerTransferRecordAddParam;
import com.rxinyun.admin.business.modular.esc.param.ServerTransferRecordIdParam;
import com.rxinyun.admin.business.modular.esc.param.ServerUpgradeParam;
import com.rxinyun.admin.business.modular.esc.service.ServerService;
import com.rxinyun.common.annotation.CommonLog;
import com.rxinyun.common.monitor.ai.MonitorAiChatResult;
import com.rxinyun.common.monitor.ai.MonitorAiDiagnosisResult;
import com.rxinyun.common.monitor.ai.MonitorAiDiagnosisService;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.common.qzsystem.result.HostMonitorResult;
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
@RequestMapping({"/admin/cloud/server"})
@Validated
public class ServerController {
   @Resource
   private ServerService serverService;
   @Resource
   private MonitorAiDiagnosisService monitorAiDiagnosisService;

   @Operation(
      summary = "查询云服务器分页"
   )
   @GetMapping({"/page"})
   public CommonResult<Page<Server>> page(ServerPageParam pageParam) {
      return CommonResult.data(this.serverService.page(pageParam));
   }

   @Operation(
      summary = "查询云服务器详情"
   )
   @GetMapping({"/detail"})
   public CommonResult<Server> detail(@Valid ServerIdParam serverIdParam) {
      return CommonResult.data(this.serverService.detail(serverIdParam));
   }

   @Operation(
      summary = "升级云服务器"
   )
   @CommonLog("升级云服务器")
   @PostMapping({"/upgrade"})
   public CommonResult upgrade(@RequestBody ServerUpgradeParam serverUpgradeParam) {
      return this.serverService.upgradeServer(serverUpgradeParam);
   }

   @Operation(
      summary = "编辑云服务器"
   )
   @CommonLog("编辑云服务器")
   @PostMapping({"/edit"})
   public CommonResult<String> edit(@RequestBody ServerEditParam serverEditParam) {
      this.serverService.edit(serverEditParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "修改云服务器续费价格"
   )
   @CommonLog("修改云服务器续费价格")
   @PostMapping({"/updateRenewPrice"})
   public CommonResult updateRenewPrice(@RequestBody ServerRenewPriceParam serverRenewPriceParam) {
      this.serverService.updateRenewPrice(serverRenewPriceParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "修改云服务器自动续费"
   )
   @CommonLog("修改云服务器自动续费")
   @PostMapping({"/isRenew"})
   public CommonResult<Boolean> isRenew(@RequestBody ServerRenewAutoParam param) {
      this.serverService.isRenew(param);
      return CommonResult.ok("自动续费修改成功");
   }

   @Operation(
      summary = "修改云服务器到期时间"
   )
   @CommonLog("修改云服务器到期时间")
   @PostMapping({"/updateEndTime"})
   public CommonResult updateEndTime(@RequestBody ServerEndTimeParam serverEndTimeParam) {
      this.serverService.updateEndTime(serverEndTimeParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "批量增加云服务器到期天数"
   )
   @CommonLog("批量增加云服务器到期天数")
   @PostMapping({"/batchAddEndDays"})
   public CommonResult batchAddEndDays(@RequestBody ServerAddEndDaysParam param) {
      this.serverService.batchAddEndDays(param);
      return CommonResult.ok();
   }

   @Operation(
      summary = "开启云服务器"
   )
   @CommonLog("开启云服务器")
   @PostMapping({"/start"})
   public CommonResult start(@RequestBody ServerIdParam serverIdParam) {
      return this.serverService.startServer(serverIdParam);
   }

   @Operation(
      summary = "关闭云服务器"
   )
   @CommonLog("关闭云服务器")
   @PostMapping({"/stop"})
   public CommonResult stop(@RequestBody ServerIdParam serverIdParam) {
      return this.serverService.closeServer(serverIdParam);
   }

   @Operation(
      summary = "删除云服务器"
   )
   @CommonLog("删除云服务器")
   @PostMapping({"/delete"})
   public CommonResult delete(@RequestBody ServerIdParam serverIdParam) {
      return this.serverService.deleteServer(serverIdParam);
   }

   @Operation(
      summary = "查询云服务器监控"
   )
   @CommonLog("查询云服务器监控")
   @PostMapping({"/monitor"})
   public CommonResult<HostMonitorResult> monitor(@RequestBody ServerMonitorParam monitorParam) {
      return this.serverService.monitorServer(monitorParam);
   }

   @Operation(
      summary = "云服务器监控AI诊断"
   )
   @CommonLog("云服务器监控AI诊断")
   @PostMapping({"/monitor/ai/diagnose"})
   public CommonResult<MonitorAiDiagnosisResult> monitorAiDiagnose(@RequestBody Object requestBody) {
      return this.monitorAiDiagnosisService.diagnose(requestBody);
   }

   @Operation(
      summary = "云服务器监控AI对话"
   )
   @CommonLog("云服务器监控AI对话")
   @PostMapping({"/monitor/ai/chat"})
   public CommonResult<MonitorAiChatResult> monitorAiChat(@RequestBody Object requestBody) {
      return this.monitorAiDiagnosisService.chat(requestBody);
   }

   @Operation(
      summary = "重装云服务器系统"
   )
   @CommonLog("重装云服务器系统")
   @PostMapping({"/reinstall"})
   public CommonResult reinstall(@RequestBody ReinstallEditParam reinstallEditParam) {
      return this.serverService.reinstallServer(reinstallEditParam);
   }

   @Operation(
      summary = "锁定云服务器"
   )
   @CommonLog("锁定云服务器")
   @PostMapping({"/lock"})
   public CommonResult lock(@RequestBody ServerLockParam serverLockParam) {
      return this.serverService.lockServer(serverLockParam);
   }

   @Operation(
      summary = "解锁云服务器"
   )
   @CommonLog("解锁云服务器")
   @PostMapping({"/unlock"})
   public CommonResult unlock(@RequestBody ServerIdParam serverIdParam) {
      return this.serverService.unlockServer(serverIdParam);
   }

   @Operation(
      summary = "重建云服务器"
   )
   @CommonLog("重建云服务器")
   @PostMapping({"/rebuild"})
   public CommonResult rebuild(@RequestBody ServerIdParam memberServerIdParam) {
      return this.serverService.rebuildServer(memberServerIdParam);
   }

   @Operation(
      summary = "修改云服务器密码"
   )
   @CommonLog("修改云服务器密码")
   @PostMapping({"/updatePassword"})
   public CommonResult updatePassword(@RequestBody PasswordEditParam passwordEditParam) {
      return this.serverService.updatePasswordServer(passwordEditParam);
   }

   @Operation(
      summary = "重启云服务器"
   )
   @CommonLog("重启云服务器")
   @PostMapping({"/restart"})
   public CommonResult restart(@RequestBody ServerIdParam serverIdParam) {
      return this.serverService.restartServer(serverIdParam);
   }

   @Operation(
      summary = "断开云服务器电源"
   )
   @CommonLog("断开云服务器电源")
   @PostMapping({"/power"})
   public CommonResult power(@RequestBody ServerIdParam serverIdParam) {
      return this.serverService.powerServer(serverIdParam);
   }

   @Operation(
      summary = "获取云服务器运行图片"
   )
   @CommonLog("获取云服务器运行图片")
   @PostMapping({"/thumbnail"})
   public CommonResult thumbnail(@RequestBody ServerIdParam serverIdParam) {
      return this.serverService.thumbnailServer(serverIdParam);
   }

   @Operation(
      summary = "同步云服务器状态"
   )
   @CommonLog("同步云服务器状态")
   @PostMapping({"/sync"})
   public CommonResult sync(@RequestBody ServerIdParam serverIdParam) {
      return this.serverService.syncStatusServer(serverIdParam);
   }

   @Operation(
      summary = "同步上游云服务器订单"
   )
   @CommonLog("同步上游云服务器订单")
   @PostMapping({"/syncUpstreamOrder"})
   public CommonResult syncUpstreamOrder(@RequestBody ServerIdParam serverIdParam) {
      return this.serverService.syncUpstreamOrder(serverIdParam);
   }

   @Operation(
      summary = "清除运行状态（回滚为运行中）"
   )
   @CommonLog("清除云服务器运行状态")
   @PostMapping({"/clearStatus"})
   public CommonResult clearStatus(@RequestBody ServerIdParam serverIdParam) {
      return this.serverService.clearStatusServer(serverIdParam);
   }

   @Operation(
      summary = "断开云服务器网络"
   )
   @CommonLog("断开云服务器网络")
   @PostMapping({"/disconnect"})
   public CommonResult disconnect(@RequestBody ServerIdParam serverIdParam) {
      return this.serverService.disconnectServer(serverIdParam);
   }

   @Operation(
      summary = "恢复云服务器网络"
   )
   @CommonLog("恢复云服务器网络")
   @PostMapping({"/reconnect"})
   public CommonResult reconnect(@RequestBody ServerIdParam serverIdParam) {
      return this.serverService.reconnectServer(serverIdParam);
   }

   @Operation(
      summary = "获取云服务器VNC地址"
   )
   @CommonLog("获取云服务器VNC地址")
   @PostMapping({"/vnc"})
   public CommonResult vnc(@RequestBody ServerIdParam serverIdParam) {
      return this.serverService.vncUrl(serverIdParam);
   }

   @Operation(
      summary = "创建云服务器备份"
   )
   @CommonLog("创建云服务器备份")
   @PostMapping({"/backup/create"})
   public CommonResult createBackup(@RequestBody ServerBackupCreateParam serverBackupCreateParam) {
      return this.serverService.createBackup(serverBackupCreateParam);
   }

   @Operation(
      summary = "还原云服务器备份"
   )
   @CommonLog("还原云服务器备份")
   @PostMapping({"/backup/restore"})
   public CommonResult restoreBackup(@RequestBody ServerBackupIdParam serverBackupIdParam) {
      return this.serverService.restoreBackup(serverBackupIdParam);
   }

   @Operation(
      summary = "删除云服务器备份"
   )
   @CommonLog("删除云服务器备份")
   @PostMapping({"/backup/delete"})
   public CommonResult deleteBackup(@RequestBody ServerBackupIdParam serverBackupIdParam) {
      return this.serverService.deleteBackup(serverBackupIdParam);
   }

   @Operation(
      summary = "查询云服务器备份列表"
   )
   @PostMapping({"/backup/list"})
   public CommonResult listBackup(@RequestBody ServerIdParam serverIdParam) {
      return this.serverService.listBackup(serverIdParam);
   }

   @Operation(
      summary = "创建云服务器快照"
   )
   @CommonLog("创建云服务器快照")
   @PostMapping({"/snapshot/create"})
   public CommonResult createSnapshot(@RequestBody ServerSnapshotCreateParam serverSnapshotCreateParam) {
      return this.serverService.createSnapshot(serverSnapshotCreateParam);
   }

   @Operation(
      summary = "还原云服务器快照"
   )
   @CommonLog("还原云服务器快照")
   @PostMapping({"/snapshot/restore"})
   public CommonResult restoreSnapshot(@RequestBody ServerSnapshotIdParam serverSnapshotIdParam) {
      return this.serverService.restoreSnapshot(serverSnapshotIdParam);
   }

   @Operation(
      summary = "删除云服务器快照"
   )
   @CommonLog("删除云服务器快照")
   @PostMapping({"/snapshot/delete"})
   public CommonResult deleteSnapshot(@RequestBody ServerSnapshotIdParam serverSnapshotIdParam) {
      return this.serverService.deleteSnapshot(serverSnapshotIdParam);
   }

   @Operation(
      summary = "查询云服务器快照列表"
   )
   @PostMapping({"/snapshot/list"})
   public CommonResult listSnapshot(@RequestBody ServerIdParam serverIdParam) {
      return this.serverService.listSnapshot(serverIdParam);
   }

   @Operation(
      summary = "创建云服务器转让记录"
   )
   @CommonLog("创建云服务器转让记录")
   @PostMapping({"/plus"})
   public CommonResult plus(@RequestBody ServerTransferRecordAddParam serverTransferRecordAddParam) {
      return this.serverService.plus(serverTransferRecordAddParam);
   }

   @Operation(
      summary = "审核云服务器转让"
   )
   @CommonLog("审核云服务器转让")
   @PostMapping({"/updateStatus"})
   public CommonResult updateStatus(@RequestBody ServerTransferRecordIdParam serverTransferRecordIdParam) {
      return this.serverService.updateStatus(serverTransferRecordIdParam);
   }

   @Operation(
      summary = "编辑云服务器备注（管理员）"
   )
   @CommonLog("编辑云服务器备注")
   @PostMapping({"/editRemark"})
   public CommonResult<String> editRemark(@RequestBody ServerRemarkEditParam serverRemarkEditParam) {
      this.serverService.editRemark(serverRemarkEditParam);
      return CommonResult.ok("修改备注成功");
   }

   @Operation(
      summary = "查询云服务器网络历史数据"
   )
   @CommonLog("查询云服务器网络历史数据")
   @PostMapping({"/networkHistory"})
   public CommonResult<List<ServerNetworkHistoryResult>> networkHistory(@RequestBody ServerIdParam serverIdParam) {
      return this.serverService.getNetworkHistory(serverIdParam);
   }

   @Operation(
      summary = "查询云服务器CPU历史数据"
   )
   @CommonLog("查询云服务器CPU历史数据")
   @PostMapping({"/cpuHistory"})
   public CommonResult<List<ServerCpuHistoryResult>> cpuHistory(@RequestBody ServerIdParam serverIdParam) {
      return this.serverService.getCpuHistory(serverIdParam);
   }
}
