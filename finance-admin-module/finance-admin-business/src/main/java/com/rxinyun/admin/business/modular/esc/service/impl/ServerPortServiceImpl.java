package com.rxinyun.admin.business.modular.esc.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.admin.business.modular.esc.entity.Server;
import com.rxinyun.admin.business.modular.esc.entity.ServerIp;
import com.rxinyun.admin.business.modular.esc.entity.ServerPort;
import com.rxinyun.admin.business.modular.esc.mapper.ServerPortMapper;
import com.rxinyun.admin.business.modular.esc.param.ServerPortAddParam;
import com.rxinyun.admin.business.modular.esc.param.ServerPortEditParam;
import com.rxinyun.admin.business.modular.esc.param.ServerPortIdParam;
import com.rxinyun.admin.business.modular.esc.param.ServerPortPageParam;
import com.rxinyun.admin.business.modular.esc.service.ServerIpService;
import com.rxinyun.admin.business.modular.esc.service.ServerPortService;
import com.rxinyun.admin.business.modular.esc.service.ServerService;
import com.rxinyun.admin.business.modular.esc.util.ResourcePoolUpstreamClient;
import com.rxinyun.common.controller.CloudControllerRouter;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.common.pojo.CommonEntity;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.common.qzsystem.entity.HostPort;
import com.rxinyun.common.util.SnowflakeIdGenerator;
import com.rxinyun.production.api.MemberMasterApi;
import com.rxinyun.production.core.pojo.BaseMemberMaster;
import jakarta.annotation.Resource;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServerPortServiceImpl extends ServiceImpl<ServerPortMapper, ServerPort> implements ServerPortService {
   private static final Logger log = LoggerFactory.getLogger(ServerPortServiceImpl.class);
   @Resource
   private MemberMasterApi memberMasterApi;
   @Resource
   private CloudControllerRouter cloudControllerRouter;
   @Resource
   private ServerService serverService;
   @Resource
   private ServerIpService serverIpService;
   @Resource
   private ServerPortMapper serverPortMapper;
   @Resource
   private ResourcePoolUpstreamClient resourcePoolUpstreamClient;

   @Override
   public Page<ServerPort> page(ServerPortPageParam pageParam) {
      QueryWrapper<ServerPort> queryWrapper = new QueryWrapper().checkSqlInjection();
      if (ObjectUtil.isNotEmpty(pageParam.getServerId())) {
         queryWrapper.lambda().and(q -> q.eq(ServerPort::getServerId, pageParam.getServerId()));
      }

      if (ObjectUtil.isAllNotEmpty(new Object[]{pageParam.getSortField(), pageParam.getSortOrder()})) {
         CommonSortOrderEnum.validate(pageParam.getSortOrder());
         queryWrapper.orderBy(true, pageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()), StrUtil.toUnderlineCase(pageParam.getSortField()));
      } else {
         queryWrapper.lambda().orderByDesc(CommonEntity::getCreateTime);
      }

      return (Page<ServerPort>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public ServerPort add(ServerPortAddParam serverPortAddParam) {
      Server memberServer = (Server)this.serverService.getOne((Wrapper<Server>)new LambdaQueryWrapper<Server>().eq(Server::getId, serverPortAddParam.getServerId()));
      if (null == memberServer) {
         throw new CommonException("服务器不存在，serverId值为：{}", new Object[]{serverPortAddParam.getServerId()});
      } else {
         log.info("服务器信息:{}", JSON.toJSONString(memberServer));
         if (!this.resourcePoolUpstreamClient.isResourcePoolServer(memberServer)) {
            if (!Integer.valueOf(1).equals(memberServer.getShareIp())) {
               return null;
            } else {
               BaseMemberMaster memberMaster = this.memberMasterApi.selectByLineId(memberServer.getLineId());
               ServerPort serverPort = (ServerPort)BeanUtil.toBean(serverPortAddParam, ServerPort.class);
               log.info("新增ip数据:{}", JSON.toJSONString(serverPort));
               ServerPortServiceImpl.PortPair portPair = this.resolvePortPair(
                  memberMaster.getIp(), serverPortAddParam.getPrivatePort(), serverPortAddParam.getPublicPort()
               );
               serverPort.setPublicPort(portPair.getPublicPort());
               serverPort.setPrivatePort(portPair.getPrivatePort());
               serverPort.setServerName(memberServer.getHostName());
               serverPort.setServerId(memberServer.getId());
               serverPort.setPublicIp(this.resolvePortForwardTargetIp(memberServer));
               serverPort.setPrivateIp(memberServer.getPrivateIp());
               serverPort.setMasterId(memberServer.getMasterId());
               serverPort.setMasterIp(memberMaster.getIp());
               this.save(serverPort);
               HostPort hostPort = new HostPort();
               hostPort.setApiPort(memberMaster.getApiPort());
               hostPort.setApiKey(memberMaster.getApiPassword());
               hostPort.setMasterIp(memberMaster.getIp());
               hostPort.setHostName(memberServer.getHostName());
               hostPort.setPublicPort(serverPort.getPublicPort());
               hostPort.setPublicIp(serverPort.getPublicIp());
               hostPort.setPrivatePort(serverPort.getPrivatePort());
               log.info("添加端口映射:{}", JSON.toJSONString(hostPort));
               this.cloudControllerRouter.getAdapterByGuardVersion(memberMaster.getGuardVersion()).addForwardPort(hostPort);
               this.serverService
                  .update(
                     (Wrapper<Server>)((LambdaUpdateWrapper<Server>)new LambdaUpdateWrapper<Server>().eq(Server::getId, memberServer.getId()))
                        .set(Server::getPort, serverPort.getPublicPort())
                  );
               return serverPort;
            }
         } else if (!Integer.valueOf(1).equals(memberServer.getShareIp())) {
            return null;
         } else {
            JSONObject b = new JSONObject();
            b.put("portName", serverPortAddParam.getPortName());
            if (serverPortAddParam.getPrivatePort() != null) {
               b.put("privatePort", serverPortAddParam.getPrivatePort());
            }

            if (serverPortAddParam.getPublicPort() != null) {
               b.put("publicPort", serverPortAddParam.getPublicPort());
            }

            CommonResult<?> r = this.resourcePoolUpstreamClient.callOrderServerApi(memberServer, "port/add", b);
            if (r.getCode() != null && Integer.valueOf(200).equals(r.getCode())) {
               Object data = r.getData();
               if (data == null) {
                  throw new CommonException("上游未返回端口映射数据", new Object[0]);
               } else {
                  ServerPort sp = (ServerPort)JSON.parseObject(JSON.toJSONString(data), ServerPort.class);
                  sp.setId(SnowflakeIdGenerator.generateId());
                  sp.setServerId(memberServer.getId());
                  this.save(sp);
                  if (sp.getPublicPort() != null) {
                     this.serverService
                        .update(
                           (Wrapper<Server>)((LambdaUpdateWrapper<Server>)new LambdaUpdateWrapper<Server>().eq(Server::getId, memberServer.getId()))
                              .set(Server::getPort, sp.getPublicPort())
                        );
                  }

                  return sp;
               }
            } else {
               throw new CommonException(r != null && StrUtil.isNotBlank(r.getMsg()) ? r.getMsg() : "上游添加端口映射失败", new Object[0]);
            }
         }
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void edit(ServerPortEditParam serverIpEditParam) {
      ServerPort serverIp = this.queryEntity(serverIpEditParam.getId());
      BeanUtil.copyProperties(serverIpEditParam, serverIp, new String[0]);
      this.updateById(serverIp);
   }

   @Override
   public ServerPort detail(ServerPortIdParam serverIpIdParam) {
      return this.queryEntity(serverIpIdParam.getId());
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void delete(List<ServerPortIdParam> serverPortIdParamList) {
      log.info("删除端口数据:{}", JSON.toJSONString(serverPortIdParamList));
      if (ObjectUtil.isNotEmpty(serverPortIdParamList)) {
         for (ServerPortIdParam serverPortIdParam : serverPortIdParamList) {
            ServerPort serverPort = this.queryEntity(serverPortIdParam.getId());
            if (null == serverPort) {
               throw new CommonException("服务器端口不存在，id值为：{}", new Object[]{serverPortIdParam.getId()});
            }

            Server memberServer = (Server)this.serverService.getOne((Wrapper<Server>)new LambdaQueryWrapper<Server>().eq(Server::getId, serverPort.getServerId()));
            if (null == memberServer) {
               throw new CommonException("服务器不存在，serverId值为：{}", new Object[]{serverPort.getServerId()});
            }

            if (this.resourcePoolUpstreamClient.isResourcePoolServer(memberServer)) {
               JSONObject b = new JSONObject();
               b.put("privatePort", serverPort.getPrivatePort());
               b.put("publicPort", serverPort.getPublicPort());
               CommonResult<?> r = this.resourcePoolUpstreamClient.callOrderServerApi(memberServer, "port/delete", b);
               if (r.getCode() == null || !Integer.valueOf(200).equals(r.getCode())) {
                  throw new CommonException(r != null && StrUtil.isNotBlank(r.getMsg()) ? r.getMsg() : "上游删除端口映射失败", new Object[0]);
               }

               this.serverPortMapper.deleteById(serverPortIdParam.getId());
            } else {
               this.serverPortMapper.deleteById(serverPortIdParam.getId());
               BaseMemberMaster memberMaster = this.memberMasterApi.selectByLineId(memberServer.getLineId());
               HostPort hostPort = new HostPort();
               hostPort.setApiPort(memberMaster.getApiPort());
               hostPort.setApiKey(memberMaster.getApiPassword());
               hostPort.setMasterIp(memberMaster.getIp());
               hostPort.setHostName(memberServer.getHostName());
               hostPort.setPublicPort(serverPort.getPublicPort());
               hostPort.setPublicIp(serverPort.getPublicIp());
               hostPort.setPrivatePort(serverPort.getPrivatePort());
               log.info("删除端口映射:{}", JSON.toJSONString(hostPort));
               this.cloudControllerRouter.getAdapterByGuardVersion(memberMaster.getGuardVersion()).removeForwardPort(hostPort);
            }
         }
      }
   }

   @Override
   public ServerPort queryEntity(String id) {
      ServerPort serverPort = (ServerPort)this.getById(id);
      if (ObjectUtil.isEmpty(serverPort)) {
         throw new CommonException("服务器端口不存在，id值为：{}", new Object[]{id});
      } else {
         return serverPort;
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   public int allocatePort(String hostId, int requestedPort) {
      if (requestedPort > 0) {
         if (this.serverPortMapper.checkPortExist(hostId, requestedPort) > 0) {
            throw new CommonException("Port " + requestedPort + " is already in use", new Object[0]);
         } else {
            return requestedPort;
         }
      } else {
         return this.findAvailablePort(hostId);
      }
   }

   private int findAvailablePort(String hostId) {
      int start = 2000;

      for (int port = start; port <= 65535; port++) {
         if (this.serverPortMapper.checkPortExist(hostId, port) == 0) {
            return port;
         }
      }

      throw new CommonException("No available ports", new Object[0]);
   }

   private ServerPortServiceImpl.PortPair resolvePortPair(String hostId, Integer requestedPrivatePort, Integer requestedPublicPort) {
      int allocatedPublicPort = this.allocatePort(hostId, requestedPublicPort != null ? requestedPublicPort : 0);
      return requestedPrivatePort == null
         ? new ServerPortServiceImpl.PortPair(allocatedPublicPort, allocatedPublicPort)
         : new ServerPortServiceImpl.PortPair(allocatedPublicPort, requestedPrivatePort);
   }

   private void savePort(String hostId, int port) {
      ServerPort hostPort = new ServerPort();
      this.serverPortMapper.insert(hostPort);
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   public void releasePort(String hostId, int port) {
      int count = this.serverPortMapper.deletePort(hostId, port);
      if (count == 0) {
         throw new CommonException("Port " + port + " was not allocated", new Object[0]);
      }
   }

   @Override
   public void deleteVps(String serverId) {
      Server memberServer = (Server)this.serverService.getOne((Wrapper<Server>)new LambdaQueryWrapper<Server>().eq(Server::getId, serverId));
      if (null == memberServer) {
         throw new CommonException("服务器不存在，serverId值为：{}", new Object[]{serverId});
      } else if (Integer.valueOf(1).equals(memberServer.getShareIp())) {
         BaseMemberMaster memberMaster = this.memberMasterApi.selectByLineId(memberServer.getLineId());
         List<ServerPort> serverPortList = this.list((Wrapper<ServerPort>)new LambdaQueryWrapper<ServerPort>().eq(ServerPort::getServerId, serverId));
         log.info("删除端口映射:{}", JSON.toJSONString(serverPortList));

         for (ServerPort serverPort : serverPortList) {
            HostPort hostPort = new HostPort();
            hostPort.setApiPort(memberMaster.getApiPort());
            hostPort.setApiKey(memberMaster.getApiPassword());
            hostPort.setMasterIp(memberMaster.getIp());
            hostPort.setHostName(memberServer.getHostName());
            hostPort.setPublicPort(serverPort.getPublicPort());
            hostPort.setPublicIp(serverPort.getPublicIp());
            hostPort.setPrivatePort(serverPort.getPrivatePort());
            log.info("删除端口映射:{}", JSON.toJSONString(hostPort));
            this.cloudControllerRouter.getAdapterByGuardVersion(memberMaster.getGuardVersion()).removeForwardPort(hostPort);
         }

         this.serverPortMapper.deleteByServerId(serverId);
      }
   }

   @Override
   public List<ServerPort> queryByServerId(String serverId) {
      return this.list((Wrapper<ServerPort>)new LambdaQueryWrapper<ServerPort>().eq(ServerPort::getServerId, serverId));
   }

   private String resolvePortForwardTargetIp(Server server) {
      if (server != null && Integer.valueOf(1).equals(server.getShareIp())) {
         for (ServerIp serverIp : this.serverIpService.queryByServerId(server.getId())) {
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
