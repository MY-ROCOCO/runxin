package com.rxinyun.business.api;

import com.rxinyun.business.core.pojo.BaseMemberAddServer;
import com.rxinyun.business.core.pojo.BasePoolPasswordParam;
import com.rxinyun.business.core.pojo.BasePoolPortAddParam;
import com.rxinyun.business.core.pojo.BasePoolPortDeleteParam;
import com.rxinyun.business.core.pojo.BasePoolReinstallParam;
import com.rxinyun.business.core.pojo.BaseServer;
import com.rxinyun.common.pojo.CommonResult;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

public interface ServerApi {
   Boolean createServer(BaseMemberAddServer baseMemberAddServer);

   BaseServer detail(String serverId);

   BaseServer getById(String serverId);

   void save(BaseServer server);

   void updateById(BaseServer server);

   Long getServerCount(HashMap<String, LocalDateTime> map);

   List<BaseServer> getServersToCheck(LocalDateTime sevenDaysLater);

   List<BaseServer> getServersForAutoRenew(LocalDateTime windowEnd);

   void updateServerStatus(String serverId);

   default void updateServerStatus(String serverId, String taskDesc) {
      this.updateServerStatus(serverId);
   }

   List<BaseServer> getServersToDeleteCheck();

   void deleteServer(String id);

   default void deleteServer(String id, String taskDesc) {
      this.deleteServer(id);
   }

   void finalizeRefundCloudIpBindings(String serverId);

   void saveOrUpdate(BaseServer server);

   Boolean upgradeHardDisksServer(String orderNo, String serverId, Integer systemDisk, List<Integer> dataDisks, BigDecimal renewPrice);

   Boolean upgradeBandwidthServer(String orderNo, String serverId, Integer bandwidth, BigDecimal renewPrice);

   Boolean upgradeCpuRamServer(String orderNo, String serverId, Integer cpu, Integer ram, BigDecimal renewPrice);

   Boolean upgradeDefenseServer(String orderNo, String serverId, Integer defense, BigDecimal renewPrice);

   Boolean renewServer(String orderNo, String serverId, Integer months);

   void syncFromUpstream(BaseServer server);

   BaseServer selectByServerId(String serverId);

   CommonResult startCloudServer(String serverId);

   CommonResult stopCloudServer(String serverId);

   CommonResult restartCloudServer(String serverId);

   CommonResult powerCloudServer(String serverId);

   CommonResult<?> monitorCloudServer(String serverId);

   CommonResult reinstallCloudServer(BasePoolReinstallParam param);

   CommonResult lockCloudServer(String serverId, String lockReason);

   CommonResult unlockCloudServer(String serverId);

   CommonResult rebuildCloudServer(String serverId);

   CommonResult rebuildCloudServer(String serverId, Integer fullRebuild);

   CommonResult updateCloudServerPassword(BasePoolPasswordParam param);

   CommonResult thumbnailCloudServer(String serverId);

   CommonResult syncCloudServerStatus(String serverId);

   CommonResult clearCloudServerStatus(String serverId);

   CommonResult disconnectCloudServerNetwork(String serverId);

   CommonResult reconnectCloudServerNetwork(String serverId);

   CommonResult resetIpCloudServer(String serverId);

   CommonResult vncCloudServer(String serverId);

   CommonResult<?> networkHistoryCloudServer(String serverId);

   CommonResult<?> cpuHistoryCloudServer(String serverId);

   CommonResult<?> createBackupCloudServer(String serverId, String backupName);

   CommonResult<?> restoreBackupCloudServer(String serverId, String backupName);

   CommonResult<?> deleteBackupCloudServer(String serverId, String backupName);

   CommonResult<?> createSnapshotCloudServer(String serverId, String snapshotName);

   CommonResult<?> restoreSnapshotCloudServer(String serverId, String snapshotName);

   CommonResult<?> deleteSnapshotCloudServer(String serverId, String snapshotName);

   CommonResult<?> addPortMappingCloudServer(BasePoolPortAddParam param);

   CommonResult<?> deletePortMappingCloudServer(BasePoolPortDeleteParam param);
}
