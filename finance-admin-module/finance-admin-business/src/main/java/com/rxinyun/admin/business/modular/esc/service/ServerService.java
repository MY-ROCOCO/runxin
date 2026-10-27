package com.rxinyun.admin.business.modular.esc.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rxinyun.admin.business.modular.esc.entity.Server;
import com.rxinyun.admin.business.modular.esc.entity.ServerBackupRecord;
import com.rxinyun.admin.business.modular.esc.entity.ServerSnapshotRecord;
import com.rxinyun.admin.business.modular.esc.param.PasswordEditParam;
import com.rxinyun.admin.business.modular.esc.param.ReinstallEditParam;
import com.rxinyun.admin.business.modular.esc.param.ServerAddEndDaysParam;
import com.rxinyun.admin.business.modular.esc.param.ServerAddParam;
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
import com.rxinyun.admin.business.modular.esc.param.ServerRenewParam;
import com.rxinyun.admin.business.modular.esc.param.ServerRenewPriceParam;
import com.rxinyun.admin.business.modular.esc.param.ServerSnapshotCreateParam;
import com.rxinyun.admin.business.modular.esc.param.ServerSnapshotIdParam;
import com.rxinyun.admin.business.modular.esc.param.ServerTransferRecordAddParam;
import com.rxinyun.admin.business.modular.esc.param.ServerTransferRecordIdParam;
import com.rxinyun.admin.business.modular.esc.param.ServerUpgradeParam;
import com.rxinyun.common.pojo.CommonResult;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface ServerService extends IService<Server> {
   Page<Server> page(ServerPageParam pageParam);

   void add(ServerAddParam serverAddParam);

   void edit(ServerEditParam serverEditParam);

   Server detail(ServerIdParam serverIdParam);

   Server queryEntity(String id);

   Long getServerCountByCreateTime(LocalDateTime startTime, LocalDateTime endTime);

   CommonResult startServer(ServerIdParam serverIdParam);

   CommonResult closeServer(ServerIdParam serverIdParam);

   CommonResult deleteServer(ServerIdParam serverIdParam);

   CommonResult deleteServer(ServerIdParam serverIdParam, String taskDesc);

   CommonResult monitorServer(ServerMonitorParam monitorParam);

   CommonResult reinstallServer(ReinstallEditParam reinstallEditParam);

   CommonResult lockServer(ServerLockParam serverLockParam);

   CommonResult unlockServer(ServerIdParam serverIdParam);

   CommonResult rebuildServer(ServerIdParam serverIdParam);

   CommonResult updatePasswordServer(PasswordEditParam passwordEditParam);

   CommonResult restartServer(ServerIdParam serverIdParam);

   CommonResult powerServer(ServerIdParam serverIdParam);

   CommonResult thumbnailServer(ServerIdParam serverIdParam);

   CommonResult syncStatusServer(ServerIdParam serverIdParam);

   CommonResult syncUpstreamOrder(ServerIdParam serverIdParam);

   CommonResult clearStatusServer(ServerIdParam serverIdParam);

   CommonResult disconnectServer(ServerIdParam serverIdParam);

   CommonResult reconnectServer(ServerIdParam serverIdParam);

   CommonResult resetIpServer(ServerIdParam serverIdParam);

   CommonResult vncUrl(ServerIdParam serverIdParam);

   CommonResult<ServerBackupRecord> createBackup(ServerBackupCreateParam serverBackupCreateParam);

   CommonResult restoreBackup(ServerBackupIdParam serverBackupIdParam);

   CommonResult deleteBackup(ServerBackupIdParam serverBackupIdParam);

   CommonResult<List<ServerBackupRecord>> listBackup(ServerIdParam serverIdParam);

   CommonResult<ServerSnapshotRecord> createSnapshot(ServerSnapshotCreateParam serverSnapshotCreateParam);

   CommonResult restoreSnapshot(ServerSnapshotIdParam serverSnapshotIdParam);

   CommonResult deleteSnapshot(ServerSnapshotIdParam serverSnapshotIdParam);

   CommonResult<List<ServerSnapshotRecord>> listSnapshot(ServerIdParam serverIdParam);

   CommonResult upgradeServer(ServerUpgradeParam serverUpgradeParam);

   List<Server> getServersToCheck(LocalDateTime sevenDaysLater);

   List<Server> getServersForAutoRenew(LocalDateTime windowEnd);

   void isRenew(ServerRenewAutoParam param);

   void updateServerStatus(String serverId);

   void updateServerStatus(String serverId, String taskDesc);

   void updateRenewPrice(ServerRenewPriceParam serverRenewPriceParam);

   void updateEndTime(ServerEndTimeParam serverEndTimeParam);

   void batchAddEndDays(ServerAddEndDaysParam param);

   List<Server> getServersToDeleteCheck();

   CommonResult plus(ServerTransferRecordAddParam serverTransferRecordAddParam);

   CommonResult updateStatus(ServerTransferRecordIdParam serverTransferRecordIdParam);

   void editRemark(ServerRemarkEditParam serverRemarkEditParam);

   CommonResult renewServer(String orderNo, String serverId, Integer months);

   CommonResult renewServer(ServerRenewParam serverRenewParam);

   CommonResult upgradeHardDisksServer(String orderNo, String serverId, Integer systemDisk, List<Integer> dataDisks, BigDecimal renewPrice);

   CommonResult upgradeBandwidthServer(String orderNo, String serverId, Integer bandwidth, BigDecimal renewPrice);

   CommonResult upgradeCpuRamServer(String orderNo, String serverId, Integer cpu, Integer ram, BigDecimal renewPrice);

   CommonResult upgradeDefenseServer(String orderNo, String serverId, Integer defense, BigDecimal renewPrice);

   CommonResult<List<ServerNetworkHistoryResult>> getNetworkHistory(ServerIdParam serverIdParam);

   CommonResult<List<ServerCpuHistoryResult>> getCpuHistory(ServerIdParam serverIdParam);

   void updateServerStatusAndLog(String serverId, String serverStatus);

   void saveResourceChangeLogAfterDeleteSuccess(String serverId);

   void releaseMacAfterDelete(String serverId);

   void applyLocalResourcePoolAfterUpstreamSuccess(String operationType, String serverId, String requestParamsJson, String orderNo);
}
