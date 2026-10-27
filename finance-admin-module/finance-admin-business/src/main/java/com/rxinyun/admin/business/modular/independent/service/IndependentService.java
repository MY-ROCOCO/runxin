package com.rxinyun.admin.business.modular.independent.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rxinyun.admin.business.modular.independent.entity.Independent;
import com.rxinyun.admin.business.modular.independent.param.IndependentAddEndDaysParam;
import com.rxinyun.admin.business.modular.independent.param.IndependentAddParam;
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
import com.rxinyun.business.core.pojo.BaseIndependent;
import com.rxinyun.common.pojo.CommonResult;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface IndependentService extends IService<Independent> {
   Page<IndependentResponse> page(IndependentPageParam pageParam);

   List<Independent> list(IndependentIdListParam independentIdListParam);

   void add(IndependentAddParam serverAddParam);

   void edit(IndependentEditParam serverEditParam);

   IndependentResponse detail(IndependentIdParam serverIdParam);

   Independent queryEntity(String id);

   String findLatestOrderNoByServerId(String serverId);

   void editRemark(RemarkEditParam remarkEditParam);

   void updateRenewPrice(IndependentRenewPriceParam param);

   void updateEndTime(IndependentEndTimeParam param);

   void batchAddEndDays(IndependentAddEndDaysParam param);

   CommonResult updatePasswordServer(IndependentPasswordEditParam passwordEditParam);

   Long getServerCountByCreateTime(LocalDateTime start, LocalDateTime end);

   List<Independent> getServersToCheck(LocalDateTime sevenDaysLater);

   List<Independent> getServersForAutoRenew(LocalDateTime windowEnd);

   void isRenew(IndependentRenewAutoParam param);

   void updateServerStatus(String serverId);

   void updateServerStatus(String serverId, Integer operateType);

   List<Independent> getServersToDeleteCheck();

   CommonResult syncUpstreamOrder(IndependentIdParam independentIdParam);

   CommonResult deleteServer(IndependentIdParam independentIdParam);

   CommonResult deleteServer(IndependentIdParam independentIdParam, Integer operateType);

   CommonResult upgradeBandwidthServer(String orderNo, String serverId, Integer bandwidth, BigDecimal renewPrice);

   CommonResult upgradeIpServer(String orderNo, String serverId, Integer ipSize, BigDecimal renewPrice);

   CommonResult renewServer(String orderNo, String serverId, Integer months, String unit, BigDecimal newMonthlyRenewPrice);

   CommonResult upgradeDefenseServer(String orderNo, String serverId, Integer defense, BigDecimal renewPrice);

   void applyLocalResourcePoolAfterUpstreamSuccess(String operationType, String serverId, String requestParamsJson, String orderNo);

   void syncFromUpstream(BaseIndependent upstream);

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

   CommonResult adminStartIndependent(IndependentIdParam param);

   CommonResult adminStopIndependent(IndependentIdParam param);

   CommonResult adminRestartIndependent(IndependentIdParam param);

   CommonResult adminPowerIndependent(IndependentIdParam param);

   CommonResult adminReinstallIndependent(IndependentReinstallEditParam param);

   CommonResult adminLockIndependent(IndependentLockParam param);

   CommonResult adminUnlockIndependent(IndependentIdParam param);
}
