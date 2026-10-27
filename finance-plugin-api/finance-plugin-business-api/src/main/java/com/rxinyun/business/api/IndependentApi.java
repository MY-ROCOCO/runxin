package com.rxinyun.business.api;

import com.rxinyun.business.core.pojo.BaseIndependent;
import com.rxinyun.business.core.pojo.BaseIndependentIp;
import com.rxinyun.business.core.pojo.BaseMemberIndependentAddServer;
import com.rxinyun.common.pojo.CommonResult;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface IndependentApi {
   BaseIndependent getById(String serverId);

   List<BaseIndependentIp> listIpsByIndependentServerId(String serverId);

   void syncFromUpstream(BaseIndependent independent);

   void save(BaseIndependent independent);

   void saveOrUpdate(BaseIndependent independent);

   Long getServerCount(HashMap<String, LocalDateTime> map);

   List<BaseIndependent> getServersToCheck(LocalDateTime sevenDaysLater);

   List<BaseIndependent> getServersForAutoRenew(LocalDateTime windowEnd);

   void updateServerStatus(String serverId);

   default void updateServerStatus(String serverId, Integer operateType) {
      this.updateServerStatus(serverId);
   }

   List<BaseIndependent> getServersToDeleteCheck();

   void deleteServer(String id);

   default void deleteServer(String id, Integer operateType) {
      this.deleteServer(id);
   }

   BaseIndependent getOne(Map<String, Object> queryWrapper);

   Boolean createServer(BaseMemberIndependentAddServer baseMemberIndependentAddServer);

   Boolean upgradeIndependentBandwidthServer(String orderNo, String serverId, Integer bandwidth, BigDecimal renewPrice);

   Boolean upgradeIndependentIpServer(String orderNo, String serverId, Integer ipSize, BigDecimal renewPrice);

   Boolean renewServer(String orderNo, String serverId, Integer months, String unit, BigDecimal newMonthlyRenewPrice);

   Boolean upgradeIndependentDefenseServer(String orderNo, String serverId, Integer defense, BigDecimal renewPrice);

   CommonResult poolApiStartIndependent(Long memberNo, String serverId);

   CommonResult poolApiStopIndependent(Long memberNo, String serverId);

   CommonResult poolApiRestartIndependent(Long memberNo, String serverId);

   CommonResult poolApiPowerIndependent(Long memberNo, String serverId);

   CommonResult poolApiReinstallIndependent(Long memberNo, String serverId, String osClassId, String osVersionId, String osPassword);

   CommonResult poolApiUpdatePasswordIndependent(Long memberNo, String serverId, String osPassword);

   CommonResult poolApiLockIndependent(Long memberNo, String serverId, String lockReason);

   CommonResult poolApiUnlockIndependent(Long memberNo, String serverId);

   CommonResult forwardPoolIndependentStart(Long memberNo, String serverId);

   CommonResult forwardPoolIndependentStop(Long memberNo, String serverId);

   CommonResult forwardPoolIndependentRestart(Long memberNo, String serverId);

   CommonResult forwardPoolIndependentPower(Long memberNo, String serverId);

   CommonResult forwardPoolIndependentReinstall(Long memberNo, String serverId, String osClassId, String osVersionId, String osPassword);

   CommonResult forwardPoolIndependentUpdatePassword(Long memberNo, String serverId, String osPassword);

   CommonResult forwardPoolIndependentLock(Long memberNo, String serverId, String lockReason);

   CommonResult forwardPoolIndependentUnlock(Long memberNo, String serverId);
}
