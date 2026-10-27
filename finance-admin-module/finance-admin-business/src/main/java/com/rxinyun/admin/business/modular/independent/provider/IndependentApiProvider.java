package com.rxinyun.admin.business.modular.independent.provider;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rxinyun.admin.business.modular.independent.entity.Independent;
import com.rxinyun.admin.business.modular.independent.entity.IndependentIp;
import com.rxinyun.admin.business.modular.independent.param.IndependentAddParam;
import com.rxinyun.admin.business.modular.independent.param.IndependentIdParam;
import com.rxinyun.admin.business.modular.independent.service.IndependentIpService;
import com.rxinyun.admin.business.modular.independent.service.IndependentService;
import com.rxinyun.business.api.IndependentApi;
import com.rxinyun.business.core.pojo.BaseIndependent;
import com.rxinyun.business.core.pojo.BaseIndependentIp;
import com.rxinyun.business.core.pojo.BaseMemberIndependentAddServer;
import com.rxinyun.common.pojo.CommonResult;
import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class IndependentApiProvider implements IndependentApi {
   private static final Logger log = LoggerFactory.getLogger(IndependentApiProvider.class);
   @Resource
   private IndependentService independentService;
   @Resource
   private IndependentIpService independentIpService;

   public BaseIndependent getById(String serverId) {
      Independent independent = (Independent)this.independentService.getById(serverId);
      if (independent == null) {
         independent = (Independent)this.independentService.getOne((Wrapper<Independent>)new LambdaQueryWrapper<Independent>().eq(Independent::getUpstreamServerId, serverId));
      }

      if (independent == null) {
         return null;
      } else {
         BaseIndependent baseIndependent = new BaseIndependent();
         BeanUtils.copyProperties(independent, baseIndependent);
         return baseIndependent;
      }
   }

   public List<BaseIndependentIp> listIpsByIndependentServerId(String serverId) {
      return serverId != null && !serverId.isEmpty()
         ? this.independentIpService.listAllByServerId(serverId).stream().map(this::toBaseIndependentIp).collect(Collectors.toList())
         : Collections.emptyList();
   }

   private BaseIndependentIp toBaseIndependentIp(IndependentIp row) {
      BaseIndependentIp b = new BaseIndependentIp();
      if (row == null) {
         return b;
      } else {
         BeanUtils.copyProperties(row, b);
         return b;
      }
   }

   public void syncFromUpstream(BaseIndependent independent) {
      this.independentService.syncFromUpstream(independent);
   }

   public void save(BaseIndependent independent) {
      Independent entity = new Independent();
      BeanUtils.copyProperties(independent, entity);
      this.independentService.save(entity);
   }

   public void saveOrUpdate(BaseIndependent independent) {
      Independent entity = new Independent();
      BeanUtils.copyProperties(independent, entity);
      this.independentService.saveOrUpdate(entity);
   }

   public Long getServerCount(HashMap<String, LocalDateTime> map) {
      LocalDateTime start = map.get("startTime");
      LocalDateTime end = map.get("endTime");
      return this.independentService.getServerCountByCreateTime(start, end);
   }

   public List<BaseIndependent> getServersToCheck(LocalDateTime sevenDaysLater) {
      return this.independentService.getServersToCheck(sevenDaysLater).stream().map(independent -> {
         BaseIndependent baseIndependent = new BaseIndependent();
         BeanUtils.copyProperties(independent, baseIndependent);
         return baseIndependent;
      }).collect(Collectors.toList());
   }

   public List<BaseIndependent> getServersForAutoRenew(LocalDateTime windowEnd) {
      return this.independentService.getServersForAutoRenew(windowEnd).stream().map(independent -> {
         BaseIndependent baseIndependent = new BaseIndependent();
         BeanUtils.copyProperties(independent, baseIndependent);
         return baseIndependent;
      }).collect(Collectors.toList());
   }

   public void updateServerStatus(String serverId) {
      this.independentService.updateServerStatus(serverId);
   }

   public void updateServerStatus(String serverId, Integer operateType) {
      this.independentService.updateServerStatus(serverId, operateType);
   }

   public List<BaseIndependent> getServersToDeleteCheck() {
      return this.independentService.getServersToDeleteCheck().stream().map(independent -> {
         BaseIndependent baseIndependent = new BaseIndependent();
         BeanUtils.copyProperties(independent, baseIndependent);
         return baseIndependent;
      }).collect(Collectors.toList());
   }

   public void deleteServer(String id) {
      IndependentIdParam independentIdParam = new IndependentIdParam();
      independentIdParam.setId(id);
      this.independentService.deleteServer(independentIdParam);
   }

   public void deleteServer(String id, Integer operateType) {
      IndependentIdParam independentIdParam = new IndependentIdParam();
      independentIdParam.setId(id);
      this.independentService.deleteServer(independentIdParam, operateType);
   }

   public BaseIndependent getOne(Map<String, Object> queryMap) {
      QueryWrapper<Independent> wrapper = new QueryWrapper();
      queryMap.forEach(wrapper::eq);
      Independent independent = (Independent)this.independentService.getOne(wrapper);
      if (independent == null) {
         return null;
      } else {
         BaseIndependent baseIndependent = new BaseIndependent();
         BeanUtils.copyProperties(independent, baseIndependent);
         return baseIndependent;
      }
   }

   public Boolean createServer(BaseMemberIndependentAddServer baseMemberIndependentAddServer) {
      try {
         IndependentAddParam memberIndependentAddParam = (IndependentAddParam)BeanUtil.toBean(baseMemberIndependentAddServer, IndependentAddParam.class);
         this.independentService.add(memberIndependentAddParam);
         baseMemberIndependentAddServer.setId(memberIndependentAddParam.getId());
      } catch (Exception var3) {
         log.error("创建服务器失败", var3);
         return false;
      }

      return true;
   }

   public Boolean upgradeIndependentBandwidthServer(String orderNo, String serverId, Integer bandwidth, BigDecimal renewPrice) {
      try {
         CommonResult<?> result = this.independentService.upgradeBandwidthServer(orderNo, serverId, bandwidth, renewPrice);
         return this.isSuccess(result, "升级带宽", orderNo, serverId);
      } catch (Exception var6) {
         log.error("升级服务器失败", var6);
         return false;
      }
   }

   public Boolean upgradeIndependentIpServer(String orderNo, String serverId, Integer ipSize, BigDecimal renewPrice) {
      try {
         CommonResult<?> result = this.independentService.upgradeIpServer(orderNo, serverId, ipSize, renewPrice);
         return this.isSuccess(result, "升级IP", orderNo, serverId);
      } catch (Exception var6) {
         log.error("升级服务器失败", var6);
         return false;
      }
   }

   public Boolean renewServer(String orderNo, String serverId, Integer months, String unit, BigDecimal newMonthlyRenewPrice) {
      try {
         CommonResult<?> result = this.independentService.renewServer(orderNo, serverId, months, unit, newMonthlyRenewPrice);
         return this.isSuccess(result, "续费", orderNo, serverId);
      } catch (Exception var7) {
         log.error("升级服务器失败", var7);
         return false;
      }
   }

   public Boolean upgradeIndependentDefenseServer(String orderNo, String serverId, Integer defense, BigDecimal renewPrice) {
      try {
         CommonResult<?> result = this.independentService.upgradeDefenseServer(orderNo, serverId, defense, renewPrice);
         return this.isSuccess(result, "升级防御", orderNo, serverId);
      } catch (Exception var6) {
         log.error("升级服务器失败", var6);
         return false;
      }
   }

   private boolean isSuccess(CommonResult<?> result, String action, String orderNo, String serverId) {
      if (result != null && 200 == result.getCode()) {
         return true;
      } else {
         String msg = result == null ? "返回结果为空" : result.getMsg();
         log.warn("独立服务器{}失败，orderNo={}，serverId={}，msg={}", new Object[]{action, orderNo, serverId, msg});
         return false;
      }
   }

   public CommonResult poolApiStartIndependent(Long memberNo, String serverId) {
      return this.independentService.poolApiStartIndependent(memberNo, serverId);
   }

   public CommonResult poolApiStopIndependent(Long memberNo, String serverId) {
      return this.independentService.poolApiStopIndependent(memberNo, serverId);
   }

   public CommonResult poolApiRestartIndependent(Long memberNo, String serverId) {
      return this.independentService.poolApiRestartIndependent(memberNo, serverId);
   }

   public CommonResult poolApiPowerIndependent(Long memberNo, String serverId) {
      return this.independentService.poolApiPowerIndependent(memberNo, serverId);
   }

   public CommonResult poolApiReinstallIndependent(Long memberNo, String serverId, String osClassId, String osVersionId, String osPassword) {
      return this.independentService.poolApiReinstallIndependent(memberNo, serverId, osClassId, osVersionId, osPassword);
   }

   public CommonResult poolApiUpdatePasswordIndependent(Long memberNo, String serverId, String osPassword) {
      return this.independentService.poolApiUpdatePasswordIndependent(memberNo, serverId, osPassword);
   }

   public CommonResult poolApiLockIndependent(Long memberNo, String serverId, String lockReason) {
      return this.independentService.poolApiLockIndependent(memberNo, serverId, lockReason);
   }

   public CommonResult poolApiUnlockIndependent(Long memberNo, String serverId) {
      return this.independentService.poolApiUnlockIndependent(memberNo, serverId);
   }

   public CommonResult forwardPoolIndependentStart(Long memberNo, String serverId) {
      return this.independentService.forwardPoolIndependentStart(memberNo, serverId);
   }

   public CommonResult forwardPoolIndependentStop(Long memberNo, String serverId) {
      return this.independentService.forwardPoolIndependentStop(memberNo, serverId);
   }

   public CommonResult forwardPoolIndependentRestart(Long memberNo, String serverId) {
      return this.independentService.forwardPoolIndependentRestart(memberNo, serverId);
   }

   public CommonResult forwardPoolIndependentPower(Long memberNo, String serverId) {
      return this.independentService.forwardPoolIndependentPower(memberNo, serverId);
   }

   public CommonResult forwardPoolIndependentReinstall(Long memberNo, String serverId, String osClassId, String osVersionId, String osPassword) {
      return this.independentService.forwardPoolIndependentReinstall(memberNo, serverId, osClassId, osVersionId, osPassword);
   }

   public CommonResult forwardPoolIndependentUpdatePassword(Long memberNo, String serverId, String osPassword) {
      return this.independentService.forwardPoolIndependentUpdatePassword(memberNo, serverId, osPassword);
   }

   public CommonResult forwardPoolIndependentLock(Long memberNo, String serverId, String lockReason) {
      return this.independentService.forwardPoolIndependentLock(memberNo, serverId, lockReason);
   }

   public CommonResult forwardPoolIndependentUnlock(Long memberNo, String serverId) {
      return this.independentService.forwardPoolIndependentUnlock(memberNo, serverId);
   }
}
