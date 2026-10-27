package com.rxinyun.admin.business.modular.esc.provider;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rxinyun.admin.business.modular.esc.entity.Server;
import com.rxinyun.admin.business.modular.esc.entity.ServerBackupRecord;
import com.rxinyun.admin.business.modular.esc.entity.ServerPort;
import com.rxinyun.admin.business.modular.esc.entity.ServerSnapshotRecord;
import com.rxinyun.admin.business.modular.esc.mapper.ServerBackupRecordMapper;
import com.rxinyun.admin.business.modular.esc.mapper.ServerIpMapper;
import com.rxinyun.admin.business.modular.esc.mapper.ServerSnapshotRecordMapper;
import com.rxinyun.admin.business.modular.esc.param.PasswordEditParam;
import com.rxinyun.admin.business.modular.esc.param.ReinstallEditParam;
import com.rxinyun.admin.business.modular.esc.param.ServerAddParam;
import com.rxinyun.admin.business.modular.esc.param.ServerBackupCreateParam;
import com.rxinyun.admin.business.modular.esc.param.ServerBackupIdParam;
import com.rxinyun.admin.business.modular.esc.param.ServerIdParam;
import com.rxinyun.admin.business.modular.esc.param.ServerIpAddParam;
import com.rxinyun.admin.business.modular.esc.param.ServerLockParam;
import com.rxinyun.admin.business.modular.esc.param.ServerMonitorParam;
import com.rxinyun.admin.business.modular.esc.param.ServerPortAddParam;
import com.rxinyun.admin.business.modular.esc.param.ServerPortIdParam;
import com.rxinyun.admin.business.modular.esc.param.ServerSnapshotCreateParam;
import com.rxinyun.admin.business.modular.esc.param.ServerSnapshotIdParam;
import com.rxinyun.admin.business.modular.esc.service.ServerIpService;
import com.rxinyun.admin.business.modular.esc.service.ServerPortService;
import com.rxinyun.admin.business.modular.esc.service.ServerService;
import com.rxinyun.business.api.ServerApi;
import com.rxinyun.business.core.pojo.BaseMemberAddServer;
import com.rxinyun.business.core.pojo.BasePoolPasswordParam;
import com.rxinyun.business.core.pojo.BasePoolPortAddParam;
import com.rxinyun.business.core.pojo.BasePoolPortDeleteParam;
import com.rxinyun.business.core.pojo.BasePoolReinstallParam;
import com.rxinyun.business.core.pojo.BaseServer;
import com.rxinyun.common.enums.business.BusinessStatusEnum;
import com.rxinyun.common.pojo.CommonResult;
import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class ServerApiProvider implements ServerApi {
   private static final Logger log = LoggerFactory.getLogger(ServerApiProvider.class);
   @Resource
   private ServerService serverService;
   @Resource
   private ServerPortService serverPortService;
   @Resource
   private ServerIpService serverIpService;
   @Resource
   private ServerIpMapper serverIpMapper;
   @Resource
   private ServerBackupRecordMapper serverBackupRecordMapper;
   @Resource
   private ServerSnapshotRecordMapper serverSnapshotRecordMapper;

   public Boolean createServer(BaseMemberAddServer baseMemberAddServer) {
      try {
         ServerAddParam memberServerAddParam = (ServerAddParam)BeanUtil.toBean(baseMemberAddServer, ServerAddParam.class);
         this.serverService.add(memberServerAddParam);
         baseMemberAddServer.setId(memberServerAddParam.getId());
      } catch (Exception var3) {
         log.error("创建服务器失败", var3);
         return false;
      }

      return true;
   }

   public BaseServer detail(String serverId) {
      Server server = (Server)this.serverService.getById(serverId);
      if (server == null) {
         return null;
      } else {
         BaseServer baseServer = new BaseServer();
         BeanUtils.copyProperties(server, baseServer);
         return baseServer;
      }
   }

   public BaseServer getById(String serverId) {
      Server server = (Server)this.serverService.getById(serverId);
      if (server == null) {
         return null;
      } else {
         BaseServer baseServer = new BaseServer();
         BeanUtils.copyProperties(server, baseServer);
         return baseServer;
      }
   }

   public void save(BaseServer server) {
      Server entity = new Server();
      BeanUtils.copyProperties(server, entity);
      this.serverService.save(entity);
   }

   public void updateById(BaseServer server) {
      Server entity = new Server();
      BeanUtils.copyProperties(server, entity);
      this.serverService.updateById(entity);
   }

   public Long getServerCount(HashMap<String, LocalDateTime> map) {
      LocalDateTime start = map.get("startTime");
      LocalDateTime end = map.get("endTime");
      return this.serverService.getServerCountByCreateTime(start, end);
   }

   public List<BaseServer> getServersToCheck(LocalDateTime sevenDaysLater) {
      return this.serverService.getServersToCheck(sevenDaysLater).stream().map(server -> {
         BaseServer baseServer = new BaseServer();
         BeanUtils.copyProperties(server, baseServer);
         return baseServer;
      }).collect(Collectors.toList());
   }

   public List<BaseServer> getServersForAutoRenew(LocalDateTime windowEnd) {
      return this.serverService.getServersForAutoRenew(windowEnd).stream().map(server -> {
         BaseServer baseServer = new BaseServer();
         BeanUtils.copyProperties(server, baseServer);
         return baseServer;
      }).collect(Collectors.toList());
   }

   public void updateServerStatus(String serverId) {
      this.serverService.updateServerStatus(serverId);
   }

   public void updateServerStatus(String serverId, String taskDesc) {
      this.serverService.updateServerStatus(serverId, taskDesc);
   }

   public List<BaseServer> getServersToDeleteCheck() {
      return this.serverService.getServersToDeleteCheck().stream().map(server -> {
         BaseServer baseServer = new BaseServer();
         BeanUtils.copyProperties(server, baseServer);
         return baseServer;
      }).collect(Collectors.toList());
   }

   public void deleteServer(String id) {
      ServerIdParam serverIdParam = new ServerIdParam();
      serverIdParam.setId(id);
      this.serverService.deleteServer(serverIdParam);
   }

   public void deleteServer(String id, String taskDesc) {
      ServerIdParam serverIdParam = new ServerIdParam();
      serverIdParam.setId(id);
      this.serverService.deleteServer(serverIdParam, taskDesc);
   }

   public void finalizeRefundCloudIpBindings(String serverId) {
      if (!StrUtil.isBlank(serverId)) {
         this.serverIpMapper.deleteByServerId(serverId);
         log.info("退款收尾：已清空云服务器本地 IP 绑定 serverId={}", serverId);
      }
   }

   public void saveOrUpdate(BaseServer server) {
      Server entity = new Server();
      BeanUtils.copyProperties(server, entity);
      this.serverService.saveOrUpdate(entity);
   }

   public Boolean upgradeHardDisksServer(String orderNo, String serverId, Integer systemDisk, List<Integer> dataDisks, BigDecimal renewPrice) {
      try {
         this.serverService.upgradeHardDisksServer(orderNo, serverId, systemDisk, dataDisks, renewPrice);
      } catch (Exception var7) {
         log.error("升级服务器失败", var7);
         return false;
      }

      return true;
   }

   public Boolean upgradeBandwidthServer(String orderNo, String serverId, Integer bandwidth, BigDecimal renewPrice) {
      try {
         this.serverService.upgradeBandwidthServer(orderNo, serverId, bandwidth, renewPrice);
      } catch (Exception var6) {
         log.error("升级服务器失败", var6);
         return false;
      }

      return true;
   }

   public Boolean upgradeCpuRamServer(String orderNo, String serverId, Integer cpu, Integer ram, BigDecimal renewPrice) {
      try {
         this.serverService.upgradeCpuRamServer(orderNo, serverId, cpu, ram, renewPrice);
      } catch (Exception var7) {
         log.error("升级服务器失败", var7);
         return false;
      }

      return true;
   }

   public Boolean upgradeDefenseServer(String orderNo, String serverId, Integer defense, BigDecimal renewPrice) {
      try {
         this.serverService.upgradeDefenseServer(orderNo, serverId, defense, renewPrice);
      } catch (Exception var6) {
         log.error("升级服务器失败", var6);
         return false;
      }

      return true;
   }

   public Boolean renewServer(String orderNo, String serverId, Integer months) {
      try {
         CommonResult<?> result = this.serverService.renewServer(orderNo, serverId, months);
         if (result != null && 200 == result.getCode()) {
            return true;
         } else {
            log.warn("管理端云服务器续费失败，orderNo={}，serverId={}，msg={}", new Object[]{orderNo, serverId, result == null ? "返回结果为空" : result.getMsg()});
            return false;
         }
      } catch (Exception var5) {
         log.error("云服务器续费失败", var5);
         return false;
      }
   }

   public void syncFromUpstream(BaseServer server) {
      if (server == null) {
         log.warn("⚠️ 上游同步数据为空，跳过处理");
      } else {
         try {
            if (server.getId() == null) {
               Server entity = new Server();
               BeanUtils.copyProperties(server, entity);
               this.serverService.save(entity);
               this.syncBusinessServerIpFromServerRow(entity);
               log.info("✅ 上游数据同步 - 新增服务器成功，ID: {}", entity.getId());
            } else {
               this.updateServerFieldsFromUpstream(server);
               log.info("✅ 上游数据同步 - 更新服务器成功，ID: {}", server.getId());
            }
         } catch (Exception var3) {
            log.error("❌ 上游数据同步失败，服务器 ID: {}，错误：{}", new Object[]{server.getId(), var3.getMessage(), var3});
            throw new RuntimeException("上游数据同步失败：" + var3.getMessage(), var3);
         }
      }
   }

   public BaseServer selectByServerId(String serverId) {
      if (StrUtil.isBlank(serverId)) {
         return null;
      } else {
         Server server = (Server)this.serverService.getOne((Wrapper<Server>)new LambdaQueryWrapper<Server>().eq(Server::getUpstreamServerId, serverId));
         if (server == null) {
            server = (Server)this.serverService.getById(serverId);
         }

         if (server == null) {
            return null;
         } else {
            BaseServer baseServer = new BaseServer();
            BeanUtils.copyProperties(server, baseServer);
            return baseServer;
         }
      }
   }

   private void updateServerFieldsFromUpstream(BaseServer upstreamServer) {
      Server existingServer = (Server)this.serverService.getById(upstreamServer.getId());
      if (existingServer == null) {
         log.warn("⚠️ 未找到待更新的服务器记录，ID: {}，转为新增处理", upstreamServer.getId());
         Server newEntity = new Server();
         BeanUtils.copyProperties(upstreamServer, newEntity);
         this.serverService.save(newEntity);
         this.syncBusinessServerIpFromServerRow(newEntity);
      } else {
         existingServer.setMasterId(upstreamServer.getMasterId());
         if (StrUtil.isNotBlank(upstreamServer.getUpstreamServerId())) {
            existingServer.setUpstreamServerId(upstreamServer.getUpstreamServerId());
         }

         existingServer.setAvailableId(upstreamServer.getAvailableId());
         existingServer.setLineId(upstreamServer.getLineId());
         existingServer.setSystemClassId(upstreamServer.getSystemClassId());
         existingServer.setSystemClass(upstreamServer.getSystemClass());
         existingServer.setSystemId(upstreamServer.getSystemId());
         existingServer.setSystemName(upstreamServer.getSystemName());
         existingServer.setImagePath(upstreamServer.getImagePath());
         existingServer.setImageDirectoryName(upstreamServer.getImageDirectoryName());
         existingServer.setCpu(upstreamServer.getCpu());
         existingServer.setRam(upstreamServer.getRam());
         existingServer.setBandwidth(upstreamServer.getBandwidth());
         existingServer.setBandwidthIn(upstreamServer.getBandwidthIn());
         existingServer.setDefense(upstreamServer.getDefense());
         existingServer.setTraffic(upstreamServer.getTraffic());
         existingServer.setSystemDisk(upstreamServer.getSystemDisk());
         existingServer.setDataDisks(upstreamServer.getDataDisks());
         existingServer.setPublicIp(upstreamServer.getPublicIp());
         existingServer.setPrivateIp(upstreamServer.getPrivateIp());
         existingServer.setPublicGateway(upstreamServer.getPublicGateway());
         existingServer.setPrivateGateway(upstreamServer.getPrivateGateway());
         existingServer.setPublicNetmask(upstreamServer.getPublicNetmask());
         existingServer.setPrivateNetmask(upstreamServer.getPrivateNetmask());
         if (!BusinessStatusEnum.DELETE.getCode().equals(upstreamServer.getBusinessStatus())
            && !BusinessStatusEnum.DELETE.getCode().equals(existingServer.getBusinessStatus())) {
            existingServer.setPublicMac(upstreamServer.getPublicMac());
            existingServer.setPrivateMac(upstreamServer.getPrivateMac());
         } else {
            existingServer.setPublicMac(null);
            existingServer.setPrivateMac(null);
         }

         existingServer.setPort(upstreamServer.getPort());
         existingServer.setSystemUserName(upstreamServer.getSystemUserName());
         existingServer.setSystemPassword(upstreamServer.getSystemPassword());
         existingServer.setServerStatus(upstreamServer.getServerStatus());
         if (upstreamServer.getBusinessStatus() != null) {
            existingServer.setBusinessStatus(upstreamServer.getBusinessStatus());
         }

         if (upstreamServer.getBuyTime() != null) {
            existingServer.setBuyTime(upstreamServer.getBuyTime());
         }

         if (upstreamServer.getEndTime() != null) {
            existingServer.setEndTime(upstreamServer.getEndTime());
         }

         if (upstreamServer.getDeleteTime() != null) {
            existingServer.setDeleteTime(upstreamServer.getDeleteTime());
         }

         existingServer.setShareIp(upstreamServer.getShareIp());
         existingServer.setSyncTimeFlag(upstreamServer.getSyncTimeFlag());
         existingServer.setSystemMaxIops(upstreamServer.getSystemMaxIops());
         existingServer.setDataDisksMaxIops(upstreamServer.getDataDisksMaxIops());
         if (upstreamServer.getPublicGateway() != null) {
            existingServer.setPublicGateway(upstreamServer.getPublicGateway());
         }

         this.serverService.updateById(existingServer);
         if (BusinessStatusEnum.DELETE.getCode().equals(existingServer.getBusinessStatus())) {
            this.serverService.releaseMacAfterDelete(existingServer.getId());
         }

         this.syncBusinessServerIpFromServerRow(existingServer);
      }
   }

   private void syncBusinessServerIpFromServerRow(Server server) {
      if (server != null && !StrUtil.isBlank(server.getId())) {
         this.serverIpMapper.deleteByServerId(server.getId());
         if (BusinessStatusEnum.DELETE.getCode().equals(server.getBusinessStatus())) {
            log.debug("业务状态为已删除，已清空 BUSINESS_SERVER_IP 且不再重建，serverId={}", server.getId());
         } else {
            boolean hasPublic = StrUtil.isNotBlank(server.getPublicIp());
            boolean hasPrivate = StrUtil.isNotBlank(server.getPrivateIp());
            if (!hasPublic && !hasPrivate) {
               log.debug("上游同步未写入公/私网 IP，已清空 BUSINESS_SERVER_IP，serverId={}", server.getId());
            } else {
               if (hasPublic) {
                  ServerIpAddParam p = new ServerIpAddParam();
                  p.setServerId(server.getId());
                  p.setIp(server.getPublicIp().trim());
                  p.setType("PUBLIC");
                  p.setGateway(StrUtil.blankToDefault(server.getPublicGateway(), ""));
                  p.setMask(StrUtil.blankToDefault(server.getPublicNetmask(), ""));
                  p.setIsMain(1);
                  p.setVlanId(0);
                  this.serverIpService.add(p);
               }

               if (hasPrivate) {
                  ServerIpAddParam p = new ServerIpAddParam();
                  p.setServerId(server.getId());
                  p.setIp(server.getPrivateIp().trim());
                  p.setType("PRIVATE");
                  p.setGateway(StrUtil.blankToDefault(server.getPrivateGateway(), ""));
                  p.setMask(StrUtil.blankToDefault(server.getPrivateNetmask(), ""));
                  p.setIsMain(hasPublic ? 0 : 1);
                  p.setVlanId(0);
                  this.serverIpService.add(p);
               }
            }
         }
      }
   }

   private static ServerIdParam idParam(String id) {
      ServerIdParam p = new ServerIdParam();
      p.setId(id);
      return p;
   }

   public CommonResult startCloudServer(String serverId) {
      return this.serverService.startServer(idParam(serverId));
   }

   public CommonResult stopCloudServer(String serverId) {
      return this.serverService.closeServer(idParam(serverId));
   }

   public CommonResult restartCloudServer(String serverId) {
      return this.serverService.restartServer(idParam(serverId));
   }

   public CommonResult powerCloudServer(String serverId) {
      return this.serverService.powerServer(idParam(serverId));
   }

   public CommonResult<?> monitorCloudServer(String serverId) {
      ServerMonitorParam param = new ServerMonitorParam();
      param.setId(serverId);
      return this.serverService.monitorServer(param);
   }

   public CommonResult reinstallCloudServer(BasePoolReinstallParam param) {
      ReinstallEditParam p = new ReinstallEditParam();
      BeanUtils.copyProperties(param, p);
      return this.serverService.reinstallServer(p);
   }

   public CommonResult lockCloudServer(String serverId, String lockReason) {
      ServerLockParam param = new ServerLockParam();
      param.setId(serverId);
      param.setLockReason(lockReason);
      return this.serverService.lockServer(param);
   }

   public CommonResult unlockCloudServer(String serverId) {
      return this.serverService.unlockServer(idParam(serverId));
   }

   public CommonResult rebuildCloudServer(String serverId) {
      return this.rebuildCloudServer(serverId, null);
   }

   public CommonResult rebuildCloudServer(String serverId, Integer fullRebuild) {
      ServerIdParam param = idParam(serverId);
      param.setFullRebuild(fullRebuild);
      return this.serverService.rebuildServer(param);
   }

   public CommonResult updateCloudServerPassword(BasePoolPasswordParam param) {
      PasswordEditParam p = new PasswordEditParam();
      BeanUtils.copyProperties(param, p);
      return this.serverService.updatePasswordServer(p);
   }

   public CommonResult thumbnailCloudServer(String serverId) {
      return this.serverService.thumbnailServer(idParam(serverId));
   }

   public CommonResult syncCloudServerStatus(String serverId) {
      return this.serverService.syncStatusServer(idParam(serverId));
   }

   public CommonResult clearCloudServerStatus(String serverId) {
      return this.serverService.clearStatusServer(idParam(serverId));
   }

   public CommonResult disconnectCloudServerNetwork(String serverId) {
      return this.serverService.disconnectServer(idParam(serverId));
   }

   public CommonResult reconnectCloudServerNetwork(String serverId) {
      return this.serverService.reconnectServer(idParam(serverId));
   }

   public CommonResult resetIpCloudServer(String serverId) {
      return this.serverService.resetIpServer(idParam(serverId));
   }

   public CommonResult vncCloudServer(String serverId) {
      return this.serverService.vncUrl(idParam(serverId));
   }

   public CommonResult<?> networkHistoryCloudServer(String serverId) {
      return this.serverService.getNetworkHistory(idParam(serverId));
   }

   public CommonResult<?> cpuHistoryCloudServer(String serverId) {
      return this.serverService.getCpuHistory(idParam(serverId));
   }

   public CommonResult<?> createBackupCloudServer(String serverId, String backupName) {
      ServerBackupCreateParam p = new ServerBackupCreateParam();
      p.setServerId(serverId);
      p.setBackupName(backupName);
      return this.serverService.createBackup(p);
   }

   public CommonResult<?> restoreBackupCloudServer(String serverId, String backupName) {
      return this.serverService.restoreBackup(this.resolveBackupIdParam(serverId, backupName));
   }

   public CommonResult<?> deleteBackupCloudServer(String serverId, String backupName) {
      return this.serverService.deleteBackup(this.resolveBackupIdParam(serverId, backupName));
   }

   public CommonResult<?> createSnapshotCloudServer(String serverId, String snapshotName) {
      ServerSnapshotCreateParam p = new ServerSnapshotCreateParam();
      p.setServerId(serverId);
      p.setSnapshotName(snapshotName);
      return this.serverService.createSnapshot(p);
   }

   public CommonResult<?> restoreSnapshotCloudServer(String serverId, String snapshotName) {
      return this.serverService.restoreSnapshot(this.resolveSnapshotIdParam(serverId, snapshotName));
   }

   public CommonResult<?> deleteSnapshotCloudServer(String serverId, String snapshotName) {
      return this.serverService.deleteSnapshot(this.resolveSnapshotIdParam(serverId, snapshotName));
   }

   private ServerSnapshotIdParam resolveSnapshotIdParam(String serverId, String snapshotName) {
      if (!StrUtil.isBlank(serverId) && !StrUtil.isBlank(snapshotName)) {
         ServerSnapshotRecord record = (ServerSnapshotRecord)this.serverSnapshotRecordMapper
            .selectOne(
               (Wrapper<ServerSnapshotRecord>)((LambdaQueryWrapper<ServerSnapshotRecord>)new LambdaQueryWrapper<ServerSnapshotRecord>().eq(ServerSnapshotRecord::getServerId, serverId))
                  .eq(ServerSnapshotRecord::getSnapshotName, snapshotName)
            );
         if (record != null && !StrUtil.isBlank(record.getId())) {
            ServerSnapshotIdParam p = new ServerSnapshotIdParam();
            p.setId(record.getId());
            return p;
         } else {
            throw new RuntimeException("快照记录不存在");
         }
      } else {
         throw new RuntimeException("服务器ID或快照名称不能为空");
      }
   }

   private ServerBackupIdParam resolveBackupIdParam(String serverId, String backupName) {
      if (!StrUtil.isBlank(serverId) && !StrUtil.isBlank(backupName)) {
         ServerBackupRecord record = (ServerBackupRecord)this.serverBackupRecordMapper
            .selectOne(
               (Wrapper<ServerBackupRecord>)((LambdaQueryWrapper<ServerBackupRecord>)new LambdaQueryWrapper<ServerBackupRecord>().eq(ServerBackupRecord::getServerId, serverId))
                  .eq(ServerBackupRecord::getBackupName, backupName)
            );
         if (record != null && !StrUtil.isBlank(record.getId())) {
            ServerBackupIdParam p = new ServerBackupIdParam();
            p.setId(record.getId());
            return p;
         } else {
            throw new RuntimeException("备份记录不存在");
         }
      } else {
         throw new RuntimeException("服务器ID或备份名称不能为空");
      }
   }

   public CommonResult<?> addPortMappingCloudServer(BasePoolPortAddParam param) {
      ServerPortAddParam p = new ServerPortAddParam();
      p.setServerId(param.getServerId());
      p.setPortName(param.getPortName());
      p.setPrivatePort(param.getPrivatePort());
      p.setPublicPort(param.getPublicPort());
      ServerPort sp = this.serverPortService.add(p);
      return sp == null ? CommonResult.error("添加端口映射后未找到记录") : CommonResult.data(sp);
   }

   public CommonResult<?> deletePortMappingCloudServer(BasePoolPortDeleteParam param) {
      List<ServerPort> list = this.serverPortService.queryByServerId(param.getServerId());
      ServerPort target = list.stream()
         .filter(
            x -> Objects.equals(x.getPrivatePort(), param.getPrivatePort())
               && (param.getPublicPort() == null || Objects.equals(x.getPublicPort(), param.getPublicPort()))
         )
         .findFirst()
         .orElse(null);
      if (target == null) {
         return CommonResult.error("未找到匹配的端口映射");
      } else {
         ServerPortIdParam idp = new ServerPortIdParam();
         idp.setId(target.getId());
         this.serverPortService.delete(Collections.singletonList(idp));
         return CommonResult.ok("删除成功");
      }
   }
}
