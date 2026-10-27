package com.rxinyun.member.business.modular.esc.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.common.controller.CloudControllerRouter;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.common.pojo.CommonEntity;
import com.rxinyun.common.qzsystem.entity.HostPort;
import com.rxinyun.member.business.modular.esc.entity.MemberServer;
import com.rxinyun.member.business.modular.esc.entity.MemberServerIp;
import com.rxinyun.member.business.modular.esc.entity.MemberServerPort;
import com.rxinyun.member.business.modular.esc.mapper.MemberServerPortMapper;
import com.rxinyun.member.business.modular.esc.param.MemberServerPortAddParam;
import com.rxinyun.member.business.modular.esc.param.MemberServerPortIdParam;
import com.rxinyun.member.business.modular.esc.param.MemberServerPortPageParam;
import com.rxinyun.member.business.modular.esc.service.MemberServerIpService;
import com.rxinyun.member.business.modular.esc.service.MemberServerPortService;
import com.rxinyun.member.business.modular.esc.service.MemberServerService;
import com.rxinyun.production.api.MemberMasterApi;
import com.rxinyun.production.core.pojo.BaseMemberMaster;
import jakarta.annotation.Resource;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberServerPortServiceImpl extends ServiceImpl<MemberServerPortMapper, MemberServerPort> implements MemberServerPortService {
   private static final Logger log = LoggerFactory.getLogger(MemberServerPortServiceImpl.class);
   @Resource
   private MemberMasterApi memberMasterApi;
   @Resource
   private CloudControllerRouter cloudControllerRouter;
   @Resource
   private MemberServerService memberServerService;
   @Resource
   private MemberServerIpService memberServerIpService;
   @Resource
   private MemberServerPortMapper memberServerPortMapper;

   @Override
   public Page<MemberServerPort> page(MemberServerPortPageParam pageParam) {
      QueryWrapper<MemberServerPort> queryWrapper = new QueryWrapper().checkSqlInjection();
      MemberServer memberServer = this.memberServerService.queryEntity(pageParam.getServerId());
      if (null == memberServer) {
         throw new CommonException("服务器不存在，serverId值为：{}", new Object[]{pageParam.getServerId()});
      } else {
         if (ObjectUtil.isNotEmpty(pageParam.getServerId())) {
            queryWrapper.lambda().and(q -> q.eq(MemberServerPort::getServerId, pageParam.getServerId()));
         }

         if (ObjectUtil.isAllNotEmpty(new Object[]{pageParam.getSortField(), pageParam.getSortOrder()})) {
            CommonSortOrderEnum.validate(pageParam.getSortOrder());
            queryWrapper.orderBy(true, pageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()), StrUtil.toUnderlineCase(pageParam.getSortField()));
         } else {
            queryWrapper.lambda().orderByDesc(CommonEntity::getCreateTime);
         }

         return (Page<MemberServerPort>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public MemberServerPort add(MemberServerPortAddParam memberServerPortAddParam) {
      MemberServer memberServer = this.memberServerService.queryEntity(memberServerPortAddParam.getServerId());
      if (null == memberServer) {
         throw new CommonException("服务器不存在，serverId值为：{}", new Object[]{memberServerPortAddParam.getServerId()});
      } else if (memberServer.getShareIp() != 1) {
         return null;
      } else {
         BaseMemberMaster memberMaster = this.memberMasterApi.selectByLineId(memberServer.getLineId());
         MemberServerPort memberServerPort = (MemberServerPort)BeanUtil.toBean(memberServerPortAddParam, MemberServerPort.class);
         log.info("新增ip数据:{}", JSON.toJSONString(memberServerPort));
         MemberServerPortServiceImpl.PortPair portPair = this.resolvePortPair(
            memberServer.getMasterId(), memberServerPortAddParam.getPrivatePort(), memberServerPortAddParam.getPublicPort()
         );
         memberServerPort.setPublicPort(portPair.getPublicPort());
         memberServerPort.setPrivatePort(portPair.getPrivatePort());
         memberServerPort.setServerName(memberServer.getHostName());
         memberServerPort.setServerId(memberServer.getId());
         memberServerPort.setPublicIp(this.resolvePortForwardTargetIp(memberServer));
         memberServerPort.setPrivateIp(memberServer.getPrivateIp());
         memberServerPort.setMasterId(memberServer.getMasterId());
         memberServerPort.setMasterIp(memberMaster.getIp());
         this.save(memberServerPort);
         HostPort hostPort = new HostPort();
         hostPort.setApiPort(memberMaster.getApiPort());
         hostPort.setApiKey(memberMaster.getApiPassword());
         hostPort.setMasterIp(memberMaster.getIp());
         hostPort.setPublicPort(memberServerPort.getPublicPort());
         hostPort.setPublicIp(memberServerPort.getPublicIp());
         hostPort.setPrivatePort(memberServerPort.getPrivatePort());
         hostPort.setHostName(memberServer.getHostName());
         log.info("添加端口映射:{}", JSON.toJSONString(hostPort));
         this.cloudControllerRouter.getAdapterByGuardVersion(memberMaster.getGuardVersion()).addForwardPort(hostPort);
         this.memberServerService
            .update(
               (Wrapper<MemberServer>)((LambdaUpdateWrapper<MemberServer>)new LambdaUpdateWrapper<MemberServer>().eq(MemberServer::getId, memberServer.getId()))
                  .set(MemberServer::getPort, memberServerPort.getPublicPort())
            );
         return memberServerPort;
      }
   }

   @Override
   public MemberServerPort detail(MemberServerPortIdParam serverIpIdParam) {
      MemberServerPort memberServerPort = this.queryEntity(serverIpIdParam.getId());
      this.memberServerService.queryEntity(memberServerPort.getServerId());
      return memberServerPort;
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void delete(List<MemberServerPortIdParam> memberServerPortIdParamList) {
      List<String> memberServerPortIdList = CollStreamUtil.toList(memberServerPortIdParamList, MemberServerPortIdParam::getId);
      if (ObjectUtil.isNotEmpty(memberServerPortIdList)) {
         for (String id : memberServerPortIdList) {
            MemberServerPort memberServerPort = this.queryEntity(id);
            if (null == memberServerPort) {
               throw new CommonException("服务器端口不存在，id值为：{}", new Object[]{id});
            }

            MemberServer memberServer = this.memberServerService.queryEntity(memberServerPort.getServerId());
            if (null == memberServer) {
               throw new CommonException("服务器不存在，serverId值为：{}", new Object[]{memberServerPort.getServerId()});
            }

            this.removeById(id);
            BaseMemberMaster memberMaster = this.memberMasterApi.selectByLineId(memberServer.getLineId());
            HostPort hostPort = new HostPort();
            hostPort.setApiPort(memberMaster.getApiPort());
            hostPort.setApiKey(memberMaster.getApiPassword());
            hostPort.setMasterIp(memberMaster.getIp());
            hostPort.setPublicPort(memberServerPort.getPublicPort());
            hostPort.setPublicIp(memberServerPort.getPublicIp());
            hostPort.setPrivatePort(memberServerPort.getPrivatePort());
            hostPort.setHostName(memberServer.getHostName());
            log.info("删除端口映射:{}", JSON.toJSONString(hostPort));
            this.cloudControllerRouter.getAdapterByGuardVersion(memberMaster.getGuardVersion()).removeForwardPort(hostPort);
         }
      }

      this.memberServerPortMapper.deleteByIds(memberServerPortIdList);
   }

   @Override
   public void delete(String serverId) {
      MemberServer memberServer = (MemberServer)this.memberServerService.getOne((Wrapper<MemberServer>)new LambdaQueryWrapper<MemberServer>().eq(MemberServer::getId, serverId));
      if (null == memberServer) {
         throw new CommonException("服务器不存在，serverId值为：{}", new Object[]{serverId});
      } else if (memberServer.getShareIp() == 1) {
         BaseMemberMaster memberMaster = this.memberMasterApi.selectByLineId(memberServer.getLineId());
         List<MemberServerPort> memberServerPortList = this.list((Wrapper<MemberServerPort>)new LambdaQueryWrapper<MemberServerPort>().eq(MemberServerPort::getServerId, serverId));
         log.info("删除端口映射:{}", JSON.toJSONString(memberServerPortList));

         for (MemberServerPort memberServerPort : memberServerPortList) {
            HostPort hostPort = new HostPort();
            hostPort.setApiPort(memberMaster.getApiPort());
            hostPort.setApiKey(memberMaster.getApiPassword());
            hostPort.setMasterIp(memberMaster.getIp());
            hostPort.setPublicPort(memberServerPort.getPublicPort());
            hostPort.setPublicIp(memberServerPort.getPublicIp());
            hostPort.setPrivatePort(memberServerPort.getPrivatePort());
            log.info("删除端口映射:{}", JSON.toJSONString(hostPort));
            this.cloudControllerRouter.getAdapterByGuardVersion(memberMaster.getGuardVersion()).removeForwardPort(hostPort);
         }

         this.memberServerPortMapper.delete(serverId);
      }
   }

   @Override
   public MemberServerPort queryEntity(String id) {
      MemberServerPort serverIp = (MemberServerPort)this.getById(id);
      if (ObjectUtil.isEmpty(serverIp)) {
         throw new CommonException("服务器端口不存在，id值为：{}", new Object[]{id});
      } else {
         return serverIp;
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   public int allocatePort(String masterId, int port) {
      if (port > 0) {
         if (this.memberServerPortMapper.checkPortExist(masterId, port) > 0) {
            throw new CommonException("Port " + port + " is already in use", new Object[0]);
         } else {
            return port;
         }
      } else {
         return this.findAvailablePort(masterId);
      }
   }

   private int findAvailablePort(String masterId) {
      int start = 10000;

      for (int port = start; port <= 65535; port++) {
         if (this.memberServerPortMapper.checkPortExist(masterId, port) == 0) {
            return port;
         }
      }

      throw new CommonException("No available ports", new Object[0]);
   }

   private MemberServerPortServiceImpl.PortPair resolvePortPair(String masterId, Integer requestedPrivatePort, Integer requestedPublicPort) {
      int allocatedPublicPort = this.allocatePort(masterId, requestedPublicPort != null ? requestedPublicPort : 0);
      return requestedPrivatePort == null
         ? new MemberServerPortServiceImpl.PortPair(allocatedPublicPort, allocatedPublicPort)
         : new MemberServerPortServiceImpl.PortPair(allocatedPublicPort, requestedPrivatePort);
   }

   private String resolvePortForwardTargetIp(MemberServer server) {
      if (server != null && Integer.valueOf(1).equals(server.getShareIp())) {
         for (MemberServerIp serverIp : this.memberServerIpService.queryByServerId(server.getId())) {
            if (Integer.valueOf(1).equals(serverIp.getIsMain())) {
               String type = serverIp.getType();
               if (("NAT".equals(type) || "PUBLIC".equals(type)) && StrUtil.isNotBlank(serverIp.getIp())) {
                  return serverIp.getIp();
               }
            }
         }

         return server.getPublicIp();
      } else {
         return server != null ? server.getPublicIp() : null;
      }
   }

   private static final class PortPair {
      private final Integer publicPort;
      private final Integer privatePort;

      private PortPair(Integer publicPort, Integer privatePort) {
         this.publicPort = publicPort;
         this.privatePort = privatePort;
      }

      private Integer getPublicPort() {
         return this.publicPort;
      }

      private Integer getPrivatePort() {
         return this.privatePort;
      }
   }
}
