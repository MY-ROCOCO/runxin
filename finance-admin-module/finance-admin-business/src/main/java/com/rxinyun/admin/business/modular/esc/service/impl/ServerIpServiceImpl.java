package com.rxinyun.admin.business.modular.esc.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.admin.business.modular.esc.entity.Server;
import com.rxinyun.admin.business.modular.esc.entity.ServerIp;
import com.rxinyun.admin.business.modular.esc.mapper.ServerIpMapper;
import com.rxinyun.admin.business.modular.esc.param.ServerIdParam;
import com.rxinyun.admin.business.modular.esc.param.ServerIpAddParam;
import com.rxinyun.admin.business.modular.esc.param.ServerIpEditParam;
import com.rxinyun.admin.business.modular.esc.param.ServerIpIdParam;
import com.rxinyun.admin.business.modular.esc.param.ServerIpPageParam;
import com.rxinyun.admin.business.modular.esc.param.ServerIpParam;
import com.rxinyun.admin.business.modular.esc.param.ServerIpReplaceParam;
import com.rxinyun.admin.business.modular.esc.service.ServerIpService;
import com.rxinyun.admin.business.modular.esc.service.ServerService;
import com.rxinyun.admin.business.modular.task.param.AutoTaskAddParam;
import com.rxinyun.admin.business.modular.task.service.AutoTaskTaskService;
import com.rxinyun.business.api.ServerIpApi;
import com.rxinyun.business.core.enums.TaskTypeEnum;
import com.rxinyun.common.enums.CommonEnum;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import com.rxinyun.common.enums.business.NetworkStatusEnum;
import com.rxinyun.common.enums.business.ServerStatusEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.common.pojo.CommonEntity;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.common.qzsystem.entity.HostParam;
import com.rxinyun.common.util.CommonTimeFormatUtil;
import com.rxinyun.common.util.SnowflakeIdGenerator;
import com.rxinyun.common.util.TimeUtil;
import com.rxinyun.production.api.MemberMasterApi;
import com.rxinyun.production.api.MemberNatNetworkApi;
import com.rxinyun.production.api.MemberPrivateNetworkApi;
import com.rxinyun.production.api.MemberPublicNetworkApi;
import com.rxinyun.production.core.pojo.BaseMemberMaster;
import com.rxinyun.production.core.pojo.BaseMemberPublicNetwork;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServerIpServiceImpl extends ServiceImpl<ServerIpMapper, ServerIp> implements ServerIpService, ServerIpApi {
   private static final Logger log = LoggerFactory.getLogger(ServerIpServiceImpl.class);
   @Resource
   private MemberPublicNetworkApi memberPublicNetworkApi;
   @Resource
   private MemberPrivateNetworkApi memberPrivateNetworkApi;
   @Resource
   private MemberNatNetworkApi memberNatNetworkApi;
   @Resource
   private ServerService serverService;
   @Resource
   private MemberMasterApi memberMasterApi;
   @Resource
   private ServerIpMapper serverIpMapper;
   @Resource
   private AutoTaskTaskService autoTaskTaskService;

   public String getHostNameByIp(String ip, String type) {
      if (!StrUtil.isBlank(ip) && !StrUtil.isBlank(type)) {
         QueryWrapper<ServerIp> queryWrapper = new QueryWrapper();
         ((LambdaQueryWrapper<ServerIp>)queryWrapper.lambda().eq(ServerIp::getIp, ip)).eq(ServerIp::getType, type);
         ServerIp serverIp = (ServerIp)this.getOne(queryWrapper, false);
         if (serverIp != null && !StrUtil.isBlank(serverIp.getServerId())) {
            Server server = (Server)this.serverService.getById(serverIp.getServerId());
            return server != null ? server.getHostName() : null;
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   @Override
   public Page<ServerIp> page(ServerIpPageParam pageParam) {
      QueryWrapper<ServerIp> queryWrapper = new QueryWrapper().checkSqlInjection();
      if (ObjectUtil.isNotEmpty(pageParam.getServerId())) {
         queryWrapper.lambda().and(q -> q.eq(ServerIp::getServerId, pageParam.getServerId()));
      }

      if (ObjectUtil.isNotEmpty(pageParam.getType())) {
         queryWrapper.lambda().and(q -> q.eq(ServerIp::getType, pageParam.getType()));
      }

      if (ObjectUtil.isAllNotEmpty(new Object[]{pageParam.getSortField(), pageParam.getSortOrder()})) {
         CommonSortOrderEnum.validate(pageParam.getSortOrder());
         queryWrapper.orderBy(true, pageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()), StrUtil.toUnderlineCase(pageParam.getSortField()));
      } else {
         queryWrapper.lambda().orderByDesc(CommonEntity::getCreateTime);
      }

      return (Page<ServerIp>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void add(ServerIpAddParam serverIpAddParam) {
      this.assertServerNotLocked(serverIpAddParam.getServerId());
      ServerIp publicNetwork = (ServerIp)BeanUtil.toBean(serverIpAddParam, ServerIp.class);
      this.save(publicNetwork);
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void edit(ServerIpEditParam serverIpEditParam) {
      ServerIp serverIp = this.queryEntity(serverIpEditParam.getId());
      this.assertServerNotLocked(serverIp.getServerId());
      BeanUtil.copyProperties(serverIpEditParam, serverIp, new String[0]);
      this.updateById(serverIp);
   }

   @Override
   public ServerIp detail(ServerIpIdParam serverIpIdParam) {
      return this.queryEntity(serverIpIdParam.getId());
   }

   @Override
   public ServerIp queryEntity(String id) {
      ServerIp serverIp = (ServerIp)this.getById(id);
      if (ObjectUtil.isEmpty(serverIp)) {
         throw new CommonException("服务器IP不存在，id值为：{}", new Object[]{id});
      } else {
         return serverIp;
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void deleteByServerId(String serverId) {
      log.info("删除ip数据，serverId:{}", serverId);
      List<ServerIp> memberServerIpList = this.queryByServerId(serverId);
      this.releaseAndDeleteServerIpRows(serverId, memberServerIpList);
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void deleteByServerIdWithServerSnapshotFallback(String serverId) {
      log.info("删除云服务器收尾释放IP，serverId:{}", serverId);
      Server server = StrUtil.isNotBlank(serverId) ? (Server)this.serverService.getById(serverId) : null;
      List<ServerIp> memberServerIpList = this.queryByServerId(serverId);
      if (CollectionUtils.isEmpty(memberServerIpList)) {
         log.warn(
            "BUSINESS_SERVER_IP 已无记录，尝试使用云服务器主表IP快照兜底释放，serverId={}, publicIp={}, privateIp={}",
            new Object[]{serverId, server != null ? server.getPublicIp() : null, server != null ? server.getPrivateIp() : null}
         );
         this.releaseFromServerSnapshot(server);
         this.serverIpMapper.deleteByServerId(serverId);
      } else {
         this.releaseAndDeleteServerIpRows(serverId, memberServerIpList);
      }
   }

   private void releaseAndDeleteServerIpRows(String serverId, List<ServerIp> memberServerIpList) {
      if (CollectionUtils.isEmpty(memberServerIpList)) {
         log.debug("BUSINESS_SERVER_IP 已无记录，deleteByServerId 幂等跳过 serverId={}", serverId);
      } else {
         for (ServerIp memberServerIp : memberServerIpList) {
            this.releaseServerIp(serverId, memberServerIp);
         }

         this.serverIpMapper.deleteByServerId(serverId);
      }
   }

   private void releaseServerIp(String serverId, ServerIp memberServerIp) {
      if (memberServerIp != null && !StrUtil.isBlank(memberServerIp.getIp())) {
         String type = memberServerIp.getType();
         if ("PUBLIC".equals(type)) {
            this.memberPublicNetworkApi.updateStatus(memberServerIp.getIp());
         } else if ("PRIVATE".equals(type)) {
            this.memberPrivateNetworkApi.updateStatus(memberServerIp.getIp());
         } else {
            if (!"NAT".equals(type)) {
               throw new CommonException("服务器IP类型错误，serverId值为：{}", new Object[]{serverId});
            }

            this.memberNatNetworkApi.updateStatus(memberServerIp.getIp());
         }

         log.info("已释放云服务器IP，serverId={}, type={}, ip={}", new Object[]{serverId, type, memberServerIp.getIp()});
      }
   }

   private void releaseFromServerSnapshot(Server server) {
      if (server != null) {
         boolean sharedIp = Integer.valueOf(1).equals(server.getShareIp());
         if (!sharedIp && StrUtil.isNotBlank(server.getPublicIp())) {
            this.memberPublicNetworkApi.updateStatus(server.getPublicIp());
            log.info("已通过云服务器主表快照兜底释放公网IP，serverId={}, ip={}", server.getId(), server.getPublicIp());
         } else if (sharedIp) {
            log.warn("共享IP云服务器缺少 BUSINESS_SERVER_IP 明细，跳过公网/NAT兜底释放，serverId={}, publicIp={}", server.getId(), server.getPublicIp());
         }

         if (StrUtil.isNotBlank(server.getPrivateIp())) {
            this.memberPrivateNetworkApi.updateStatus(server.getPrivateIp());
            log.info("已通过云服务器主表快照兜底释放私网IP，serverId={}, ip={}", server.getId(), server.getPrivateIp());
         }
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void delete(List<ServerIpIdParam> serverIpIdParamList) {
      log.info("批量删除服务器IP，参数：{}", JSON.toJSONString(serverIpIdParamList));
      List<String> serverIpIdList = CollStreamUtil.toList(serverIpIdParamList, ServerIpIdParam::getId);
      LambdaQueryWrapper<ServerIp> lambdaQueryWrapper = new LambdaQueryWrapper();
      lambdaQueryWrapper.in(ServerIp::getId, serverIpIdList);
      List<ServerIp> serverIpList = this.list(lambdaQueryWrapper);
      Set<String> serverIdSet = serverIpList.stream().map(ServerIp::getServerId).collect(Collectors.toSet());

      for (String serverId : serverIdSet) {
         this.assertServerNotLocked(serverId);
      }

      if (serverIpList.stream().anyMatch(serverIp -> serverIp.getIsMain().equals(CommonEnum.YES.getCode()))) {
         throw new CommonException("存在主IP，不允许删除", new Object[0]);
      } else {
         if (ObjectUtil.isNotEmpty(serverIpIdList)) {
            this.serverIpMapper.removeByIds(serverIpIdList);
         }

         List<String> publicIpList = serverIpList.stream().map(ServerIp::getIp).collect(Collectors.toList());
         log.info("批量释放公网IP，参数：{}", JSON.toJSONString(publicIpList));
         this.memberPublicNetworkApi.updateStatus(publicIpList);

         for (String serverId : serverIdSet) {
            log.info("请求受控批量更新IP，参数：{}", serverId);
            this.batsetIpHost(serverId);
         }
      }
   }

   @Override
   public List<ServerIp> queryByServerId(String serverId) {
      LambdaQueryWrapper<ServerIp> queryWrapper = (LambdaQueryWrapper<ServerIp>)new LambdaQueryWrapper<ServerIp>().eq(ServerIp::getServerId, serverId);
      return this.list(queryWrapper);
   }

   @Override
   public List<String> listServerIdByIp(String ip) {
      if (StrUtil.isBlank(ip)) {
         return new ArrayList<>();
      } else {
         LambdaQueryWrapper<ServerIp> q = ((LambdaQueryWrapper<ServerIp>)new LambdaQueryWrapper<ServerIp>().like(ServerIp::getIp, ip))
            .select(ServerIp::getServerId);
         return this.list(q).stream().map(ServerIp::getServerId).distinct().collect(Collectors.toList());
      }
   }

   @Override
   public Map<String, Integer> countPublicIpByServerIds(List<String> serverIds) {
      if (CollectionUtils.isEmpty(serverIds)) {
         return new HashMap<>();
      } else {
         LambdaQueryWrapper<ServerIp> q = ((LambdaQueryWrapper<ServerIp>)((LambdaQueryWrapper<ServerIp>)new LambdaQueryWrapper<ServerIp>().in(ServerIp::getServerId, serverIds))
               .in(ServerIp::getType, new Object[]{"PUBLIC", "NAT"}))
            .select(ServerIp::getServerId);
         return this.list(q).stream().collect(Collectors.groupingBy(ServerIp::getServerId, Collectors.summingInt(serverIp -> 1)));
      }
   }

   @Override
   public CommonResult addBatch(ServerIpParam serverIdParam) {
      log.info("批量添加服务器IP，参数：{}", JSON.toJSONString(serverIdParam));
      this.assertServerNotLocked(serverIdParam.getId());

      for (String id : serverIdParam.getPublicNetworkIdList()) {
         this.setNetworkInformation(serverIdParam.getId(), id);
      }

      return this.batsetIpHost(serverIdParam.getId());
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public CommonResult replacePublicIp(ServerIpReplaceParam serverIpReplaceParam) {
      log.info("更换云服务器IP，参数：{}", JSON.toJSONString(serverIpReplaceParam));
      if (serverIpReplaceParam == null || StrUtil.isBlank(serverIpReplaceParam.getServerId())) {
         throw new CommonException("服务器ID不能为空", new Object[0]);
      } else if (StrUtil.isBlank(serverIpReplaceParam.getPublicNetworkId())) {
         throw new CommonException("新的公网IP池ID不能为空", new Object[0]);
      } else {
         Server server = this.serverService.queryEntity(serverIpReplaceParam.getServerId());
         if (server == null) {
            throw new CommonException("服务器不存在，serverId值为：{}", new Object[]{serverIpReplaceParam.getServerId()});
         } else {
            this.assertServerNotLocked(server);
            if (Integer.valueOf(1).equals(server.getType())) {
               throw new CommonException("资源池云服务器不支持本地更换IP", new Object[0]);
            } else {
               ServerIp currentIp = this.queryCurrentPublicIp(serverIpReplaceParam, server);
               BaseMemberPublicNetwork newPublicNetwork = this.memberPublicNetworkApi.selectById(serverIpReplaceParam.getPublicNetworkId());
               if (newPublicNetwork == null) {
                  throw new CommonException("新的公网IP不存在", new Object[0]);
               } else if (NetworkStatusEnum.UNOCCUPIED.getCode() != newPublicNetwork.getEnable()) {
                  throw new CommonException("新的公网IP已被占用", new Object[0]);
               } else if (StrUtil.equals(currentIp.getIp(), newPublicNetwork.getIp())) {
                  throw new CommonException("新公网IP不能与当前公网IP相同", new Object[0]);
               } else {
                  this.ensureIpNotBound(newPublicNetwork.getIp(), currentIp.getId());
                  String oldIp = currentIp.getIp();
                  boolean replaceMainIp = Integer.valueOf(CommonEnum.YES.getCode()).equals(currentIp.getIsMain());
                  this.memberPublicNetworkApi
                     .updateStatusWithOptimisticLock(newPublicNetwork.getId(), NetworkStatusEnum.OCCUPIED.getCode(), newPublicNetwork.getVersion());
                  currentIp.setIp(newPublicNetwork.getIp());
                  currentIp.setGateway(newPublicNetwork.getGateway());
                  currentIp.setMask(newPublicNetwork.getMask());
                  currentIp.setVlanId(newPublicNetwork.getVlanId());
                  currentIp.setCname(newPublicNetwork.getCname());
                  currentIp.setType("PUBLIC");
                  currentIp.setIsMain(replaceMainIp ? CommonEnum.YES.getCode() : 0);
                  currentIp.setUpdateTime(new Date());
                  this.updateById(currentIp);
                  if (replaceMainIp) {
                     Server serverToUpdate = new Server();
                     serverToUpdate.setId(server.getId());
                     serverToUpdate.setPublicIp(newPublicNetwork.getIp());
                     serverToUpdate.setPublicGateway(newPublicNetwork.getGateway());
                     serverToUpdate.setPublicNetmask(newPublicNetwork.getMask());
                     serverToUpdate.setUpdateTime(new Date());
                     this.serverService.updateById(serverToUpdate);
                  }

                  this.memberPublicNetworkApi.updateStatus(oldIp);
                  CommonResult resetResult = this.syncIpAfterIpChange(server);
                  if (resetResult != null && resetResult.getCode() != null && Integer.valueOf(200).equals(resetResult.getCode())) {
                     return resetResult;
                  } else {
                     throw new CommonException(resetResult != null ? resetResult.getMsg() : "重置云服务器IP失败", new Object[0]);
                  }
               }
            }
         }
      }
   }

   @Override
   public CommonResult resetCurrentIp(String serverId) {
      this.assertServerNotLocked(serverId);
      List<ServerIp> serverIps = this.queryByServerId(serverId);
      boolean hasPublicOrNat = serverIps.stream().anyMatch(serverIp -> "PUBLIC".equals(serverIp.getType()) || "NAT".equals(serverIp.getType()));
      return !hasPublicOrNat ? CommonResult.error("当前云服务器不存在可重置的公网/NAT IP数据") : this.batsetIpHost(serverId);
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void setMainIp(ServerIpIdParam serverIpIdParam) {
      if (serverIpIdParam != null && !StrUtil.isBlank(serverIpIdParam.getId())) {
         ServerIp targetIp = this.queryEntity(serverIpIdParam.getId());
         String serverId = StrUtil.blankToDefault(serverIpIdParam.getServerId(), targetIp.getServerId());
         if (!StrUtil.isBlank(serverId) && serverId.equals(targetIp.getServerId())) {
            Server server = this.serverService.queryEntity(serverId);
            if (server == null) {
               throw new CommonException("服务器不存在，serverId值为：{}", new Object[]{serverId});
            } else {
               this.assertServerNotLocked(server);
               boolean privateFamily = "PRIVATE".equals(targetIp.getType());
               LambdaUpdateWrapper<ServerIp> clearOldMainWrapper = (LambdaUpdateWrapper<ServerIp>)((LambdaUpdateWrapper<ServerIp>)((LambdaUpdateWrapper<ServerIp>)new LambdaUpdateWrapper<ServerIp>()
                        .eq(ServerIp::getServerId, serverId))
                     .set(ServerIp::getIsMain, 0))
                  .set(CommonEntity::getUpdateTime, new Date());
               if (privateFamily) {
                  clearOldMainWrapper.eq(ServerIp::getType, "PRIVATE");
               } else {
                  clearOldMainWrapper.and(
                     w -> ((LambdaUpdateWrapper<ServerIp>)((LambdaUpdateWrapper<ServerIp>)w.eq(ServerIp::getType, "PUBLIC")).or()).eq(ServerIp::getType, "NAT")
                  );
               }

               this.update(clearOldMainWrapper);
               targetIp.setIsMain(CommonEnum.YES.getCode());
               targetIp.setUpdateTime(new Date());
               this.updateById(targetIp);
               Server serverToUpdate = new Server();
               serverToUpdate.setId(serverId);
               serverToUpdate.setUpdateTime(new Date());
               if (privateFamily) {
                  serverToUpdate.setPrivateIp(targetIp.getIp());
                  serverToUpdate.setPrivateGateway(targetIp.getGateway());
                  serverToUpdate.setPrivateNetmask(targetIp.getMask());
                  serverToUpdate.setPrivateVlanId(targetIp.getVlanId());
               } else {
                  serverToUpdate.setPublicIp(targetIp.getIp());
                  serverToUpdate.setPublicGateway(targetIp.getGateway());
                  serverToUpdate.setPublicNetmask(targetIp.getMask());
               }

               this.serverService.updateById(serverToUpdate);
               CommonResult resetResult = this.syncIpAfterIpChange(server);
               if (resetResult == null || resetResult.getCode() == null || !Integer.valueOf(200).equals(resetResult.getCode())) {
                  throw new CommonException(resetResult != null ? resetResult.getMsg() : "重置云服务器IP失败", new Object[0]);
               }
            }
         } else {
            throw new CommonException("服务器ID与服务器IP不匹配", new Object[0]);
         }
      } else {
         throw new CommonException("服务器IP标识不能为空", new Object[0]);
      }
   }

   private CommonResult syncIpAfterIpChange(Server server) {
      if (server == null) {
         return CommonResult.error("服务器不存在");
      } else if (Integer.valueOf(1).equals(server.getType())) {
         return this.serverService.resetIpServer(buildServerIdParam(server.getId()));
      } else {
         BaseMemberMaster memberMaster = this.memberMasterApi.selectByMasterId(server.getMasterId());
         return memberMaster != null && "3".equals(StrUtil.trim(memberMaster.getGuardVersion()))
            ? this.resetCurrentIp(server.getId())
            : this.serverService.resetIpServer(buildServerIdParam(server.getId()));
      }
   }

   private void setNetworkInformation(String serverId, String publicNetworkId) {
      BaseMemberPublicNetwork publicNetwork = this.memberPublicNetworkApi.selectById(publicNetworkId);
      log.info("创建云服务器，公网网络信息:{}", JSON.toJSONString(publicNetwork));
      if (ObjectUtil.isNull(publicNetwork)) {
         throw new CommonException("创建云服务器失败，公网网络信息不存在", new Object[0]);
      } else {
         ServerIpAddParam serverIpAddParam = new ServerIpAddParam();
         serverIpAddParam.setServerId(serverId);
         serverIpAddParam.setIp(publicNetwork.getIp());
         serverIpAddParam.setGateway(publicNetwork.getGateway());
         serverIpAddParam.setMask(publicNetwork.getMask());
         serverIpAddParam.setType("PUBLIC");
         this.add(serverIpAddParam);
         this.memberPublicNetworkApi.updateStatus(publicNetwork.getId(), NetworkStatusEnum.OCCUPIED.getCode());
      }
   }

   private ServerIp queryCurrentPublicIp(ServerIpReplaceParam serverIpReplaceParam, Server server) {
      ServerIp currentIp;
      if (StrUtil.isNotBlank(serverIpReplaceParam.getCurrentServerIpId())) {
         currentIp = this.queryEntity(serverIpReplaceParam.getCurrentServerIpId());
         if (!server.getId().equals(currentIp.getServerId())) {
            throw new CommonException("当前IP不属于该云服务器", new Object[0]);
         }
      } else {
         LambdaQueryWrapper<ServerIp> mainIpWrapper = (LambdaQueryWrapper<ServerIp>)((LambdaQueryWrapper<ServerIp>)((LambdaQueryWrapper<ServerIp>)new LambdaQueryWrapper<ServerIp>()
                  .eq(ServerIp::getServerId, server.getId()))
               .eq(ServerIp::getType, "PUBLIC"))
            .eq(ServerIp::getIsMain, CommonEnum.YES.getCode());
         currentIp = (ServerIp)this.getOne(mainIpWrapper, false);
         if (currentIp == null && StrUtil.isNotBlank(server.getPublicIp())) {
            LambdaQueryWrapper<ServerIp> serverPublicIpWrapper = (LambdaQueryWrapper<ServerIp>)((LambdaQueryWrapper<ServerIp>)((LambdaQueryWrapper<ServerIp>)new LambdaQueryWrapper<ServerIp>()
                     .eq(ServerIp::getServerId, server.getId()))
                  .eq(ServerIp::getType, "PUBLIC"))
               .eq(ServerIp::getIp, server.getPublicIp());
            currentIp = (ServerIp)this.getOne(serverPublicIpWrapper, false);
         }
      }

      if (currentIp == null) {
         throw new CommonException("当前云服务器不存在可更换公网IP", new Object[0]);
      } else if (!"PUBLIC".equals(currentIp.getType())) {
         throw new CommonException("仅支持更换公网IP", new Object[0]);
      } else {
         return currentIp;
      }
   }

   private void ensureIpNotBound(String ip, String currentServerIpId) {
      LambdaQueryWrapper<ServerIp> duplicateWrapper = (LambdaQueryWrapper<ServerIp>)((LambdaQueryWrapper<ServerIp>)new LambdaQueryWrapper<ServerIp>().eq(ServerIp::getIp, ip))
         .ne(ServerIp::getId, currentServerIpId);
      if (this.count(duplicateWrapper) > 0L) {
         throw new CommonException("新的公网IP已绑定云服务器", new Object[0]);
      }
   }

   private void assertServerNotLocked(String serverId) {
      if (!StrUtil.isBlank(serverId)) {
         Server server = this.serverService.queryEntity(serverId);
         this.assertServerNotLocked(server);
      }
   }

   private void assertServerNotLocked(Server server) {
      if (server != null && ServerStatusEnum.LOCKED.getCode().equals(server.getServerStatus())) {
         throw new CommonException("云服务器已锁定，请先解锁后再操作", new Object[0]);
      }
   }

   public CommonResult batsetIpHost(String serverId) {
      log.info("批量添加服务器IP，参数：{}", serverId);

      try {
         Server server = this.serverService.queryEntity(serverId);
         if (null == server) {
            return CommonResult.error("服务器不存在");
         } else {
            BaseMemberMaster memberMaster = this.memberMasterApi.selectByMasterId(server.getMasterId());
            log.info("宿主机memberMaster，参数：{}", JSON.toJSONString(memberMaster));
            if (null == memberMaster) {
               return CommonResult.error("宿主机不存在");
            } else {
               List<ServerIp> serverIps = this.queryByServerId(server.getId());
               String ipStr = serverIps.stream().map(ServerIp::getIp).collect(Collectors.joining(","));
               HostParam hostParam = new HostParam();
               hostParam.setHostName(server.getHostName());
               hostParam.setServerId(server.getId());
               hostParam.setMasterIp(memberMaster.getIp());
               hostParam.setApiKey(memberMaster.getApiPassword());
               hostParam.setApiPort(memberMaster.getApiPort());
               hostParam.setIpStr(ipStr);
               log.info("批量添加IP请求参数: {}", JSON.toJSONString(hostParam));
               AutoTaskAddParam taskParam = new AutoTaskAddParam();
               taskParam.setMasterId(memberMaster.getId());
               taskParam.setServerId(server.getId());
               taskParam.setProductType(1);
               taskParam.setTaskType(TaskTypeEnum.ADD_IP.getCode());
               taskParam.setExecuteTime(CommonTimeFormatUtil.formatNowGmt8());
               taskParam.setTaskStatus(1);
               taskParam.setNextRetryTime(TimeUtil.toDate(TimeUtil.now()));
               taskParam.setTaskDesc("批量设置云服务器IP任务");
               taskParam.setPlatform("B");
               taskParam.setParams(JSON.toJSONString(hostParam));
               taskParam.setRetryCount(0);
               taskParam.setMaxRetry(3);
               taskParam.setTimeoutTime(DateUtil.offsetHour(TimeUtil.toDate(TimeUtil.now()), 2));
               this.autoTaskTaskService.add(taskParam);
               server.setServerStatus(ServerStatusEnum.UPGRADE.getCode());
               this.serverService.updateById(server);
               return CommonResult.ok("批量设置IP任务已创建");
            }
         }
      } catch (Exception var9) {
         log.error("批量添加IP", var9);
         return CommonResult.error(var9.getMessage());
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void replaceFromResourcePoolUpstreamSnapshot(String serverId, JSONObject dataSnapshot) {
      if (!StrUtil.isBlank(serverId) && dataSnapshot != null) {
         this.serverIpMapper.deleteByServerId(serverId);
         JSONArray ipList = dataSnapshot.getJSONArray("ipList");
         boolean hasPublicInList = false;
         if (ipList != null && !ipList.isEmpty()) {
            for (int i = 0; i < ipList.size(); i++) {
               JSONObject ipObj = ipList.getJSONObject(i);
               String ipType = ipObj.getString("ipType");
               if (StrUtil.isBlank(ipType)) {
                  ipType = ipObj.getString("type");
               }

               String ipTypeUpper = ipType != null ? ipType.trim().toUpperCase() : "";
               if ("PUBLIC".equals(ipTypeUpper) || "PUBLIC_IP".equals(ipTypeUpper) || "WAN".equals(ipTypeUpper)) {
                  hasPublicInList = true;
                  break;
               }
            }
         }

         boolean firstPublic = true;
         boolean firstPrivate = true;
         if (ipList != null && !ipList.isEmpty()) {
            for (int i = 0; i < ipList.size(); i++) {
               JSONObject ipObjx = ipList.getJSONObject(i);
               String ip = ipObjx.getString("ip");
               if (StrUtil.isBlank(ip)) {
                  ip = ipObjx.getString("ipAddress");
               }

               if (!StrUtil.isBlank(ip)) {
                  String ipTypex = ipObjx.getString("ipType");
                  if (StrUtil.isBlank(ipTypex)) {
                     ipTypex = ipObjx.getString("type");
                  }

                  String ipTypeUpper = ipTypex != null ? ipTypex.trim().toUpperCase() : "";
                  String type;
                  if (!"PRIVATE".equals(ipTypeUpper) && !"PRIVATE_IP".equals(ipTypeUpper) && !"LAN".equals(ipTypeUpper)) {
                     type = "PUBLIC";
                  } else {
                     type = "PRIVATE";
                  }

                  Integer upstreamIsMain = ipObjx.getInteger("isMain");
                  int isMain;
                  if (upstreamIsMain != null) {
                     isMain = upstreamIsMain;
                  } else if ("PUBLIC".equals(type)) {
                     isMain = firstPublic ? 1 : 0;
                     firstPublic = false;
                  } else {
                     isMain = !hasPublicInList && firstPrivate ? 1 : 0;
                     firstPrivate = false;
                  }

                  String mask = firstNonBlank(ipObjx.getString("subnetMask"), ipObjx.getString("netmask"), ipObjx.getString("mask"));
                  String gateway = firstNonBlank(ipObjx.getString("gateway"), ipObjx.getString("publicGateway"), ipObjx.getString("privateGateway"));
                  Integer vlanId = ipObjx.getInteger("vlanId");
                  ServerIp row = new ServerIp();
                  row.setId(SnowflakeIdGenerator.generateId());
                  row.setServerId(serverId);
                  row.setIp(ip.trim());
                  row.setType(type);
                  row.setGateway(StrUtil.blankToDefault(gateway, ""));
                  row.setMask(StrUtil.blankToDefault(mask, ""));
                  row.setVlanId(vlanId == null ? 0 : vlanId);
                  row.setIsMain(isMain);
                  this.save(row);
               }
            }
         } else {
            String publicIp = firstNonBlank(dataSnapshot.getString("publicIp"), dataSnapshot.getString("public_ip"));
            String privateIp = firstNonBlank(dataSnapshot.getString("privateIp"), dataSnapshot.getString("private_ip"));
            if (StrUtil.isBlank(publicIp) && StrUtil.isBlank(privateIp)) {
               log.debug("资源池云服务器快照无 ipList 且无 public/private ip，已清空 BUSINESS_SERVER_IP，serverId={}", serverId);
            } else {
               if (StrUtil.isNotBlank(publicIp)) {
                  ServerIp row = new ServerIp();
                  row.setId(SnowflakeIdGenerator.generateId());
                  row.setServerId(serverId);
                  row.setIp(publicIp.trim());
                  row.setType("PUBLIC");
                  row.setGateway(StrUtil.blankToDefault(dataSnapshot.getString("publicGateway"), ""));
                  row.setMask(StrUtil.blankToDefault(dataSnapshot.getString("publicNetmask"), ""));
                  row.setIsMain(1);
                  row.setVlanId(0);
                  this.save(row);
               }

               if (StrUtil.isNotBlank(privateIp)) {
                  ServerIp row = new ServerIp();
                  row.setId(SnowflakeIdGenerator.generateId());
                  row.setServerId(serverId);
                  row.setIp(privateIp.trim());
                  row.setType("PRIVATE");
                  row.setGateway(StrUtil.blankToDefault(dataSnapshot.getString("privateGateway"), ""));
                  row.setMask(StrUtil.blankToDefault(dataSnapshot.getString("privateNetmask"), ""));
                  row.setIsMain(StrUtil.isBlank(publicIp) ? 1 : 0);
                  row.setVlanId(0);
                  this.save(row);
               }
            }
         }
      }
   }

   private static String firstNonBlank(String... vals) {
      if (vals == null) {
         return null;
      } else {
         for (String v : vals) {
            if (StrUtil.isNotBlank(v)) {
               return v;
            }
         }

         return null;
      }
   }

   private static ServerIdParam buildServerIdParam(String serverId) {
      ServerIdParam serverIdParam = new ServerIdParam();
      serverIdParam.setId(serverId);
      return serverIdParam;
   }
}
