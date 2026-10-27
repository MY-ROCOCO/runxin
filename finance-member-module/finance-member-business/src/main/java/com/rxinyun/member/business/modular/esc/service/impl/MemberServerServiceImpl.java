package com.rxinyun.member.business.modular.esc.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.UpdateChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.business.api.AutoTaskApi;
import com.rxinyun.business.api.ServerApi;
import com.rxinyun.business.core.enums.TaskTypeEnum;
import com.rxinyun.business.core.pojo.BaseAutoTask;
import com.rxinyun.business.core.pojo.BasePoolPasswordParam;
import com.rxinyun.business.core.pojo.BasePoolReinstallParam;
import com.rxinyun.common.controller.CloudControllerAdapter;
import com.rxinyun.common.controller.CloudControllerRouter;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import com.rxinyun.common.enums.business.BusinessStatusEnum;
import com.rxinyun.common.enums.business.NetworkStatusEnum;
import com.rxinyun.common.enums.business.ServerStatusEnum;
import com.rxinyun.common.enums.order.ProductTypeEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.common.pojo.CommonEntity;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.common.qzsystem.entity.HostBackupParam;
import com.rxinyun.common.qzsystem.entity.HostIp;
import com.rxinyun.common.qzsystem.entity.HostParam;
import com.rxinyun.common.util.CommonTimeFormatUtil;
import com.rxinyun.common.util.OsInstancePasswordValidator;
import com.rxinyun.common.util.SnowflakeIdGenerator;
import com.rxinyun.dev.api.DevConfigApi;
import com.rxinyun.member.business.modular.esc.entity.MemberServer;
import com.rxinyun.member.business.modular.esc.entity.MemberServerBackupRecord;
import com.rxinyun.member.business.modular.esc.entity.MemberServerIp;
import com.rxinyun.member.business.modular.esc.entity.MemberServerSnapshotRecord;
import com.rxinyun.member.business.modular.esc.mapper.MemberServerBackupRecordMapper;
import com.rxinyun.member.business.modular.esc.mapper.MemberServerMapper;
import com.rxinyun.member.business.modular.esc.mapper.MemberServerSnapshotRecordMapper;
import com.rxinyun.member.business.modular.esc.param.MemberRemarkEditParam;
import com.rxinyun.member.business.modular.esc.param.MemberServerAddParam;
import com.rxinyun.member.business.modular.esc.param.MemberServerBackupCreateParam;
import com.rxinyun.member.business.modular.esc.param.MemberServerBackupIdParam;
import com.rxinyun.member.business.modular.esc.param.MemberServerCpuHistoryResult;
import com.rxinyun.member.business.modular.esc.param.MemberServerIdListParam;
import com.rxinyun.member.business.modular.esc.param.MemberServerIdParam;
import com.rxinyun.member.business.modular.esc.param.MemberServerIpAddParam;
import com.rxinyun.member.business.modular.esc.param.MemberServerIpParam;
import com.rxinyun.member.business.modular.esc.param.MemberServerNetworkHistoryResult;
import com.rxinyun.member.business.modular.esc.param.MemberServerPageParam;
import com.rxinyun.member.business.modular.esc.param.MemberServerRenewAutoParam;
import com.rxinyun.member.business.modular.esc.param.MemberServerSnapshotCreateParam;
import com.rxinyun.member.business.modular.esc.param.MemberServerSnapshotIdParam;
import com.rxinyun.member.business.modular.esc.param.MemberServerTransferRecordAddParam;
import com.rxinyun.member.business.modular.esc.param.MemberServerTransferRecordIdParam;
import com.rxinyun.member.business.modular.esc.param.PasswordEditParam;
import com.rxinyun.member.business.modular.esc.param.ReinstallEditParam;
import com.rxinyun.member.business.modular.esc.service.MemberServerIpService;
import com.rxinyun.member.business.modular.esc.service.MemberServerService;
import com.rxinyun.member.business.modular.esc.service.MemberServerTransferRecordService;
import com.rxinyun.member.core.util.StpClientLoginUserUtil;
import com.rxinyun.member.member.pojo.SaBaseClientLoginUser;
import com.rxinyun.production.api.MemberLocalLineApi;
import com.rxinyun.production.api.MemberMasterApi;
import com.rxinyun.production.api.MemberNatNetworkApi;
import com.rxinyun.production.api.MemberPrivateNetworkApi;
import com.rxinyun.production.api.MemberPublicNetworkApi;
import com.rxinyun.production.api.MemberSystemVersionApi;
import com.rxinyun.production.core.pojo.BaseMemberLocalLine;
import com.rxinyun.production.core.pojo.BaseMemberMaster;
import com.rxinyun.production.core.pojo.BaseMemberPublicNetwork;
import com.rxinyun.production.core.pojo.BaseMemberSystemVersion;
import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberServerServiceImpl extends ServiceImpl<MemberServerMapper, MemberServer> implements MemberServerService {
   private static final Logger log = LoggerFactory.getLogger(MemberServerServiceImpl.class);
   private static final String LOCKED_OPERATION_MESSAGE = "云服务器已锁定，请先解锁后再操作";
   @Resource
   private MemberServerMapper memberServerMapper;
   @Resource
   private MemberPrivateNetworkApi memberPrivateNetworkApi;
   @Resource
   private MemberPublicNetworkApi memberPublicNetworkApi;
   @Resource
   private MemberNatNetworkApi memberNatNetworkApi;
   @Resource
   private MemberMasterApi memberMasterApi;
   @Resource
   private ServerApi serverApi;
   @Resource
   private MemberSystemVersionApi memberOsVersionApi;
   @Resource
   private MemberServerIpService memberServerIpService;
   @Resource
   private MemberLocalLineApi memberLocalLineApi;
   @Resource
   private AutoTaskApi autoTaskApi;
   @Resource
   private RedisTemplate<String, Object> redisTemplate;
   @Resource
   private MemberServerTransferRecordService memberServerTransferRecordService;
   @Resource
   private MemberServerBackupRecordMapper memberServerBackupRecordMapper;
   @Resource
   private MemberServerSnapshotRecordMapper memberServerSnapshotRecordMapper;
   @Resource
   private CloudControllerRouter cloudControllerRouter;

   @Override
   public Page<MemberServer> page(MemberServerPageParam pageParam) {
      SaBaseClientLoginUser clientLoginUser = StpClientLoginUserUtil.getClientLoginUser();
      QueryWrapper<MemberServer> queryWrapper = new QueryWrapper().checkSqlInjection();
      queryWrapper.lambda().eq(MemberServer::getMemberNo, clientLoginUser.getId());
      if (ObjectUtil.isNotEmpty(pageParam.getStatus())) {
         queryWrapper.lambda().and(q -> q.eq(MemberServer::getServerStatus, pageParam.getStatus()));
      }

      if (StrUtil.isNotBlank(pageParam.getServerName())) {
         queryWrapper.lambda().and(q -> q.like(MemberServer::getHostName, pageParam.getServerName()));
      } else if (ObjectUtil.isNotEmpty(pageParam.getSearchKey())) {
         queryWrapper.lambda().and(q -> q.like(MemberServer::getHostName, pageParam.getSearchKey()));
      }

      if (ObjectUtil.isNotEmpty(pageParam.getBusinessStatus())) {
         queryWrapper.lambda().and(q -> q.eq(MemberServer::getBusinessStatus, pageParam.getBusinessStatus()));
      }

      if (StrUtil.isNotBlank(pageParam.getServerIp())) {
         List<String> serverIds = this.memberServerIpService.listServerIdByIp(pageParam.getServerIp());
         if (serverIds.isEmpty()) {
            queryWrapper.lambda().and(q -> q.eq(MemberServer::getId, ""));
         } else {
            queryWrapper.lambda().and(q -> q.in(MemberServer::getId, serverIds));
         }
      }

      if (ObjectUtil.isAllNotEmpty(new Object[]{pageParam.getSortField(), pageParam.getSortOrder()})) {
         CommonSortOrderEnum.validate(pageParam.getSortOrder());
         queryWrapper.orderBy(true, pageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()), this.resolveServerSortField(pageParam.getSortField()));
      } else {
         queryWrapper.lambda().orderByDesc(CommonEntity::getCreateTime);
      }

      Page<MemberServer> pageResult = (Page<MemberServer>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
      this.fillLineAndAreaNames(pageResult.getRecords());
      this.fillCurrentIpNum(pageResult.getRecords());

      for (MemberServer server : pageResult.getRecords()) {
         this.fillGuardVersion(server);
      }

      return pageResult;
   }

   private void fillCurrentIpNum(List<MemberServer> servers) {
      if (servers != null && !servers.isEmpty()) {
         List<String> serverIds = servers.stream().map(MemberServer::getId).collect(Collectors.toList());
         Map<String, Integer> ipNumMap = this.memberServerIpService.countPublicIpByServerIds(serverIds);

         for (MemberServer server : servers) {
            server.setIpNum(ipNumMap.getOrDefault(server.getId(), 0));
         }
      }
   }

   private String resolveServerSortField(String sortField) {
      if (StrUtil.equals(sortField, "lineName")) {
         return "line_id";
      } else {
         return StrUtil.equals(sortField, "areaName") ? "available_id" : StrUtil.toUnderlineCase(sortField);
      }
   }

   private void fillCurrentIpNum(MemberServer server) {
      if (server != null) {
         Map<String, Integer> ipNumMap = this.memberServerIpService.countPublicIpByServerIds(List.of(server.getId()));
         server.setIpNum(ipNumMap.getOrDefault(server.getId(), 0));
      }
   }

   private void fillGuardVersion(MemberServer server) {
      if (server != null && server.getMasterId() != null) {
         try {
            BaseMemberMaster master = this.memberMasterApi.selectByMasterId(server.getMasterId());
            if (master != null && master.getGuardVersion() != null) {
               server.setGuardVersion(master.getGuardVersion());
            }
         } catch (Exception var3) {
            log.debug("填充宿主机受控类型失败 masterId={}", server.getMasterId(), var3);
         }
      }
   }

   private boolean isResourcePoolServer(MemberServer s) {
      if (s == null) {
         return false;
      } else {
         return Integer.valueOf(1).equals(s.getType()) ? true : StrUtil.isBlank(s.getMasterId()) && StrUtil.isNotBlank(s.getResourcePoolId());
      }
   }

   @Override
   public List<MemberServer> list(MemberServerIdListParam memberServerIdListParam) {
      SaBaseClientLoginUser clientLoginUser = StpClientLoginUserUtil.getClientLoginUser();
      return this.list(
         (Wrapper<MemberServer>)((LambdaQueryWrapper<MemberServer>)new QueryWrapper<MemberServer>().lambda().eq(MemberServer::getMemberNo, clientLoginUser.getId()))
            .in(MemberServer::getId, memberServerIdListParam.getServerIds())
      );
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void add(MemberServerAddParam memberServerAddParam) {
      try {
         OsInstancePasswordValidator.validateOrThrow(memberServerAddParam.getSystemPassword());
         BaseMemberLocalLine line = this.memberLocalLineApi.selectByLineId(memberServerAddParam.getLineId());
         if (line == null) {
            throw new CommonException("该线路不存在", new Object[0]);
         } else {
            Integer ipNum = memberServerAddParam.getIpNum();
            if (ipNum == null) {
               ipNum = (Integer)ObjectUtil.defaultIfNull(line.getIpFreeSize(), 1);
            }

            if (ipNum == null || ipNum < 1) {
               throw new CommonException("IP数量必须大于0", new Object[0]);
            } else if (line.getIpFreeSize() != null && ipNum < line.getIpFreeSize()) {
               throw new CommonException("不能低于赠送IP数量", new Object[0]);
            } else if (line.getIpMinSize() != null && ipNum < line.getIpMinSize()) {
               throw new CommonException("IP数量不能小于线路最小值", new Object[0]);
            } else if (line.getIpMaxSize() != null && ipNum > line.getIpMaxSize()) {
               throw new CommonException("IP数量不能大于线路最大值", new Object[0]);
            } else {
               memberServerAddParam.setIpNum(ipNum);
               MemberServer memberServer = (MemberServer)BeanUtil.toBean(memberServerAddParam, MemberServer.class);
               memberServer.setBusinessStatus(BusinessStatusEnum.NORMAL.getCode());
               memberServer.setServerStatus(ServerStatusEnum.CREATING.getCode());
               DevConfigApi devConfigApi = (DevConfigApi)SpringUtil.getBean(DevConfigApi.class);
               String cloudServerRetentionPeriod = devConfigApi.getValueByKey("CLOUD_SERVER_RETENTION_PERIOD");
               memberServer.setDeleteTime(DateUtil.offsetDay(memberServerAddParam.getEndTime(), Integer.valueOf(cloudServerRetentionPeriod)));
               this.saveOrUpdate(memberServer);
               if (memberServer.getId() == null) {
                  throw new CommonException("ID 未生成", new Object[0]);
               } else {
                  memberServerAddParam.setId(memberServer.getId());
                  log.info("Saved memberServer with ID: {}", memberServer.getId());
                  log.info("创建云服务器开始，HOST_NAME:{}", memberServerAddParam.getHostName());
                  BaseAutoTask taskParam = new BaseAutoTask();
                  taskParam.setMasterId(memberServer.getMasterId());
                  taskParam.setServerId(memberServer.getId());
                  taskParam.setProductType(1);
                  taskParam.setTaskType(TaskTypeEnum.CREATE.getCode());
                  taskParam.setExecuteTime(CommonTimeFormatUtil.formatNowGmt8());
                  taskParam.setTaskStatus(1);
                  taskParam.setNextRetryTime(new Date());
                  taskParam.setTaskDesc("创建云服务器任务");
                  taskParam.setPlatform("C");
                  JSONObject paramObj = new JSONObject();
                  paramObj.put("serverId", memberServer.getId());
                  paramObj.put("ipNum", ipNum);
                  taskParam.setParams(JSON.toJSONString(paramObj));
                  taskParam.setRetryCount(0);
                  taskParam.setMaxRetry(3);
                  taskParam.setTimeoutTime(DateUtil.offsetHour(new Date(), 2));
                  this.autoTaskApi.add(taskParam);
               }
            }
         }
      } catch (CommonException var9) {
         log.error("创建云服务器失败: {}", var9.getMessage(), var9);
         throw var9;
      } catch (Exception var10) {
         log.error("创建云服务器时发生未知错误: {}", var10.getMessage(), var10);
         throw new CommonException("创建云服务器失败", new Object[]{var10.getMessage()});
      }
   }

   @Override
   public MemberServer detail(MemberServerIdParam serverIdParam) {
      MemberServer server = this.queryEntity(serverIdParam.getId());
      this.fillLineAndAreaNames(server);
      this.fillCurrentIpNum(server);
      this.fillGuardVersion(server);
      return server;
   }

   @Override
   public MemberServer queryEntity(String id) {
      SaBaseClientLoginUser clientLoginUser = StpClientLoginUserUtil.getClientLoginUser();
      MemberServer memberServer = (MemberServer)this.getOne(
         (Wrapper<MemberServer>)((LambdaQueryWrapper<MemberServer>)new LambdaQueryWrapper<MemberServer>().eq(MemberServer::getMemberNo, clientLoginUser.getId())).eq(MemberServer::getId, id)
      );
      if (ObjectUtil.isEmpty(memberServer)) {
         throw new CommonException("云服务器不存在，id值为：{}", new Object[]{id});
      } else {
         return memberServer;
      }
   }

   private void fillLineAndAreaNames(MemberServer server) {
      if (server != null) {
         this.fillLineAndAreaNames(List.of(server));
      }
   }

   private void fillLineAndAreaNames(List<MemberServer> servers) {
      if (servers != null && !servers.isEmpty()) {
         List<String> lineIds = servers.stream().map(MemberServer::getLineId).filter(CharSequenceUtil::isNotBlank).distinct().collect(Collectors.toList());
         if (!lineIds.isEmpty()) {
            Map<String, BaseMemberLocalLine> lineMap = this.memberLocalLineApi.selectByLineIds(lineIds);

            for (MemberServer server : servers) {
               BaseMemberLocalLine line = lineMap.get(server.getLineId());
               if (line != null) {
                  server.setLineName(line.getLineName());
                  server.setAreaName(line.getAvailableName());
               }
            }
         }
      }
   }

   @Override
   public MemberServer getByIdAndMemberNo(String id, String memberNo) {
      return !ObjectUtil.isEmpty(id) && !ObjectUtil.isEmpty(memberNo)
         ? (MemberServer)this.getOne(
            (Wrapper<MemberServer>)((LambdaQueryWrapper<MemberServer>)new LambdaQueryWrapper<MemberServer>().eq(MemberServer::getId, id)).eq(MemberServer::getMemberNo, memberNo)
         )
         : null;
   }

   @Override
   public void editRemark(MemberRemarkEditParam memberRemarkEditParam) {
      SaBaseClientLoginUser clientLoginUser = StpClientLoginUserUtil.getClientLoginUser();
      MemberServer memberServer = (MemberServer)this.getOne(
         (Wrapper<MemberServer>)((LambdaQueryWrapper<MemberServer>)new LambdaQueryWrapper<MemberServer>().eq(MemberServer::getMemberNo, clientLoginUser.getId()))
            .eq(MemberServer::getId, memberRemarkEditParam.getId())
      );
      if (ObjectUtil.isEmpty(memberServer)) {
         throw new CommonException("云服务器不存在，id值为：{}", new Object[]{memberRemarkEditParam.getId()});
      } else {
         memberServer.setMemberRemark(memberRemarkEditParam.getMemberRemark());
         this.updateById(memberServer);
      }
   }

   @Override
   public CommonResult startServer(MemberServerIdParam memberServerIdParam) {
      String lockKey = "server:operation_lock:" + memberServerIdParam.getId();
      String lockValue = UUID.randomUUID().toString();
      long expireTime = 300L;
      boolean locked = false;

      CommonResult var9;
      try {
         if (!this.tryLock(lockKey, lockValue, expireTime)) {
            return CommonResult.error("服务器正在执行其他操作，请稍后再试");
         }

         locked = true;
         SaBaseClientLoginUser clientLoginUser = StpClientLoginUserUtil.getClientLoginUser();
         MemberServer memberServer = this.queryEntity(memberServerIdParam.getId());
         if (!memberServer.getMemberNo().equals(String.valueOf(clientLoginUser.getId()))) {
            throw new CommonException("您没有权限操作该服务器", new Object[0]);
         }

         if (memberServer.getServerStatus().equals(ServerStatusEnum.CREATING.getCode())) {
            throw new CommonException("服务器创建中，请等待......", new Object[0]);
         }

         if (memberServer.getServerStatus().equals(ServerStatusEnum.LOCKED.getCode())) {
            throw new CommonException("服务器已锁定，请联系客服", new Object[0]);
         }

         if (memberServer.getServerStatus().equals(ServerStatusEnum.REINSTALLING.getCode())) {
            throw new CommonException("服务器重装系统中，请等待......", new Object[0]);
         }

         if (!this.isResourcePoolServer(memberServer)) {
            return this.startSelfProducedServer(memberServer);
         }

         var9 = this.startResourcePoolServer(memberServer);
      } catch (Exception var13) {
         log.error("启动服务器失败", var13);
         return CommonResult.error(var13.getMessage());
      } finally {
         if (locked) {
            this.releaseLock(lockKey, lockValue);
         }
      }

      return var9;
   }

   @Override
   public CommonResult closeServer(MemberServerIdParam memberServerIdParam) {
      String lockKey = "server:operation_lock:" + memberServerIdParam.getId();
      String lockValue = UUID.randomUUID().toString();
      long expireTime = 300L;
      boolean locked = false;

      CommonResult var9;
      try {
         if (!this.tryLock(lockKey, lockValue, expireTime)) {
            return CommonResult.error("服务器正在执行其他操作，请稍后再试");
         }

         locked = true;
         SaBaseClientLoginUser clientLoginUser = StpClientLoginUserUtil.getClientLoginUser();
         MemberServer memberServer = this.queryEntity(memberServerIdParam.getId());
         if (!memberServer.getMemberNo().equals(String.valueOf(clientLoginUser.getId()))) {
            throw new CommonException("您没有权限操作该服务器", new Object[0]);
         }

         this.assertServerNotLocked(memberServer);
         if (!memberServer.getServerStatus().equals(ServerStatusEnum.RUNNING.getCode())) {
            throw new CommonException("服务器状态异常，无法关闭", new Object[0]);
         }

         if (!this.isResourcePoolServer(memberServer)) {
            return this.closeSelfProducedServer(memberServer);
         }

         var9 = this.closeResourcePoolServer(memberServer);
      } catch (Exception var13) {
         log.error("关闭服务器失败", var13);
         return CommonResult.error(var13.getMessage());
      } finally {
         if (locked) {
            this.releaseLock(lockKey, lockValue);
         }
      }

      return var9;
   }

   private CommonResult startResourcePoolServer(MemberServer memberServer) {
      CommonResult startResult = this.serverApi.startCloudServer(memberServer.getId());
      if (startResult != null && Integer.valueOf(200).equals(startResult.getCode())) {
         memberServer.setServerStatus(ServerStatusEnum.STARTING.getCode());
         this.updateById(memberServer);
      }

      return startResult;
   }

   private CommonResult startSelfProducedServer(MemberServer memberServer) {
      BaseMemberMaster memberMaster = this.memberMasterApi.selectByLineId(memberServer.getLineId());
      HostParam hostParam = new HostParam();
      hostParam.setHostName(memberServer.getHostName());
      hostParam.setServerId(memberServer.getId());
      hostParam.setMasterIp(this.getMasterIpForTask(memberMaster));
      hostParam.setApiKey(memberMaster.getApiPassword());
      hostParam.setApiPort(memberMaster.getApiPort());
      log.info("开机请求:{}", JSON.toJSONString(hostParam));
      BaseAutoTask taskParam = new BaseAutoTask();
      taskParam.setMasterId(memberMaster.getId());
      taskParam.setServerId(memberServer.getId());
      taskParam.setProductType(1);
      taskParam.setTaskType(TaskTypeEnum.START.getCode());
      taskParam.setExecuteTime(CommonTimeFormatUtil.formatNowGmt8());
      taskParam.setTaskStatus(1);
      taskParam.setNextRetryTime(new Date());
      taskParam.setTaskDesc("启动云服务器任务");
      taskParam.setPlatform("C");
      taskParam.setParams(JSONUtil.toJsonStr(hostParam));
      taskParam.setRetryCount(0);
      taskParam.setMaxRetry(3);
      taskParam.setTimeoutTime(DateUtil.offsetHour(new Date(), 2));
      return this.createTaskAndUpdateServerStatus(taskParam, memberServer, ServerStatusEnum.STARTING.getCode(), "启动任务已创建");
   }

   private CommonResult closeResourcePoolServer(MemberServer memberServer) {
      CommonResult stopResult = this.serverApi.stopCloudServer(memberServer.getId());
      if (stopResult != null && Integer.valueOf(200).equals(stopResult.getCode())) {
         memberServer.setServerStatus(ServerStatusEnum.STOPPING.getCode());
         this.updateById(memberServer);
      }

      return stopResult;
   }

   private CommonResult closeSelfProducedServer(MemberServer memberServer) {
      BaseMemberMaster memberMaster = this.memberMasterApi.selectByMasterId(memberServer.getMasterId());
      if (memberMaster == null) {
         return CommonResult.error("宿主机不存在");
      } else {
         HostParam hostParam = new HostParam();
         hostParam.setHostName(memberServer.getHostName());
         hostParam.setServerId(memberServer.getId());
         hostParam.setMasterIp(this.getMasterIpForTask(memberMaster));
         hostParam.setApiKey(memberMaster.getApiPassword());
         hostParam.setApiPort(memberMaster.getApiPort());
         log.info("关机请求:{}", JSON.toJSONString(hostParam));
         BaseAutoTask taskParam = new BaseAutoTask();
         taskParam.setMasterId(memberMaster.getId());
         taskParam.setServerId(memberServer.getId());
         taskParam.setProductType(1);
         taskParam.setTaskType(TaskTypeEnum.STOP.getCode());
         taskParam.setExecuteTime(CommonTimeFormatUtil.formatNowGmt8());
         taskParam.setTaskStatus(1);
         taskParam.setNextRetryTime(new Date());
         taskParam.setTaskDesc("关闭云服务器任务");
         taskParam.setPlatform("C");
         taskParam.setParams(JSONUtil.toJsonStr(hostParam));
         taskParam.setRetryCount(0);
         taskParam.setMaxRetry(3);
         taskParam.setTimeoutTime(DateUtil.offsetHour(new Date(), 2));
         return this.createTaskAndUpdateServerStatus(taskParam, memberServer, ServerStatusEnum.STOPPING.getCode(), "关闭任务已创建");
      }
   }

   @Override
   public CommonResult monitorServer(MemberServerIdParam memberServerIdParam) {
      SaBaseClientLoginUser clientLoginUser = StpClientLoginUserUtil.getClientLoginUser();
      MemberServer memberServer = this.queryEntity(memberServerIdParam.getId());
      if (!memberServer.getMemberNo().equals(String.valueOf(clientLoginUser.getId()))) {
         throw new CommonException("您没有权限操作该服务器", new Object[0]);
      } else if (this.isResourcePoolServer(memberServer)) {
         return this.serverApi.monitorCloudServer(memberServer.getId());
      } else {
         BaseMemberMaster memberMaster = this.memberMasterApi.selectByMasterId(memberServer.getMasterId());
         CloudControllerAdapter adapter = this.cloudControllerRouter.getAdapterByGuardVersion(memberMaster.getGuardVersion());
         log.info(
            "监控路由: serverId={}, masterId={}, guardVersion={}, adapter={}",
            new Object[]{memberServer.getId(), memberServer.getMasterId(), memberMaster.getGuardVersion(), adapter.getClass().getName()}
         );
         HostParam hostParam = new HostParam();
         hostParam.setHostName(memberServer.getHostName());
         hostParam.setServerId(memberServer.getId());
         hostParam.setMasterIp(this.getMasterIpForTask(memberMaster));
         hostParam.setApiKey(memberMaster.getApiPassword());
         hostParam.setApiPort(memberMaster.getApiPort());
         this.fillMonitorQueryParam(hostParam, memberServerIdParam);
         if ("2".equals(memberMaster.getGuardVersion())) {
            hostParam.setMfcCloudId(memberServer.getMfcCloudId());
            hostParam.setMfcLoginAccount(memberMaster.getMfcLoginAccount());
            hostParam.setMfcLoginPassword(memberMaster.getMfcLoginPassword());
         }

         return adapter.monitorHost(hostParam);
      }
   }

   private void fillMonitorQueryParam(HostParam hostParam, MemberServerIdParam param) {
      if (hostParam != null && param != null) {
         hostParam.setDateType(param.getDateType());
         hostParam.setMinDateTime(param.getMinDateTime());
         hostParam.setMaxDateTime(param.getMaxDateTime());
         hostParam.setSort(param.getSort());
      }
   }

   @Override
   public CommonResult reinstallServer(ReinstallEditParam reinstallEditParam) {
      String lockKey = "server:operation_lock:" + reinstallEditParam.getId();
      String lockValue = UUID.randomUUID().toString();
      long expireTime = 300L;
      boolean locked = false;

      CommonResult var11;
      try {
         if (!this.tryLock(lockKey, lockValue, expireTime)) {
            return CommonResult.error("服务器正在执行其他操作，请稍后再试");
         }

         locked = true;
         SaBaseClientLoginUser clientLoginUser = StpClientLoginUserUtil.getClientLoginUser();
         log.info("登录用户信息：{}", clientLoginUser);
         String pwdErr = OsInstancePasswordValidator.validate(reinstallEditParam.getOsPassword());
         if (pwdErr != null) {
            return CommonResult.error(pwdErr);
         }

         MemberServer server = (MemberServer)this.getOne((Wrapper<MemberServer>)new LambdaQueryWrapper<MemberServer>().eq(MemberServer::getId, reinstallEditParam.getId()));
         if (null == server) {
            return CommonResult.error("服务器不存在");
         }

         if (!server.getMemberNo().equals(String.valueOf(clientLoginUser.getId()))) {
            throw new CommonException("您没有权限操作该服务器", new Object[0]);
         }

         this.assertServerNotLocked(server);
         BaseMemberSystemVersion memberOsVersion = this.memberOsVersionApi.selectByOsVersionId(reinstallEditParam.getOsVersionId());
         if (null == memberOsVersion) {
            return CommonResult.error("操作系统版本信息不存在");
         }

         if (!this.isResourcePoolServer(server)) {
            return this.reinstallSelfProducedServer(server, reinstallEditParam, memberOsVersion);
         }

         var11 = this.reinstallResourcePoolServer(server, reinstallEditParam, memberOsVersion);
      } catch (Exception var15) {
         log.error("重装服务器失败", var15);
         return CommonResult.error(var15.getMessage());
      } finally {
         if (locked) {
            this.releaseLock(lockKey, lockValue);
         }
      }

      return var11;
   }

   @Override
   public CommonResult lockServer(MemberServerIdParam serverIdParam) {
      String lockKey = "server:operation_lock:" + serverIdParam.getId();
      String lockValue = UUID.randomUUID().toString();
      long expireTime = 300L;
      boolean locked = false;

      CommonResult var9;
      try {
         if (!this.tryLock(lockKey, lockValue, expireTime)) {
            return CommonResult.error("服务器正在执行其他操作，请稍后再试");
         }

         locked = true;
         SaBaseClientLoginUser clientLoginUser = StpClientLoginUserUtil.getClientLoginUser();
         MemberServer server = this.queryEntity(serverIdParam.getId());
         if (!server.getMemberNo().equals(String.valueOf(clientLoginUser.getId()))) {
            throw new CommonException("您没有权限操作该服务器", new Object[0]);
         }

         if (server.getServerStatus().equals(ServerStatusEnum.LOCKED.getCode())) {
            throw new CommonException("服务器已锁定，请联系客服解锁", new Object[0]);
         }

         if (!this.isResourcePoolServer(server)) {
            return this.lockSelfProducedServer(server);
         }

         var9 = this.lockResourcePoolServer(server);
      } catch (Exception var13) {
         log.error("锁定服务器失败", var13);
         return CommonResult.error(var13.getMessage());
      } finally {
         if (locked) {
            this.releaseLock(lockKey, lockValue);
         }
      }

      return var9;
   }

   @Override
   public CommonResult unlockServer(MemberServerIdParam serverIdParam) {
      String lockKey = "server:operation_lock:" + serverIdParam.getId();
      String lockValue = UUID.randomUUID().toString();
      long expireTime = 300L;
      boolean locked = false;

      CommonResult var9;
      try {
         if (!this.tryLock(lockKey, lockValue, expireTime)) {
            return CommonResult.error("服务器正在执行其他操作，请稍后再试");
         }

         locked = true;
         SaBaseClientLoginUser clientLoginUser = StpClientLoginUserUtil.getClientLoginUser();
         MemberServer server = this.queryEntity(serverIdParam.getId());
         if (!server.getMemberNo().equals(String.valueOf(clientLoginUser.getId()))) {
            throw new CommonException("您没有权限操作该服务器", new Object[0]);
         }

         if (!server.getServerStatus().equals(ServerStatusEnum.LOCKED.getCode())) {
            throw new CommonException("服务器状态异常，无法解锁", new Object[0]);
         }

         if (!this.isResourcePoolServer(server)) {
            return this.unlockSelfProducedServer(server);
         }

         var9 = this.unlockResourcePoolServer(server);
      } catch (Exception var13) {
         log.error("解锁服务器失败", var13);
         return CommonResult.error(var13.getMessage());
      } finally {
         if (locked) {
            this.releaseLock(lockKey, lockValue);
         }
      }

      return var9;
   }

   @Override
   public CommonResult rebuildServer(MemberServerIdParam serverIdParam) {
      String lockKey = "server:operation_lock:" + serverIdParam.getId();
      String lockValue = UUID.randomUUID().toString();
      long expireTime = 300L;
      boolean locked = false;

      CommonResult var9;
      try {
         if (!this.tryLock(lockKey, lockValue, expireTime)) {
            return CommonResult.error("服务器正在执行其他操作，请稍后再试");
         }

         locked = true;
         SaBaseClientLoginUser clientLoginUser = StpClientLoginUserUtil.getClientLoginUser();
         MemberServer server = this.queryEntity(serverIdParam.getId());
         if (null == server) {
            return CommonResult.error("服务器不存在");
         }

         if (!server.getMemberNo().equals(String.valueOf(clientLoginUser.getId()))) {
            throw new CommonException("您没有权限操作该服务器", new Object[0]);
         }

         this.assertServerNotLocked(server);
         if (!this.isResourcePoolServer(server)) {
            return this.rebuildSelfProducedServer(server, serverIdParam.getFullRebuild());
         }

         var9 = this.rebuildResourcePoolServer(server, serverIdParam.getFullRebuild());
      } catch (Exception var13) {
         log.error("重建服务器失败", var13);
         return CommonResult.error(var13.getMessage());
      } finally {
         if (locked) {
            this.releaseLock(lockKey, lockValue);
         }
      }

      return var9;
   }

   @Override
   public CommonResult updatePasswordServer(PasswordEditParam passwordEditParam) {
      String lockKey = "server:operation_lock:" + passwordEditParam.getId();
      String lockValue = UUID.randomUUID().toString();
      long expireTime = 300L;
      boolean locked = false;

      CommonResult var10;
      try {
         if (!this.tryLock(lockKey, lockValue, expireTime)) {
            return CommonResult.error("服务器正在执行其他操作，请稍后再试");
         }

         locked = true;
         SaBaseClientLoginUser clientLoginUser = StpClientLoginUserUtil.getClientLoginUser();
         MemberServer server = this.queryEntity(passwordEditParam.getId());
         if (null == server) {
            return CommonResult.error("服务器不存在");
         }

         String pwdErr = OsInstancePasswordValidator.validate(passwordEditParam.getOsPassword());
         if (pwdErr != null) {
            return CommonResult.error(pwdErr);
         }

         if (!server.getMemberNo().equals(String.valueOf(clientLoginUser.getId()))) {
            throw new CommonException("您没有权限操作该服务器", new Object[0]);
         }

         this.assertServerNotLocked(server);
         if (!this.isResourcePoolServer(server)) {
            return this.updatePasswordSelfProducedServer(server, passwordEditParam);
         }

         var10 = this.updatePasswordResourcePoolServer(server, passwordEditParam);
      } catch (Exception var14) {
         log.error("修改服务器密码失败", var14);
         return CommonResult.error(var14.getMessage());
      } finally {
         if (locked) {
            this.releaseLock(lockKey, lockValue);
         }
      }

      return var10;
   }

   @Override
   public CommonResult restartServer(MemberServerIdParam serverIdParam) {
      String lockKey = "server:operation_lock:" + serverIdParam.getId();
      String lockValue = UUID.randomUUID().toString();
      long expireTime = 300L;
      boolean locked = false;

      CommonResult var9;
      try {
         if (!this.tryLock(lockKey, lockValue, expireTime)) {
            return CommonResult.error("服务器正在执行其他操作，请稍后再试");
         }

         locked = true;
         SaBaseClientLoginUser clientLoginUser = StpClientLoginUserUtil.getClientLoginUser();
         MemberServer server = this.queryEntity(serverIdParam.getId());
         if (null == server) {
            return CommonResult.error("服务器不存在");
         }

         if (!server.getMemberNo().equals(String.valueOf(clientLoginUser.getId()))) {
            throw new CommonException("您没有权限操作该服务器", new Object[0]);
         }

         this.assertServerNotLocked(server);
         if (!this.isResourcePoolServer(server)) {
            return this.restartSelfProducedServer(server);
         }

         var9 = this.restartResourcePoolServer(server);
      } catch (Exception var13) {
         log.error("重启服务器失败", var13);
         return CommonResult.error(var13.getMessage());
      } finally {
         if (locked) {
            this.releaseLock(lockKey, lockValue);
         }
      }

      return var9;
   }

   @Override
   public CommonResult powerServer(MemberServerIdParam serverIdParam) {
      String lockKey = "server:operation_lock:" + serverIdParam.getId();
      String lockValue = UUID.randomUUID().toString();
      long expireTime = 300L;
      boolean locked = false;

      CommonResult var9;
      try {
         if (!this.tryLock(lockKey, lockValue, expireTime)) {
            return CommonResult.error("服务器正在执行其他操作，请稍后再试");
         }

         locked = true;
         SaBaseClientLoginUser clientLoginUser = StpClientLoginUserUtil.getClientLoginUser();
         MemberServer server = this.queryEntity(serverIdParam.getId());
         if (null == server) {
            return CommonResult.error("服务器不存在");
         }

         if (!server.getMemberNo().equals(String.valueOf(clientLoginUser.getId()))) {
            throw new CommonException("您没有权限操作该服务器", new Object[0]);
         }

         this.assertServerNotLocked(server);
         if (!this.isResourcePoolServer(server)) {
            return this.powerSelfProducedServer(server);
         }

         var9 = this.powerResourcePoolServer(server);
      } catch (Exception var13) {
         log.error("断开服务器电源失败", var13);
         return CommonResult.error(var13.getMessage());
      } finally {
         if (locked) {
            this.releaseLock(lockKey, lockValue);
         }
      }

      return var9;
   }

   @Override
   public CommonResult thumbnailServer(MemberServerIdParam serverIdParam) {
      SaBaseClientLoginUser clientLoginUser = StpClientLoginUserUtil.getClientLoginUser();

      try {
         MemberServer server = this.queryEntity(serverIdParam.getId());
         if (null == server) {
            return CommonResult.error("服务器不存在");
         } else if (!server.getMemberNo().equals(String.valueOf(clientLoginUser.getId()))) {
            throw new CommonException("您没有权限操作该服务器", new Object[0]);
         } else if (this.isResourcePoolServer(server)) {
            return this.serverApi.thumbnailCloudServer(server.getId());
         } else {
            BaseMemberMaster memberMaster = this.memberMasterApi.selectByMasterId(server.getMasterId());
            CloudControllerAdapter adapter = this.cloudControllerRouter.getAdapterByGuardVersion(memberMaster.getGuardVersion());
            log.info("宿主机memberMaster，参数：{}", JSON.toJSONString(memberMaster));
            if (null == memberMaster) {
               return CommonResult.error("宿主机不存在");
            } else {
               HostParam hostParam = new HostParam();
               hostParam.setHostName(server.getHostName());
               hostParam.setServerId(server.getId());
               hostParam.setMasterIp(this.getMasterIpForTask(memberMaster));
               hostParam.setApiKey(memberMaster.getApiPassword());
               hostParam.setApiPort(memberMaster.getApiPort());
               CommonResult result = adapter.thumbnailHost(hostParam);
               log.info("获取主机运行图片结果：{}", JSON.toJSONString(result));
               if (result.getCode().equals(200)) {
                  if (result.getData() instanceof Map<?, ?> dataMap) {
                     Object image = dataMap.get("image");
                     if (image != null && StrUtil.isNotBlank(String.valueOf(image))) {
                        return CommonResult.data(String.valueOf(image));
                     }

                     Object url = dataMap.get("url");
                     if (url != null && StrUtil.isNotBlank(String.valueOf(url))) {
                        return CommonResult.data(String.valueOf(url));
                     }
                  }

                  return result;
               } else {
                  return CommonResult.error(result.getMsg());
               }
            }
         }
      } catch (Exception var12) {
         log.error("服务器获取主机运行图片失败", var12);
         return CommonResult.error(var12.getMessage());
      }
   }

   @Override
   public CommonResult syncStatusServer(MemberServerIdParam serverIdParam) {
      SaBaseClientLoginUser clientLoginUser = StpClientLoginUserUtil.getClientLoginUser();

      try {
         MemberServer server = this.queryEntity(serverIdParam.getId());
         if (null == server) {
            return CommonResult.error("服务器不存在");
         } else if (!server.getMemberNo().equals(String.valueOf(clientLoginUser.getId()))) {
            throw new CommonException("您没有权限操作该服务器", new Object[0]);
         } else if (this.isResourcePoolServer(server)) {
            return this.serverApi.syncCloudServerStatus(server.getId());
         } else {
            BaseMemberMaster memberMaster = this.memberMasterApi.selectByMasterId(server.getMasterId());
            if (null == memberMaster) {
               return CommonResult.error("宿主机不存在");
            } else {
               log.info("宿主机memberMaster，参数：{}", JSON.toJSONString(memberMaster));
               CloudControllerAdapter adapter = this.cloudControllerRouter.getAdapterByGuardVersion(memberMaster.getGuardVersion());
               HostParam hostParam = new HostParam();
               hostParam.setHostName(server.getHostName());
               hostParam.setServerId(server.getId());
               hostParam.setMasterIp(this.getMasterIpForTask(memberMaster));
               hostParam.setApiKey(memberMaster.getApiPassword());
               hostParam.setApiPort(memberMaster.getApiPort());
               if ("2".equals(memberMaster.getGuardVersion())) {
                  hostParam.setMfcCloudId(server.getMfcCloudId());
                  hostParam.setMfcLoginAccount(memberMaster.getMfcLoginAccount());
                  hostParam.setMfcLoginPassword(memberMaster.getMfcLoginPassword());
               }

               CommonResult result = adapter.getStateHost(hostParam);
               log.info("获取主机运行状态结果：{}", JSON.toJSONString(result));
               if (!result.getCode().equals(200)) {
                  return CommonResult.error(result.getMsg());
               } else {
                  if ("1".equals(memberMaster.getGuardVersion()) || "2".equals(memberMaster.getGuardVersion()) || "3".equals(memberMaster.getGuardVersion())) {
                     Object data = result.getData();
                     if (data instanceof Map) {
                        String state = (String)((Map)data).get("state");
                        if (StrUtil.isNotBlank(state)) {
                           String serverStatus;
                           if ("Running".equalsIgnoreCase(state)) {
                              serverStatus = ServerStatusEnum.RUNNING.getCode();
                           } else if ("Stopped".equalsIgnoreCase(state)) {
                              serverStatus = ServerStatusEnum.STOP.getCode();
                           } else {
                              serverStatus = ServerStatusEnum.UNKNOWN.getCode();
                           }

                           server.setServerStatus(serverStatus);
                           this.updateById(server);
                           log.info(
                              "同步云服务器状态成功(宿主机版本{})，serverId={}, state={}, 已更新数据库 serverStatus={}",
                              new Object[]{memberMaster.getGuardVersion(), server.getId(), state, serverStatus}
                           );
                        }
                     }
                  }

                  return result;
               }
            }
         }
      } catch (Exception var11) {
         log.error("服务器同步主机运行状态失败", var11);
         return CommonResult.error(var11.getMessage());
      }
   }

   @Override
   public CommonResult disconnectServer(MemberServerIdParam serverIdParam) {
      try {
         SaBaseClientLoginUser clientLoginUser = StpClientLoginUserUtil.getClientLoginUser();
         MemberServer server = this.queryEntity(serverIdParam.getId());
         if (null == server) {
            return CommonResult.error("服务器不存在");
         } else if (!server.getMemberNo().equals(String.valueOf(clientLoginUser.getId()))) {
            throw new CommonException("您没有权限操作该服务器", new Object[0]);
         } else {
            return this.isResourcePoolServer(server) ? this.disconnectResourcePoolServer(server) : this.disconnectSelfProducedServer(server);
         }
      } catch (Exception var4) {
         log.error("服务器断网失败", var4);
         return CommonResult.error(var4.getMessage());
      }
   }

   @Override
   public CommonResult reconnectServer(MemberServerIdParam serverIdParam) {
      String lockKey = "server:operation_lock:" + serverIdParam.getId();
      String lockValue = UUID.randomUUID().toString();
      long expireTime = 300L;
      boolean locked = false;

      CommonResult var9;
      try {
         if (!this.tryLock(lockKey, lockValue, expireTime)) {
            return CommonResult.error("服务器正在执行其他操作，请稍后再试");
         }

         locked = true;
         SaBaseClientLoginUser clientLoginUser = StpClientLoginUserUtil.getClientLoginUser();
         MemberServer server = this.queryEntity(serverIdParam.getId());
         if (null == server) {
            return CommonResult.error("服务器不存在");
         }

         if (!server.getMemberNo().equals(String.valueOf(clientLoginUser.getId()))) {
            throw new CommonException("您没有权限操作该服务器", new Object[0]);
         }

         if (!this.isResourcePoolServer(server)) {
            return this.reconnectSelfProducedServer(server);
         }

         var9 = this.reconnectResourcePoolServer(server);
      } catch (Exception var13) {
         log.error("服务器恢复网络失败", var13);
         return CommonResult.error(var13.getMessage());
      } finally {
         if (locked) {
            this.releaseLock(lockKey, lockValue);
         }
      }

      return var9;
   }

   private CommonResult reinstallResourcePoolServer(MemberServer server, ReinstallEditParam reinstallEditParam, BaseMemberSystemVersion memberOsVersion) {
      BasePoolReinstallParam p = new BasePoolReinstallParam();
      p.setId(server.getId());
      p.setHostName(server.getHostName());
      p.setSystemClassId(reinstallEditParam.getOsClassId());
      p.setSystemId(reinstallEditParam.getOsVersionId());
      p.setSystemName(memberOsVersion.getOsName());
      p.setSystemPassword(reinstallEditParam.getOsPassword());
      p.setRemotePort(reinstallEditParam.getRemotePort());
      p.setFormatDataDisk(reinstallEditParam.getFormatDataDisk());
      CommonResult reinstallResult = this.serverApi.reinstallCloudServer(p);
      if (reinstallResult != null && Integer.valueOf(200).equals(reinstallResult.getCode())) {
         server.setServerStatus(ServerStatusEnum.REINSTALLING.getCode());
         this.updateById(server);
      }

      return reinstallResult;
   }

   private CommonResult reinstallSelfProducedServer(MemberServer server, ReinstallEditParam reinstallEditParam, BaseMemberSystemVersion memberOsVersion) {
      BaseMemberMaster memberMaster = this.memberMasterApi.selectByMasterId(server.getMasterId());
      if (null == memberMaster) {
         return CommonResult.error("宿主机不存在");
      } else {
         server.setImageDirectoryName(memberOsVersion.getImageDirectoryName());
         server.setSystemClassId(reinstallEditParam.getOsClassId());
         server.setSystemClass(memberOsVersion.getOsClassName());
         server.setSystemName(memberOsVersion.getOsName());
         server.setSystemUserName(memberOsVersion.getDefaultUserName());
         int reinstallPort = !"2".equals(memberMaster.getGuardVersion()) && !"3".equals(memberMaster.getGuardVersion())
            ? memberOsVersion.getDefaultPort()
            : this.resolveReinstallPort(reinstallEditParam.getRemotePort(), memberOsVersion.getDefaultPort());
         server.setPort(reinstallPort);
         server.setSystemPassword(reinstallEditParam.getOsPassword());
         List<MemberServerIp> serverIps = this.memberServerIpService.queryByServerId(server.getId());
         List<HostIp> hostIps = this.buildAttachPublicIps(serverIps);
         MemberServerIp primaryPublicIp = this.resolvePrimaryPublicServerIp(server, memberMaster, serverIps);
         MemberServerIp privateServerIp = this.resolvePrivateServerIp(serverIps);
         boolean sharedIp = this.isSharedIp(server, memberMaster);
         if (primaryPublicIp != null && StrUtil.isNotBlank(primaryPublicIp.getIp())) {
            primaryPublicIp.getIp();
         } else {
            server.getPublicIp();
         }

         HostParam hostParam = new HostParam();
         BeanUtils.copyProperties(server, hostParam);
         hostParam.setHostName(server.getHostName());
         hostParam.setServerId(server.getId());
         hostParam.setMasterIp(this.getMasterIpForTask(memberMaster));
         hostParam.setApiKey(memberMaster.getApiPassword());
         hostParam.setApiPort(memberMaster.getApiPort());
         hostParam.setImagePath(memberMaster.getMirrorPath());
         hostParam.setImageDirectoryName(memberOsVersion.getImageDirectoryName());
         hostParam.setOsClass(memberOsVersion.getOsClassName());
         hostParam.setSystemPassword(reinstallEditParam.getOsPassword());
         hostParam.setMfcSystemId(memberOsVersion.getMfcSystemId());
         hostParam.setPort(reinstallPort);
         if ("2".equals(memberMaster.getGuardVersion()) || "3".equals(memberMaster.getGuardVersion())) {
            hostParam.setFormatDataDisk(this.normalizeFormatDataDisk(reinstallEditParam.getFormatDataDisk()));
         }

         hostParam.setOsPath(memberMaster.getOsPath());
         hostParam.setDisksPath(memberMaster.getDisksPath());
         if (!sharedIp) {
            hostParam.setPublicIp(primaryPublicIp != null && StrUtil.isNotBlank(primaryPublicIp.getIp()) ? primaryPublicIp.getIp() : server.getPublicIp());
            hostParam.setPublicGateway(
               primaryPublicIp != null && StrUtil.isNotBlank(primaryPublicIp.getGateway()) ? primaryPublicIp.getGateway() : server.getPublicGateway()
            );
            hostParam.setPublicNetmask(
               primaryPublicIp != null && StrUtil.isNotBlank(primaryPublicIp.getMask()) ? primaryPublicIp.getMask() : server.getPublicNetmask()
            );
            hostParam.setPublicMac(server.getPublicMac());
            hostParam.setPublicVlanId(primaryPublicIp != null ? primaryPublicIp.getVlanId() : null);
            hostParam.setPublicSwitchName(memberMaster.getPublicSwitchName());
         } else {
            hostParam.setNatIp(primaryPublicIp != null && StrUtil.isNotBlank(primaryPublicIp.getIp()) ? primaryPublicIp.getIp() : server.getPublicIp());
            hostParam.setNatGateway(
               primaryPublicIp != null && StrUtil.isNotBlank(primaryPublicIp.getGateway()) ? primaryPublicIp.getGateway() : server.getPublicGateway()
            );
            hostParam.setNatNetmask(
               primaryPublicIp != null && StrUtil.isNotBlank(primaryPublicIp.getMask()) ? primaryPublicIp.getMask() : server.getPublicNetmask()
            );
            hostParam.setNatMac(server.getPublicMac());
            hostParam.setNatVlanId(primaryPublicIp != null ? primaryPublicIp.getVlanId() : null);
            hostParam.setNatSwitchName(memberMaster.getNatSwitchName());
         }

         hostParam.setPrivateSwitchName(memberMaster.getPrivateSwitchName());
         hostParam.setPrivateIp(privateServerIp != null && StrUtil.isNotBlank(privateServerIp.getIp()) ? privateServerIp.getIp() : server.getPrivateIp());
         hostParam.setPrivateGateway(
            privateServerIp != null && StrUtil.isNotBlank(privateServerIp.getGateway()) ? privateServerIp.getGateway() : server.getPrivateGateway()
         );
         hostParam.setPrivateNetmask(
            privateServerIp != null && StrUtil.isNotBlank(privateServerIp.getMask()) ? privateServerIp.getMask() : server.getPrivateNetmask()
         );
         hostParam.setPrivateMac(server.getPrivateMac());
         hostParam.setPrivateVlanId(privateServerIp != null ? privateServerIp.getVlanId() : server.getPrivateVlanId());
         hostParam.setDisksMaxIops(memberMaster.getDisksMaxiops());
         hostParam.setOsMaxIops(memberMaster.getOsMaxiops());
         hostParam.setAttachIps(hostIps);
         log.info("开始重装服务器，参数：{}", hostParam);
         BaseAutoTask taskParam = new BaseAutoTask();
         taskParam.setMasterId(memberMaster.getId());
         taskParam.setServerId(server.getId());
         taskParam.setProductType(1);
         taskParam.setTaskType(TaskTypeEnum.REINSTALL.getCode());
         taskParam.setExecuteTime(CommonTimeFormatUtil.formatNowGmt8());
         taskParam.setTaskStatus(1);
         taskParam.setNextRetryTime(new Date());
         taskParam.setTaskDesc("重装云服务器系统");
         taskParam.setPlatform("C");
         taskParam.setParams(JSONUtil.toJsonStr(hostParam));
         taskParam.setRetryCount(0);
         taskParam.setMaxRetry(3);
         taskParam.setTimeoutTime(DateUtil.offsetHour(new Date(), 2));
         return this.createTaskAndUpdateServerStatus(taskParam, server, ServerStatusEnum.REINSTALLING.getCode(), "重装任务已创建");
      }
   }

   private int resolveReinstallPort(Integer remotePort, int defaultPort) {
      return remotePort != null && remotePort > 0 && remotePort <= 65535 ? remotePort : defaultPort;
   }

   private int normalizeFormatDataDisk(Integer formatDataDisk) {
      return Integer.valueOf(1).equals(formatDataDisk) ? 1 : 0;
   }

   private CommonResult lockResourcePoolServer(MemberServer server) {
      CommonResult lockResult = this.serverApi.lockCloudServer(server.getId(), null);
      if (lockResult != null && Integer.valueOf(200).equals(lockResult.getCode())) {
         server.setServerStatus(ServerStatusEnum.LOCKING.getCode());
         this.updateById(server);
      }

      return lockResult;
   }

   private CommonResult lockSelfProducedServer(MemberServer server) {
      BaseMemberMaster memberMaster = this.memberMasterApi.selectByMasterId(server.getMasterId());
      HostParam hostParam = new HostParam();
      hostParam.setHostName(server.getHostName());
      hostParam.setServerId(server.getId());
      hostParam.setMasterIp(this.getMasterIpForTask(memberMaster));
      hostParam.setApiKey(memberMaster.getApiPassword());
      hostParam.setApiPort(memberMaster.getApiPort());
      BaseAutoTask taskParam = new BaseAutoTask();
      taskParam.setMasterId(memberMaster.getId());
      taskParam.setServerId(server.getId());
      taskParam.setProductType(1);
      taskParam.setTaskType(TaskTypeEnum.LOCK.getCode());
      taskParam.setExecuteTime(CommonTimeFormatUtil.formatNowGmt8());
      taskParam.setTaskStatus(1);
      taskParam.setNextRetryTime(new Date());
      taskParam.setTaskDesc("锁定云服务器任务");
      taskParam.setPlatform("C");
      taskParam.setParams(JSONUtil.toJsonStr(hostParam));
      taskParam.setRetryCount(0);
      taskParam.setMaxRetry(3);
      taskParam.setTimeoutTime(DateUtil.offsetHour(new Date(), 2));
      return this.createTaskAndUpdateServerStatus(taskParam, server, ServerStatusEnum.LOCKING.getCode(), "锁定任务已创建");
   }

   private CommonResult unlockResourcePoolServer(MemberServer server) {
      CommonResult unlockResult = this.serverApi.unlockCloudServer(server.getId());
      if (unlockResult != null && Integer.valueOf(200).equals(unlockResult.getCode())) {
         server.setServerStatus(ServerStatusEnum.UNLOCKING.getCode());
         this.updateById(server);
      }

      return unlockResult;
   }

   private CommonResult unlockSelfProducedServer(MemberServer server) {
      BaseMemberMaster memberMaster = this.memberMasterApi.selectByMasterId(server.getMasterId());
      HostParam hostParam = new HostParam();
      hostParam.setHostName(server.getHostName());
      hostParam.setServerId(server.getId());
      hostParam.setMasterIp(this.getMasterIpForTask(memberMaster));
      hostParam.setApiKey(memberMaster.getApiPassword());
      hostParam.setApiPort(memberMaster.getApiPort());
      BaseAutoTask taskParam = new BaseAutoTask();
      taskParam.setMasterId(memberMaster.getId());
      taskParam.setServerId(server.getId());
      taskParam.setProductType(1);
      taskParam.setTaskType(TaskTypeEnum.UNLOCK.getCode());
      taskParam.setExecuteTime(CommonTimeFormatUtil.formatNowGmt8());
      taskParam.setTaskStatus(1);
      taskParam.setNextRetryTime(new Date());
      taskParam.setTaskDesc("解锁云服务器任务");
      taskParam.setPlatform("C");
      taskParam.setParams(JSONUtil.toJsonStr(hostParam));
      taskParam.setRetryCount(0);
      taskParam.setMaxRetry(3);
      taskParam.setTimeoutTime(DateUtil.offsetHour(new Date(), 2));
      return this.createTaskAndUpdateServerStatus(taskParam, server, ServerStatusEnum.UNLOCKING.getCode(), "解锁任务已创建");
   }

   private CommonResult rebuildResourcePoolServer(MemberServer server, Integer fullRebuild) {
      CommonResult rebuildResult = this.serverApi.rebuildCloudServer(server.getId(), fullRebuild);
      if (rebuildResult != null && Integer.valueOf(200).equals(rebuildResult.getCode())) {
         server.setServerStatus(ServerStatusEnum.REBUILDING.getCode());
         this.updateById(server);
      }

      return rebuildResult;
   }

   private CommonResult rebuildSelfProducedServer(MemberServer server, Integer fullRebuild) {
      BaseMemberMaster memberMaster = this.memberMasterApi.selectByMasterId(server.getMasterId());
      log.info("宿主机memberMaster，参数：{}", JSON.toJSONString(memberMaster));
      if (null == memberMaster) {
         return CommonResult.error("宿主机不存在");
      } else {
         log.info("开始重建服务器，参数：{}", server);
         HostParam hostParam = new HostParam();
         BeanUtils.copyProperties(server, hostParam);
         hostParam.setHostName(server.getHostName());
         hostParam.setServerId(server.getId());
         hostParam.setMasterIp(this.getMasterIpForTask(memberMaster));
         hostParam.setApiKey(memberMaster.getApiPassword());
         hostParam.setApiPort(memberMaster.getApiPort());
         hostParam.setImagePath(memberMaster.getMirrorPath());
         hostParam.setImageDirectoryName(server.getImageDirectoryName());
         hostParam.setOsClass(server.getSystemClass());
         hostParam.setSystemPassword(server.getSystemPassword());
         hostParam.setOsPath(memberMaster.getOsPath());
         hostParam.setOsDisk(server.getSystemDisk());
         hostParam.setHardDisks(server.getDataDisks().get(0));
         hostParam.setDisksPath(memberMaster.getDisksPath());
         hostParam.setPublicSwitchName(memberMaster.getPublicSwitchName());
         hostParam.setPrivateSwitchName(memberMaster.getPrivateSwitchName());
         hostParam.setDisksMaxIops(memberMaster.getDisksMaxiops());
         hostParam.setOsMaxIops(memberMaster.getOsMaxiops());
         hostParam.setRebuildFlag(1);
         hostParam.setFullRebuild(fullRebuild);
         List<MemberServerIp> serverIps = this.memberServerIpService.queryByServerId(server.getId());
         List<HostIp> hostIps = this.buildAttachPublicIps(serverIps);
         MemberServerIp primaryPublicIp = this.resolvePrimaryPublicServerIp(server, memberMaster, serverIps);
         MemberServerIp privateServerIp = this.resolvePrivateServerIp(serverIps);
         boolean sharedIp = this.isSharedIp(server, memberMaster);
         if (primaryPublicIp != null && StrUtil.isNotBlank(primaryPublicIp.getIp())) {
            primaryPublicIp.getIp();
         } else {
            server.getPublicIp();
         }

         if (!sharedIp) {
            hostParam.setPublicIp(primaryPublicIp != null && StrUtil.isNotBlank(primaryPublicIp.getIp()) ? primaryPublicIp.getIp() : server.getPublicIp());
            hostParam.setPublicGateway(
               primaryPublicIp != null && StrUtil.isNotBlank(primaryPublicIp.getGateway()) ? primaryPublicIp.getGateway() : server.getPublicGateway()
            );
            hostParam.setPublicNetmask(
               primaryPublicIp != null && StrUtil.isNotBlank(primaryPublicIp.getMask()) ? primaryPublicIp.getMask() : server.getPublicNetmask()
            );
            hostParam.setPublicMac(server.getPublicMac());
            hostParam.setPublicVlanId(primaryPublicIp != null ? primaryPublicIp.getVlanId() : null);
         } else {
            hostParam.setNatIp(primaryPublicIp != null && StrUtil.isNotBlank(primaryPublicIp.getIp()) ? primaryPublicIp.getIp() : server.getPublicIp());
            hostParam.setNatGateway(
               primaryPublicIp != null && StrUtil.isNotBlank(primaryPublicIp.getGateway()) ? primaryPublicIp.getGateway() : server.getPublicGateway()
            );
            hostParam.setNatNetmask(
               primaryPublicIp != null && StrUtil.isNotBlank(primaryPublicIp.getMask()) ? primaryPublicIp.getMask() : server.getPublicNetmask()
            );
            hostParam.setNatMac(server.getPublicMac());
            hostParam.setNatVlanId(primaryPublicIp != null ? primaryPublicIp.getVlanId() : null);
            hostParam.setNatSwitchName(memberMaster.getNatSwitchName());
         }

         hostParam.setPrivateIp(privateServerIp != null && StrUtil.isNotBlank(privateServerIp.getIp()) ? privateServerIp.getIp() : server.getPrivateIp());
         hostParam.setPrivateGateway(
            privateServerIp != null && StrUtil.isNotBlank(privateServerIp.getGateway()) ? privateServerIp.getGateway() : server.getPrivateGateway()
         );
         hostParam.setPrivateNetmask(
            privateServerIp != null && StrUtil.isNotBlank(privateServerIp.getMask()) ? privateServerIp.getMask() : server.getPrivateNetmask()
         );
         hostParam.setPrivateMac(server.getPrivateMac());
         hostParam.setPrivateVlanId(privateServerIp != null ? privateServerIp.getVlanId() : server.getPrivateVlanId());
         hostParam.setShareIp(server.getShareIp());
         hostParam.setAttachIps(hostIps);
         log.info("重建服务器请求参数：{}", JSON.toJSONString(hostParam));
         BaseAutoTask taskParam = new BaseAutoTask();
         taskParam.setMasterId(memberMaster.getId());
         taskParam.setServerId(server.getId());
         taskParam.setProductType(1);
         taskParam.setTaskType(TaskTypeEnum.REBUILD.getCode());
         taskParam.setExecuteTime(CommonTimeFormatUtil.formatNowGmt8());
         taskParam.setTaskStatus(1);
         taskParam.setNextRetryTime(new Date());
         taskParam.setTaskDesc("重建云服务器");
         taskParam.setPlatform("C");
         taskParam.setParams(JSONUtil.toJsonStr(hostParam));
         taskParam.setRetryCount(0);
         taskParam.setMaxRetry(3);
         taskParam.setTimeoutTime(DateUtil.offsetHour(new Date(), 2));
         return this.createTaskAndUpdateServerStatus(taskParam, server, ServerStatusEnum.REBUILDING.getCode(), "重建任务已创建");
      }
   }

   private CommonResult updatePasswordResourcePoolServer(MemberServer server, PasswordEditParam passwordEditParam) {
      BasePoolPasswordParam p = new BasePoolPasswordParam();
      p.setId(server.getId());
      p.setHostName(server.getHostName());
      p.setOsPassword(passwordEditParam.getOsPassword());
      CommonResult passwordResult = this.serverApi.updateCloudServerPassword(p);
      if (passwordResult != null && Integer.valueOf(200).equals(passwordResult.getCode())) {
         server.setServerStatus(ServerStatusEnum.REBOOTING.getCode());
         server.setSystemPassword(passwordEditParam.getOsPassword());
         this.updateById(server);
      }

      return passwordResult;
   }

   private CommonResult updatePasswordSelfProducedServer(MemberServer server, PasswordEditParam passwordEditParam) {
      BaseMemberMaster memberMaster = this.memberMasterApi.selectByMasterId(server.getMasterId());
      log.info("宿主机memberMaster，参数：{}", JSON.toJSONString(memberMaster));
      if (null == memberMaster) {
         return CommonResult.error("宿主机不存在");
      } else {
         server.setSystemPassword(passwordEditParam.getOsPassword());
         HostParam hostParam = new HostParam();
         hostParam.setHostName(server.getHostName());
         hostParam.setServerId(server.getId());
         hostParam.setMasterIp(this.getMasterIpForTask(memberMaster));
         hostParam.setApiKey(memberMaster.getApiPassword());
         hostParam.setApiPort(memberMaster.getApiPort());
         hostParam.setSystemPassword(passwordEditParam.getOsPassword());
         BaseAutoTask taskParam = new BaseAutoTask();
         taskParam.setMasterId(memberMaster.getId());
         taskParam.setServerId(server.getId());
         taskParam.setProductType(1);
         taskParam.setTaskType(TaskTypeEnum.UPDATE_PASSWORD.getCode());
         taskParam.setExecuteTime(CommonTimeFormatUtil.formatNowGmt8());
         taskParam.setTaskStatus(1);
         taskParam.setNextRetryTime(new Date());
         taskParam.setTaskDesc("修改云服务器密码");
         taskParam.setPlatform("C");
         taskParam.setParams(JSONUtil.toJsonStr(hostParam));
         taskParam.setRetryCount(0);
         taskParam.setMaxRetry(3);
         taskParam.setTimeoutTime(DateUtil.offsetHour(new Date(), 2));
         return this.createTaskAndUpdateServerStatus(taskParam, server, ServerStatusEnum.REBOOTING.getCode(), "密码修改任务已创建");
      }
   }

   private CommonResult restartResourcePoolServer(MemberServer server) {
      CommonResult restartResult = this.serverApi.restartCloudServer(server.getId());
      if (restartResult != null && Integer.valueOf(200).equals(restartResult.getCode())) {
         server.setServerStatus(ServerStatusEnum.REBOOTING.getCode());
         this.updateById(server);
      }

      return restartResult;
   }

   private CommonResult restartSelfProducedServer(MemberServer server) {
      BaseMemberMaster memberMaster = this.memberMasterApi.selectByMasterId(server.getMasterId());
      log.info("宿主机memberMaster，参数：{}", JSON.toJSONString(memberMaster));
      if (null == memberMaster) {
         return CommonResult.error("宿主机不存在");
      } else {
         HostParam hostParam = new HostParam();
         hostParam.setHostName(server.getHostName());
         hostParam.setServerId(server.getId());
         hostParam.setMasterIp(this.getMasterIpForTask(memberMaster));
         hostParam.setApiKey(memberMaster.getApiPassword());
         hostParam.setApiPort(memberMaster.getApiPort());
         BaseAutoTask taskParam = new BaseAutoTask();
         taskParam.setMasterId(memberMaster.getId());
         taskParam.setServerId(server.getId());
         taskParam.setProductType(1);
         taskParam.setTaskType(TaskTypeEnum.RESTART.getCode());
         taskParam.setExecuteTime(CommonTimeFormatUtil.formatNowGmt8());
         taskParam.setTaskStatus(1);
         taskParam.setNextRetryTime(new Date());
         taskParam.setTaskDesc("重启云服务器任务");
         taskParam.setPlatform("C");
         taskParam.setParams(JSONUtil.toJsonStr(hostParam));
         taskParam.setRetryCount(0);
         taskParam.setMaxRetry(3);
         taskParam.setTimeoutTime(DateUtil.offsetHour(new Date(), 2));
         return this.createTaskAndUpdateServerStatus(taskParam, server, ServerStatusEnum.REBOOTING.getCode(), "重启任务已创建");
      }
   }

   private CommonResult powerResourcePoolServer(MemberServer server) {
      CommonResult powerResult = this.serverApi.powerCloudServer(server.getId());
      if (powerResult != null && Integer.valueOf(200).equals(powerResult.getCode())) {
         server.setServerStatus(ServerStatusEnum.POWERING.getCode());
         this.updateById(server);
      }

      return powerResult;
   }

   private CommonResult powerSelfProducedServer(MemberServer server) {
      BaseMemberMaster memberMaster = this.memberMasterApi.selectByMasterId(server.getMasterId());
      log.info("宿主机memberMaster，参数：{}", JSON.toJSONString(memberMaster));
      if (null == memberMaster) {
         return CommonResult.error("宿主机不存在");
      } else {
         HostParam hostParam = new HostParam();
         hostParam.setHostName(server.getHostName());
         hostParam.setServerId(server.getId());
         hostParam.setMasterIp(this.getMasterIpForTask(memberMaster));
         hostParam.setApiKey(memberMaster.getApiPassword());
         hostParam.setApiPort(memberMaster.getApiPort());
         BaseAutoTask taskParam = new BaseAutoTask();
         taskParam.setMasterId(memberMaster.getId());
         taskParam.setServerId(server.getId());
         taskParam.setProductType(1);
         taskParam.setTaskType(TaskTypeEnum.POWER.getCode());
         taskParam.setExecuteTime(CommonTimeFormatUtil.formatNowGmt8());
         taskParam.setTaskStatus(1);
         taskParam.setNextRetryTime(new Date());
         taskParam.setTaskDesc("断开云服务器电源任务");
         taskParam.setPlatform("C");
         taskParam.setParams(JSONUtil.toJsonStr(hostParam));
         taskParam.setRetryCount(0);
         taskParam.setMaxRetry(3);
         taskParam.setTimeoutTime(DateUtil.offsetHour(new Date(), 2));
         return this.createTaskAndUpdateServerStatus(taskParam, server, ServerStatusEnum.POWERING.getCode(), "断开电源任务已创建");
      }
   }

   private CommonResult disconnectResourcePoolServer(MemberServer server) {
      CommonResult disconnectResult = this.serverApi.disconnectCloudServerNetwork(server.getId());
      if (disconnectResult != null && Integer.valueOf(200).equals(disconnectResult.getCode())) {
         server.setServerStatus(ServerStatusEnum.DISCONNECTING.getCode());
         this.updateById(server);
      }

      return disconnectResult;
   }

   private CommonResult disconnectSelfProducedServer(MemberServer server) {
      BaseMemberMaster memberMaster = this.memberMasterApi.selectByMasterId(server.getMasterId());
      log.info("宿主机memberMaster，参数：{}", JSON.toJSONString(memberMaster));
      if (null == memberMaster) {
         return CommonResult.error("宿主机不存在");
      } else {
         HostParam hostParam = new HostParam();
         hostParam.setHostName(server.getHostName());
         hostParam.setServerId(server.getId());
         hostParam.setMasterIp(this.getMasterIpForTask(memberMaster));
         hostParam.setApiKey(memberMaster.getApiPassword());
         hostParam.setApiPort(memberMaster.getApiPort());
         hostParam.setDisconnect(2);
         BaseAutoTask taskParam = new BaseAutoTask();
         taskParam.setMasterId(memberMaster.getId());
         taskParam.setServerId(server.getId());
         taskParam.setProductType(1);
         taskParam.setTaskType(TaskTypeEnum.DISCONNECT.getCode());
         taskParam.setExecuteTime(CommonTimeFormatUtil.formatNowGmt8());
         taskParam.setTaskStatus(1);
         taskParam.setNextRetryTime(new Date());
         taskParam.setTaskDesc("断开云服务器网络任务");
         taskParam.setPlatform("C");
         taskParam.setParams(JSONUtil.toJsonStr(hostParam));
         taskParam.setRetryCount(0);
         taskParam.setMaxRetry(3);
         taskParam.setTimeoutTime(DateUtil.offsetHour(new Date(), 2));
         return this.createTaskAndUpdateServerStatus(taskParam, server, ServerStatusEnum.DISCONNECTING.getCode(), "断网任务已创建");
      }
   }

   private CommonResult reconnectResourcePoolServer(MemberServer server) {
      CommonResult reconnectResult = this.serverApi.reconnectCloudServerNetwork(server.getId());
      if (reconnectResult != null && Integer.valueOf(200).equals(reconnectResult.getCode())) {
         server.setServerStatus(ServerStatusEnum.CONNECTING.getCode());
         this.updateById(server);
      }

      return reconnectResult;
   }

   private CommonResult reconnectSelfProducedServer(MemberServer server) {
      BaseMemberMaster memberMaster = this.memberMasterApi.selectByMasterId(server.getMasterId());
      log.info("宿主机memberMaster，参数：{}", JSON.toJSONString(memberMaster));
      if (null == memberMaster) {
         return CommonResult.error("宿主机不存在");
      } else {
         HostParam hostParam = new HostParam();
         hostParam.setHostName(server.getHostName());
         hostParam.setServerId(server.getId());
         hostParam.setMasterIp(this.getMasterIpForTask(memberMaster));
         hostParam.setApiKey(memberMaster.getApiPassword());
         hostParam.setApiPort(memberMaster.getApiPort());
         hostParam.setDisconnect(1);
         BaseAutoTask taskParam = new BaseAutoTask();
         taskParam.setMasterId(memberMaster.getId());
         taskParam.setServerId(server.getId());
         taskParam.setProductType(1);
         taskParam.setTaskType(TaskTypeEnum.RECONNECT.getCode());
         taskParam.setExecuteTime(CommonTimeFormatUtil.formatNowGmt8());
         taskParam.setTaskStatus(1);
         taskParam.setNextRetryTime(new Date());
         taskParam.setTaskDesc("恢复云服务器网络任务");
         taskParam.setPlatform("C");
         taskParam.setParams(JSONUtil.toJsonStr(hostParam));
         taskParam.setRetryCount(0);
         taskParam.setMaxRetry(3);
         taskParam.setTimeoutTime(DateUtil.offsetHour(new Date(), 2));
         return this.createTaskAndUpdateServerStatus(taskParam, server, ServerStatusEnum.CONNECTING.getCode(), "恢复网络任务已创建");
      }
   }

   @Override
   public CommonResult vncUrl(MemberServerIdParam serverIdParam) {
      SaBaseClientLoginUser clientLoginUser = StpClientLoginUserUtil.getClientLoginUser();

      try {
         MemberServer server = this.queryEntity(serverIdParam.getId());
         if (null == server) {
            return CommonResult.error("服务器不存在");
         } else if (!server.getMemberNo().equals(String.valueOf(clientLoginUser.getId()))) {
            throw new CommonException("您没有权限操作该服务器", new Object[0]);
         } else if (this.isResourcePoolServer(server)) {
            return this.serverApi.vncCloudServer(server.getId());
         } else {
            BaseMemberMaster memberMaster = this.memberMasterApi.selectByMasterId(server.getMasterId());
            CloudControllerAdapter adapter = this.cloudControllerRouter.getAdapterByGuardVersion(memberMaster.getGuardVersion());
            log.info("宿主机memberMaster，参数：{}", JSON.toJSONString(memberMaster));
            if (null == memberMaster) {
               return CommonResult.error("宿主机不存在");
            } else {
               HostParam hostParam = new HostParam();
               hostParam.setHostName(server.getHostName());
               hostParam.setServerId(server.getId());
               hostParam.setMasterIp(this.getMasterIpForTask(memberMaster));
               hostParam.setApiKey(memberMaster.getApiPassword());
               hostParam.setApiPort(memberMaster.getApiPort());
               hostParam.setDisconnect(1);
               if ("2".equals(memberMaster.getGuardVersion())) {
                  hostParam.setMfcCloudId(server.getMfcCloudId());
                  hostParam.setMfcLoginAccount(memberMaster.getMfcLoginAccount());
                  hostParam.setMfcLoginPassword(memberMaster.getMfcLoginPassword());
               }

               CommonResult result = adapter.guidHost(hostParam);
               log.info("vncUrl结果：{}", JSON.toJSONString(result));
               return result.getCode().equals(200) ? result : CommonResult.error(result.getMsg());
            }
         }
      } catch (Exception var8) {
         log.error("获取vncUrl失败", var8);
         return CommonResult.error(var8.getMessage());
      }
   }

   @Override
   public CommonResult<MemberServerBackupRecord> createBackup(MemberServerBackupCreateParam memberServerBackupCreateParam) {
      if (memberServerBackupCreateParam != null && !StrUtil.isBlank(memberServerBackupCreateParam.getServerId())) {
         String lockValue = UUID.randomUUID().toString();
         String lockKey = "server:operation_lock:" + memberServerBackupCreateParam.getServerId();
         boolean locked = false;

         CommonResult duplicate;
         try {
            if (!this.tryLock(lockKey, lockValue, 300L)) {
               return CommonResult.error("服务器正在执行其他操作，请稍后再试");
            }

            locked = true;
            SaBaseClientLoginUser clientLoginUser = StpClientLoginUserUtil.getClientLoginUser();
            if (StrUtil.isBlank(memberServerBackupCreateParam.getBackupName())) {
               return CommonResult.error("备份名称不能为空");
            }

            MemberServer server = this.queryEntity(memberServerBackupCreateParam.getServerId());
            if (server == null) {
               return CommonResult.error("服务器不存在");
            }

            if (!server.getMemberNo().equals(String.valueOf(clientLoginUser.getId()))) {
               throw new CommonException("您没有权限操作该服务器", new Object[0]);
            }

            if (!this.isResourcePoolServer(server)) {
               BaseMemberMaster memberMaster = this.memberMasterApi.selectByMasterId(server.getMasterId());
               if (memberMaster == null) {
                  return CommonResult.error("宿主机不存在");
               }

               if (StrUtil.isBlank(memberMaster.getBackupPath())) {
                  return CommonResult.error("宿主机未配置备份目录");
               }

               Long duplicatex = this.memberServerBackupRecordMapper
                  .selectCount((Wrapper<MemberServerBackupRecord>)new LambdaQueryWrapper<MemberServerBackupRecord>().eq(MemberServerBackupRecord::getServerId, server.getId()));
               if (duplicatex != null && duplicatex > 0L) {
                  return CommonResult.error("该云服务器已存在备份，每台云服务器仅允许一个备份");
               }

               CloudControllerAdapter adapter = this.cloudControllerRouter.getAdapterByGuardVersion(memberMaster.getGuardVersion());
               HostBackupParam hostBackupParam = this.buildHostBackupParam(server, memberMaster, memberServerBackupCreateParam.getBackupName());
               CommonResult result = adapter.createBackupHost(hostBackupParam);
               if (!Integer.valueOf(200).equals(result.getCode())) {
                  return result;
               }

               MemberServerBackupRecord record = new MemberServerBackupRecord();
               record.setId(SnowflakeIdGenerator.generateId());
               record.setServerId(server.getId());
               record.setMemberNo(server.getMemberNo());
               record.setBackupName(memberServerBackupCreateParam.getBackupName());
               record.setBackupPath(memberMaster.getBackupPath());
               record.setCreateTime(new Date());
               this.memberServerBackupRecordMapper.insert(record);
               return CommonResult.data(record);
            }

            CommonResult<?> result = this.serverApi.createBackupCloudServer(server.getId(), memberServerBackupCreateParam.getBackupName());
            if (Integer.valueOf(200).equals(result.getCode())) {
               MemberServerBackupRecord record = (MemberServerBackupRecord)this.memberServerBackupRecordMapper
                  .selectOne(
                     (Wrapper<MemberServerBackupRecord>)((LambdaQueryWrapper<MemberServerBackupRecord>)new LambdaQueryWrapper<MemberServerBackupRecord>().eq(MemberServerBackupRecord::getServerId, server.getId()))
                        .eq(MemberServerBackupRecord::getBackupName, memberServerBackupCreateParam.getBackupName())
                  );
               return CommonResult.data(record);
            }

            duplicate = CommonResult.error(result.getMsg());
         } catch (Exception var17) {
            log.error("创建云服务器备份失败", var17);
            return CommonResult.error(var17.getMessage());
         } finally {
            if (locked) {
               this.releaseLock(lockKey, lockValue);
            }
         }

         return duplicate;
      } else {
         return CommonResult.error("服务器ID不能为空");
      }
   }

   @Override
   public CommonResult restoreBackup(MemberServerBackupIdParam memberServerBackupIdParam) {
      String lockValue = UUID.randomUUID().toString();
      boolean locked = false;
      String lockKey = null;

      CommonResult adapter;
      try {
         SaBaseClientLoginUser clientLoginUser = StpClientLoginUserUtil.getClientLoginUser();
         MemberServerBackupRecord record = (MemberServerBackupRecord)this.memberServerBackupRecordMapper.selectById(memberServerBackupIdParam.getId());
         if (record == null) {
            return CommonResult.error("备份记录不存在");
         }

         MemberServer server = this.queryEntity(record.getServerId());
         if (server == null) {
            return CommonResult.error("服务器不存在");
         }

         if (!server.getMemberNo().equals(String.valueOf(clientLoginUser.getId()))) {
            throw new CommonException("您没有权限操作该服务器", new Object[0]);
         }

         lockKey = "server:operation_lock:" + server.getId();
         if (!this.tryLock(lockKey, lockValue, 300L)) {
            return CommonResult.error("服务器正在执行其他操作，请稍后再试");
         }

         locked = true;
         if (this.isResourcePoolServer(server)) {
            return this.serverApi.restoreBackupCloudServer(server.getId(), record.getBackupName());
         }

         BaseMemberMaster memberMaster = this.memberMasterApi.selectByMasterId(server.getMasterId());
         if (memberMaster == null) {
            return CommonResult.error("宿主机不存在");
         }

         if (!StrUtil.isBlank(memberMaster.getBackupPath())) {
            CloudControllerAdapter adapterx = this.cloudControllerRouter.getAdapterByGuardVersion(memberMaster.getGuardVersion());
            HostBackupParam hostBackupParam = this.buildHostBackupParam(server, memberMaster, record.getBackupName());
            return adapterx.restoreBackupHost(hostBackupParam);
         }

         adapter = CommonResult.error("宿主机未配置备份目录");
      } catch (Exception var15) {
         log.error("还原云服务器备份失败", var15);
         return CommonResult.error(var15.getMessage());
      } finally {
         if (locked && lockKey != null) {
            this.releaseLock(lockKey, lockValue);
         }
      }

      return adapter;
   }

   @Override
   public CommonResult deleteBackup(MemberServerBackupIdParam memberServerBackupIdParam) {
      String lockValue = UUID.randomUUID().toString();
      boolean locked = false;
      String lockKey = null;

      CommonResult var12;
      try {
         SaBaseClientLoginUser clientLoginUser = StpClientLoginUserUtil.getClientLoginUser();
         MemberServerBackupRecord record = (MemberServerBackupRecord)this.memberServerBackupRecordMapper.selectById(memberServerBackupIdParam.getId());
         if (record == null) {
            return CommonResult.error("备份记录不存在");
         }

         MemberServer server = this.queryEntity(record.getServerId());
         if (server == null) {
            return CommonResult.error("服务器不存在");
         }

         if (!server.getMemberNo().equals(String.valueOf(clientLoginUser.getId()))) {
            throw new CommonException("您没有权限操作该服务器", new Object[0]);
         }

         lockKey = "server:operation_lock:" + server.getId();
         if (!this.tryLock(lockKey, lockValue, 300L)) {
            return CommonResult.error("服务器正在执行其他操作，请稍后再试");
         }

         locked = true;
         if (this.isResourcePoolServer(server)) {
            return this.serverApi.deleteBackupCloudServer(server.getId(), record.getBackupName());
         }

         BaseMemberMaster memberMaster = this.memberMasterApi.selectByMasterId(server.getMasterId());
         if (memberMaster == null) {
            return CommonResult.error("宿主机不存在");
         }

         if (StrUtil.isBlank(memberMaster.getBackupPath())) {
            return CommonResult.error("宿主机未配置备份目录");
         }

         CloudControllerAdapter adapter = this.cloudControllerRouter.getAdapterByGuardVersion(memberMaster.getGuardVersion());
         HostBackupParam hostBackupParam = this.buildHostBackupParam(server, memberMaster, record.getBackupName());
         CommonResult result = adapter.removeBackupHost(hostBackupParam);
         if (Integer.valueOf(200).equals(result.getCode())) {
            this.memberServerBackupRecordMapper.deleteById(record.getId());
            return CommonResult.ok("删除备份成功");
         }

         var12 = result;
      } catch (Exception var16) {
         log.error("删除云服务器备份失败", var16);
         return CommonResult.error(var16.getMessage());
      } finally {
         if (locked && lockKey != null) {
            this.releaseLock(lockKey, lockValue);
         }
      }

      return var12;
   }

   @Override
   public CommonResult<List<MemberServerBackupRecord>> listBackup(MemberServerIdParam memberServerIdParam) {
      try {
         SaBaseClientLoginUser clientLoginUser = StpClientLoginUserUtil.getClientLoginUser();
         MemberServer server = this.queryEntity(memberServerIdParam.getId());
         if (server == null) {
            return CommonResult.error("服务器不存在");
         } else if (!server.getMemberNo().equals(String.valueOf(clientLoginUser.getId()))) {
            throw new CommonException("您没有权限操作该服务器", new Object[0]);
         } else {
            List<MemberServerBackupRecord> list = this.memberServerBackupRecordMapper
               .selectList(
                  (Wrapper<MemberServerBackupRecord>)((LambdaQueryWrapper<MemberServerBackupRecord>)new LambdaQueryWrapper<MemberServerBackupRecord>().eq(MemberServerBackupRecord::getServerId, server.getId()))
                     .orderByDesc(MemberServerBackupRecord::getCreateTime)
               );
            return CommonResult.data(list);
         }
      } catch (Exception var5) {
         log.error("查询云服务器备份列表失败", var5);
         return CommonResult.error(var5.getMessage());
      }
   }

   @Override
   public CommonResult<MemberServerSnapshotRecord> createSnapshot(MemberServerSnapshotCreateParam memberServerSnapshotCreateParam) {
      String lockValue = UUID.randomUUID().toString();
      boolean locked = false;
      String lockKey = null;

      CommonResult adapter;
      try {
         SaBaseClientLoginUser clientLoginUser = StpClientLoginUserUtil.getClientLoginUser();
         if (memberServerSnapshotCreateParam == null || StrUtil.isBlank(memberServerSnapshotCreateParam.getServerId())) {
            return CommonResult.error("服务器ID不能为空");
         }

         MemberServer server = this.queryEntity(memberServerSnapshotCreateParam.getServerId());
         if (server == null) {
            return CommonResult.error("服务器不存在");
         }

         if (!server.getMemberNo().equals(String.valueOf(clientLoginUser.getId()))) {
            throw new CommonException("您没有权限操作该服务器", new Object[0]);
         }

         lockKey = "server:operation_lock:" + server.getId();
         if (!this.tryLock(lockKey, lockValue, 300L)) {
            return CommonResult.error("服务器正在执行其他操作，请稍后再试");
         }

         locked = true;
         CommonResult limitResult = this.checkSnapshotLimit(server);
         if (!Integer.valueOf(200).equals(limitResult.getCode())) {
            return limitResult;
         }

         String snapshotName = this.generateUniqueSnapshotName(server.getHostName());
         if (!this.isResourcePoolServer(server)) {
            BaseMemberMaster memberMaster = this.memberMasterApi.selectByMasterId(server.getMasterId());
            if (memberMaster == null) {
               return CommonResult.error("宿主机不存在");
            }

            CloudControllerAdapter adapterx = this.cloudControllerRouter.getAdapterByGuardVersion(memberMaster.getGuardVersion());
            HostBackupParam hostBackupParam = this.buildHostSnapshotParam(server, memberMaster, snapshotName);
            CommonResult result = adapterx.createSnapshotHost(hostBackupParam);
            if (!Integer.valueOf(200).equals(result.getCode())) {
               return result;
            }

            MemberServerSnapshotRecord record = new MemberServerSnapshotRecord();
            record.setId(SnowflakeIdGenerator.generateId());
            record.setServerId(server.getId());
            record.setMemberNo(server.getMemberNo());
            record.setSnapshotName(snapshotName);
            record.setCreateTime(new Date());
            this.memberServerSnapshotRecordMapper.insert(record);
            return CommonResult.data(record);
         }

         CommonResult<?> result = this.serverApi.createSnapshotCloudServer(server.getId(), snapshotName);
         if (Integer.valueOf(200).equals(result.getCode())) {
            MemberServerSnapshotRecord record = (MemberServerSnapshotRecord)this.memberServerSnapshotRecordMapper
               .selectOne(
                  (Wrapper<MemberServerSnapshotRecord>)((LambdaQueryWrapper<MemberServerSnapshotRecord>)new LambdaQueryWrapper<MemberServerSnapshotRecord>().eq(MemberServerSnapshotRecord::getServerId, server.getId()))
                     .eq(MemberServerSnapshotRecord::getSnapshotName, snapshotName)
               );
            return CommonResult.data(record);
         }

         adapter = CommonResult.error(result.getMsg());
      } catch (Exception var18) {
         log.error("创建云服务器快照失败", var18);
         return CommonResult.error(var18.getMessage());
      } finally {
         if (locked && lockKey != null) {
            this.releaseLock(lockKey, lockValue);
         }
      }

      return adapter;
   }

   @Override
   public CommonResult restoreSnapshot(MemberServerSnapshotIdParam memberServerSnapshotIdParam) {
      String lockValue = UUID.randomUUID().toString();
      boolean locked = false;
      String lockKey = null;

      CommonResult adapter;
      try {
         SaBaseClientLoginUser clientLoginUser = StpClientLoginUserUtil.getClientLoginUser();
         MemberServerSnapshotRecord record = (MemberServerSnapshotRecord)this.memberServerSnapshotRecordMapper.selectById(memberServerSnapshotIdParam.getId());
         if (record == null) {
            return CommonResult.error("快照记录不存在");
         }

         MemberServer server = this.queryEntity(record.getServerId());
         if (server == null) {
            return CommonResult.error("服务器不存在");
         }

         if (!server.getMemberNo().equals(String.valueOf(clientLoginUser.getId()))) {
            throw new CommonException("您没有权限操作该服务器", new Object[0]);
         }

         lockKey = "server:operation_lock:" + server.getId();
         if (!this.tryLock(lockKey, lockValue, 300L)) {
            return CommonResult.error("服务器正在执行其他操作，请稍后再试");
         }

         locked = true;
         if (this.isResourcePoolServer(server)) {
            return this.serverApi.restoreSnapshotCloudServer(server.getId(), record.getSnapshotName());
         }

         BaseMemberMaster memberMaster = this.memberMasterApi.selectByMasterId(server.getMasterId());
         if (memberMaster != null) {
            CloudControllerAdapter adapterx = this.cloudControllerRouter.getAdapterByGuardVersion(memberMaster.getGuardVersion());
            HostBackupParam hostBackupParam = this.buildHostSnapshotParam(server, memberMaster, record.getSnapshotName());
            return adapterx.restoreSnapshotHost(hostBackupParam);
         }

         adapter = CommonResult.error("宿主机不存在");
      } catch (Exception var15) {
         log.error("还原云服务器快照失败", var15);
         return CommonResult.error(var15.getMessage());
      } finally {
         if (locked && lockKey != null) {
            this.releaseLock(lockKey, lockValue);
         }
      }

      return adapter;
   }

   @Override
   public CommonResult deleteSnapshot(MemberServerSnapshotIdParam memberServerSnapshotIdParam) {
      String lockValue = UUID.randomUUID().toString();
      boolean locked = false;
      String lockKey = null;

      CommonResult var12;
      try {
         SaBaseClientLoginUser clientLoginUser = StpClientLoginUserUtil.getClientLoginUser();
         MemberServerSnapshotRecord record = (MemberServerSnapshotRecord)this.memberServerSnapshotRecordMapper.selectById(memberServerSnapshotIdParam.getId());
         if (record == null) {
            return CommonResult.error("快照记录不存在");
         }

         MemberServer server = this.queryEntity(record.getServerId());
         if (server == null) {
            return CommonResult.error("服务器不存在");
         }

         if (!server.getMemberNo().equals(String.valueOf(clientLoginUser.getId()))) {
            throw new CommonException("您没有权限操作该服务器", new Object[0]);
         }

         lockKey = "server:operation_lock:" + server.getId();
         if (!this.tryLock(lockKey, lockValue, 300L)) {
            return CommonResult.error("服务器正在执行其他操作，请稍后再试");
         }

         locked = true;
         if (this.isResourcePoolServer(server)) {
            return this.serverApi.deleteSnapshotCloudServer(server.getId(), record.getSnapshotName());
         }

         BaseMemberMaster memberMaster = this.memberMasterApi.selectByMasterId(server.getMasterId());
         if (memberMaster == null) {
            return CommonResult.error("宿主机不存在");
         }

         CloudControllerAdapter adapter = this.cloudControllerRouter.getAdapterByGuardVersion(memberMaster.getGuardVersion());
         HostBackupParam hostBackupParam = this.buildHostSnapshotParam(server, memberMaster, record.getSnapshotName());
         CommonResult result = adapter.removeSnapshotHost(hostBackupParam);
         if (Integer.valueOf(200).equals(result.getCode())) {
            this.memberServerSnapshotRecordMapper.deleteById(record.getId());
            return CommonResult.ok("删除快照成功");
         }

         var12 = result;
      } catch (Exception var16) {
         log.error("删除云服务器快照失败", var16);
         return CommonResult.error(var16.getMessage());
      } finally {
         if (locked && lockKey != null) {
            this.releaseLock(lockKey, lockValue);
         }
      }

      return var12;
   }

   @Override
   public CommonResult<List<MemberServerSnapshotRecord>> listSnapshot(MemberServerIdParam memberServerIdParam) {
      try {
         SaBaseClientLoginUser clientLoginUser = StpClientLoginUserUtil.getClientLoginUser();
         MemberServer server = this.queryEntity(memberServerIdParam.getId());
         if (server == null) {
            return CommonResult.error("服务器不存在");
         } else if (!server.getMemberNo().equals(String.valueOf(clientLoginUser.getId()))) {
            throw new CommonException("您没有权限操作该服务器", new Object[0]);
         } else {
            List<MemberServerSnapshotRecord> list = this.memberServerSnapshotRecordMapper
               .selectList(
                  (Wrapper<MemberServerSnapshotRecord>)((LambdaQueryWrapper<MemberServerSnapshotRecord>)new LambdaQueryWrapper<MemberServerSnapshotRecord>().eq(MemberServerSnapshotRecord::getServerId, server.getId()))
                     .orderByDesc(MemberServerSnapshotRecord::getCreateTime)
               );
            return CommonResult.data(list);
         }
      } catch (Exception var5) {
         log.error("查询云服务器快照列表失败", var5);
         return CommonResult.error(var5.getMessage());
      }
   }

   private HostBackupParam buildHostBackupParam(MemberServer server, BaseMemberMaster memberMaster, String backupName) {
      HostBackupParam hostBackupParam = new HostBackupParam();
      hostBackupParam.setServerId(server.getId());
      hostBackupParam.setHostName(server.getHostName());
      hostBackupParam.setBackupName(backupName);
      hostBackupParam.setBackupPath(memberMaster.getBackupPath());
      hostBackupParam.setMasterIp(this.getMasterIpForTask(memberMaster));
      hostBackupParam.setApiKey(memberMaster.getApiPassword());
      hostBackupParam.setApiPort(memberMaster.getApiPort());
      return hostBackupParam;
   }

   private HostBackupParam buildHostSnapshotParam(MemberServer server, BaseMemberMaster memberMaster, String snapshotName) {
      HostBackupParam hostBackupParam = new HostBackupParam();
      hostBackupParam.setServerId(server.getId());
      hostBackupParam.setHostName(server.getHostName());
      hostBackupParam.setSnapshotName(snapshotName);
      hostBackupParam.setMasterIp(this.getMasterIpForTask(memberMaster));
      hostBackupParam.setApiKey(memberMaster.getApiPassword());
      hostBackupParam.setApiPort(memberMaster.getApiPort());
      return hostBackupParam;
   }

   private String generateUniqueSnapshotName(String hostName) {
      String prefix = StrUtil.blankToDefault(hostName, "snapshot");

      for (int i = 0; i < 20; i++) {
         String snapshotName = prefix + "-" + this.randomSixDigits();
         Long duplicate = this.memberServerSnapshotRecordMapper
            .selectCount((Wrapper<MemberServerSnapshotRecord>)new LambdaQueryWrapper<MemberServerSnapshotRecord>().eq(MemberServerSnapshotRecord::getSnapshotName, snapshotName));
         if (duplicate == null || duplicate == 0L) {
            return snapshotName;
         }
      }

      return prefix + "-" + System.currentTimeMillis();
   }

   private int randomSixDigits() {
      return 100000 + (int)(Math.random() * 900000.0);
   }

   private CommonResult checkSnapshotLimit(MemberServer server) {
      if (server != null && !StrUtil.isBlank(server.getLineId())) {
         BaseMemberLocalLine line = this.memberLocalLineApi.selectByLineId(server.getLineId());
         if (line == null) {
            return CommonResult.error("线路不存在");
         } else if (!Integer.valueOf(1).equals(line.getSnapPriceType())) {
            return CommonResult.error("当前线路未开启快照");
         } else {
            Integer maxSnapshotNum = line.getSnapOpenMax();
            if (maxSnapshotNum != null && maxSnapshotNum > 0) {
               Long currentCount = this.memberServerSnapshotRecordMapper
                  .selectCount((Wrapper<MemberServerSnapshotRecord>)new LambdaQueryWrapper<MemberServerSnapshotRecord>().eq(MemberServerSnapshotRecord::getServerId, server.getId()));
               return currentCount != null && currentCount >= maxSnapshotNum.intValue()
                  ? CommonResult.error("当前云服务器快照数量已达到线路最大限制：" + maxSnapshotNum)
                  : CommonResult.ok();
            } else {
               return CommonResult.error("当前线路未开放快照");
            }
         }
      } else {
         return CommonResult.error("服务器未关联线路，无法创建快照");
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public CommonResult addIp(MemberServerIpParam serverIdParam) {
      try {
         MemberServer server = this.queryEntity(serverIdParam.getId());
         if (null == server) {
            return CommonResult.error("服务器不存在");
         } else {
            this.assertServerNotLocked(server);
            if (this.isResourcePoolServer(server)) {
               return CommonResult.error("资源池云服务器不支持在线加 IP");
            } else {
               BaseMemberMaster memberMaster = this.memberMasterApi.selectByMasterId(server.getMasterId());
               log.info("宿主机memberMaster，参数：{}", JSON.toJSONString(memberMaster));
               if (null == memberMaster) {
                  return CommonResult.error("宿主机不存在");
               } else {
                  this.updateNetworkInformation(server.getId(), memberMaster.getId(), server.getLineId());
                  List<MemberServerIp> memberServerIps = this.memberServerIpService.queryByServerId(server.getId());
                  String ipStr = memberServerIps.stream().map(MemberServerIp::getIp).collect(Collectors.joining(","));
                  HostParam hostParam = new HostParam();
                  hostParam.setHostName(server.getHostName());
                  hostParam.setServerId(server.getId());
                  hostParam.setMasterIp(this.getMasterIpForTask(memberMaster));
                  hostParam.setApiKey(memberMaster.getApiPassword());
                  hostParam.setApiPort(memberMaster.getApiPort());
                  hostParam.setIpStr(ipStr);
                  BaseAutoTask taskParam = new BaseAutoTask();
                  taskParam.setMasterId(memberMaster.getId());
                  taskParam.setServerId(server.getId());
                  taskParam.setProductType(1);
                  taskParam.setTaskType(TaskTypeEnum.ADD_IP.getCode());
                  taskParam.setExecuteTime(CommonTimeFormatUtil.formatNowGmt8());
                  taskParam.setTaskStatus(1);
                  taskParam.setNextRetryTime(new Date());
                  taskParam.setTaskDesc("批量设置云服务器IP任务");
                  taskParam.setPlatform("C");
                  taskParam.setParams(JSONUtil.toJsonStr(hostParam));
                  taskParam.setRetryCount(0);
                  taskParam.setMaxRetry(3);
                  taskParam.setTimeoutTime(DateUtil.offsetHour(new Date(), 2));
                  this.autoTaskApi.add(taskParam);
                  server.setServerStatus(ServerStatusEnum.UPGRADE.getCode());
                  this.updateById(server);
                  return CommonResult.ok("批量设置IP任务已创建");
               }
            }
         }
      } catch (Exception var9) {
         log.error("批量设置IP失败", var9);
         return CommonResult.error(var9.getMessage());
      }
   }

   private void updateNetworkInformation(String serverId, String masterId, String lineId) {
      BaseMemberPublicNetwork publicNetwork = this.memberPublicNetworkApi.selectByMaster(masterId, lineId);
      log.info("创建云服务器，公网网络信息:{}", JSON.toJSONString(publicNetwork));
      if (ObjectUtil.isNull(publicNetwork)) {
         throw new CommonException("创建云服务器失败，公网网络信息不存在", new Object[0]);
      } else {
         MemberServerIpAddParam serverIpAddParam = new MemberServerIpAddParam();
         serverIpAddParam.setIp(publicNetwork.getIp());
         serverIpAddParam.setServerId(serverId);
         serverIpAddParam.setGateway(publicNetwork.getGateway());
         serverIpAddParam.setMask(publicNetwork.getMask());
         this.memberServerIpService.add(serverIpAddParam);
         this.memberPublicNetworkApi.updateStatus(publicNetwork.getId(), NetworkStatusEnum.OCCUPIED.getCode());
      }
   }

   @Override
   public CommonResult upgradeHardDisksServer(String orderNo, String serverId, Integer systemDisk, List<Integer> dataDisks, BigDecimal renewPrice) {
      try {
         MemberServer server = (MemberServer)this.getOne((Wrapper<MemberServer>)new LambdaQueryWrapper<MemberServer>().eq(MemberServer::getId, serverId));
         if (null == server) {
            return CommonResult.error("服务器不存在");
         } else {
            this.assertServerNotLocked(server);
            BaseMemberMaster memberMaster = this.memberMasterApi.selectByMasterId(server.getMasterId());
            log.info("宿主机memberMaster，参数：{}", JSON.toJSONString(memberMaster));
            if (null == memberMaster) {
               return CommonResult.error("宿主机不存在");
            } else {
               if (dataDisks != null) {
                  server.setDataDisks(dataDisks);
               }

               if (systemDisk != null) {
                  server.setSystemDisk(systemDisk);
               }

               server.setRenewPrice(renewPrice);
               HostParam hostParam = new HostParam();
               hostParam.setHostName(server.getHostName());
               hostParam.setServerId(server.getId());
               hostParam.setMasterIp(this.getMasterIpForTask(memberMaster));
               hostParam.setApiKey(memberMaster.getApiPassword());
               hostParam.setApiPort(memberMaster.getApiPort());
               if (dataDisks != null && !dataDisks.isEmpty()) {
                  hostParam.setHardDisks(dataDisks.get(0));
               }

               hostParam.setDataDisks(dataDisks);
               hostParam.setOsDisk(systemDisk);
               BaseAutoTask taskParam = new BaseAutoTask();
               taskParam.setMasterId(memberMaster.getId());
               taskParam.setServerId(server.getId());
               taskParam.setProductType(1);
               taskParam.setTaskType(TaskTypeEnum.UPGRADE_HARD_DISKS.getCode());
               taskParam.setExecuteTime(CommonTimeFormatUtil.formatNowGmt8());
               taskParam.setTaskStatus(1);
               taskParam.setNextRetryTime(new Date());
               taskParam.setTaskDesc("升级云服务器磁盘任务");
               taskParam.setPlatform("C");
               taskParam.setParams(JSONUtil.toJsonStr(hostParam));
               taskParam.setRetryCount(0);
               taskParam.setMaxRetry(3);
               taskParam.setTimeoutTime(DateUtil.offsetHour(new Date(), 2));
               return this.createTaskAndUpdateServerStatus(taskParam, server, ServerStatusEnum.UPGRADE.getCode(), "升级磁盘任务已创建");
            }
         }
      } catch (Exception var10) {
         log.error("升级服务器失败", var10);
         return CommonResult.error(var10.getMessage());
      }
   }

   @Override
   public CommonResult upgradeBandwidthServer(String orderNo, String serverId, Integer bandwidth, BigDecimal renewPrice) {
      try {
         MemberServer server = (MemberServer)this.getOne((Wrapper<MemberServer>)new LambdaQueryWrapper<MemberServer>().eq(MemberServer::getId, serverId));
         if (null == server) {
            return CommonResult.error("服务器不存在");
         } else {
            this.assertServerNotLocked(server);
            BaseMemberMaster memberMaster = this.memberMasterApi.selectByMasterId(server.getMasterId());
            log.info("宿主机memberMaster，参数：{}", JSON.toJSONString(memberMaster));
            if (null == memberMaster) {
               return CommonResult.error("宿主机不存在");
            } else {
               server.setBandwidth(bandwidth);
               server.setRenewPrice(renewPrice);
               HostParam hostParam = new HostParam();
               hostParam.setHostName(server.getHostName());
               hostParam.setServerId(server.getId());
               hostParam.setMasterIp(this.getMasterIpForTask(memberMaster));
               hostParam.setApiKey(memberMaster.getApiPassword());
               hostParam.setApiPort(memberMaster.getApiPort());
               hostParam.setBandwidth(bandwidth);
               BaseAutoTask taskParam = new BaseAutoTask();
               taskParam.setMasterId(server.getMasterId());
               taskParam.setServerId(server.getId());
               taskParam.setProductType(1);
               taskParam.setTaskType(TaskTypeEnum.UPGRADE_BANDWIDTH.getCode());
               taskParam.setExecuteTime(CommonTimeFormatUtil.formatNowGmt8());
               taskParam.setTaskStatus(1);
               taskParam.setNextRetryTime(new Date());
               taskParam.setTaskDesc("升级云服务器带宽任务");
               taskParam.setPlatform("C");
               taskParam.setParams(JSONUtil.toJsonStr(hostParam));
               taskParam.setRetryCount(0);
               taskParam.setMaxRetry(3);
               taskParam.setTimeoutTime(DateUtil.offsetHour(new Date(), 2));
               return this.createTaskAndUpdateServerStatus(taskParam, server, ServerStatusEnum.UPGRADE.getCode(), "升级带宽任务已创建");
            }
         }
      } catch (Exception var9) {
         log.error("升级服务器带宽失败", var9);
         return CommonResult.error(var9.getMessage());
      }
   }

   @Override
   public CommonResult upgradeCpuRamServer(String orderNo, String serverId, Integer cpu, Integer ram, BigDecimal renewPrice) {
      try {
         MemberServer server = (MemberServer)this.getOne((Wrapper<MemberServer>)new LambdaQueryWrapper<MemberServer>().eq(MemberServer::getId, serverId));
         if (null == server) {
            return CommonResult.error("服务器不存在");
         } else {
            this.assertServerNotLocked(server);
            BaseMemberMaster memberMaster = this.memberMasterApi.selectByMasterId(server.getMasterId());
            log.info("宿主机memberMaster，参数：{}", JSON.toJSONString(memberMaster));
            if (null == memberMaster) {
               return CommonResult.error("宿主机不存在");
            } else {
               server.setCpu(cpu);
               server.setRam(ram);
               server.setRenewPrice(renewPrice);
               HostParam hostParam = new HostParam();
               hostParam.setHostName(server.getHostName());
               hostParam.setServerId(server.getId());
               hostParam.setMasterIp(this.getMasterIpForTask(memberMaster));
               hostParam.setApiKey(memberMaster.getApiPassword());
               hostParam.setApiPort(memberMaster.getApiPort());
               hostParam.setCpu(cpu);
               hostParam.setRam(ram);
               hostParam.setCpuLimit(memberMaster.getCpuLimit());
               hostParam.setCpuReservePercentage(memberMaster.getCpuReservePercentage());
               hostParam.setCpuRelativeWeight(memberMaster.getCpuRelativeWeight());
               hostParam.setMemoryDynamic(memberMaster.getMemoryDynamic());
               hostParam.setRamStart(memberMaster.getRamStart());
               hostParam.setMemoryBufferPercentage(memberMaster.getMemoryBufferPercentage());
               hostParam.setMemoryPriority(memberMaster.getMemoryPriority());
               BaseAutoTask taskParam = new BaseAutoTask();
               taskParam.setMasterId(server.getMasterId());
               taskParam.setServerId(server.getId());
               taskParam.setProductType(1);
               taskParam.setTaskType(TaskTypeEnum.UPGRADE_CPU_RAM.getCode());
               taskParam.setExecuteTime(CommonTimeFormatUtil.formatNowGmt8());
               taskParam.setTaskStatus(1);
               taskParam.setNextRetryTime(new Date());
               taskParam.setTaskDesc("升级云服务器核心任务");
               taskParam.setPlatform("C");
               taskParam.setParams(JSONUtil.toJsonStr(hostParam));
               taskParam.setRetryCount(0);
               taskParam.setMaxRetry(3);
               taskParam.setTimeoutTime(DateUtil.offsetHour(new Date(), 2));
               return this.createTaskAndUpdateServerStatus(taskParam, server, ServerStatusEnum.UPGRADE.getCode(), "升级CPU和内存任务已创建");
            }
         }
      } catch (Exception var10) {
         log.error("升级服务器核心失败", var10);
         return CommonResult.error(var10.getMessage());
      }
   }

   @Override
   public CommonResult renewServer(String orderNo, String serverId, Integer months) {
      try {
         MemberServer server = (MemberServer)this.getOne((Wrapper<MemberServer>)new LambdaQueryWrapper<MemberServer>().eq(MemberServer::getId, serverId));
         if (server == null) {
            return CommonResult.error("服务器不存在");
         } else {
            this.assertServerNotLocked(server);
            server.setEndTime(DateUtil.offsetMonth(server.getEndTime(), months));
            DevConfigApi devConfigApi = (DevConfigApi)SpringUtil.getBean(DevConfigApi.class);
            String cloudServerRetentionPeriod = devConfigApi.getValueByKey("CLOUD_SERVER_RETENTION_PERIOD");
            log.info("云服务器保留天数：{}", cloudServerRetentionPeriod);
            server.setDeleteTime(DateUtil.offsetDay(server.getEndTime(), Integer.valueOf(cloudServerRetentionPeriod)));
            boolean needStartAfterRenew = this.isExpiredAndStopped(server);
            if (BusinessStatusEnum.EXPIRED.getCode().equals(server.getBusinessStatus())) {
               server.setBusinessStatus(BusinessStatusEnum.NORMAL.getCode());
            }

            this.updateById(server);
            if (needStartAfterRenew) {
               BaseMemberMaster memberMaster = this.memberMasterApi.selectByMasterId(server.getMasterId());
               if (memberMaster == null) {
                  log.warn("续费已更新到期时间，宿主机不存在，跳过开机任务，orderNo={}，serverId={}", orderNo, serverId);
                  return CommonResult.ok();
               }

               log.info("服务器已过期且已停止，自动启动服务器");
               HostParam hostParam = new HostParam();
               hostParam.setHostName(server.getHostName());
               hostParam.setServerId(server.getId());
               hostParam.setMasterIp(this.getMasterIpForTask(memberMaster));
               hostParam.setApiKey(memberMaster.getApiPassword());
               hostParam.setApiPort(memberMaster.getApiPort());
               BaseAutoTask taskParam = new BaseAutoTask();
               taskParam.setMasterId(server.getMasterId());
               taskParam.setServerId(server.getId());
               taskParam.setProductType(1);
               taskParam.setTaskType(TaskTypeEnum.RENEW.getCode());
               taskParam.setExecuteTime(CommonTimeFormatUtil.formatNowGmt8());
               taskParam.setTaskStatus(1);
               taskParam.setNextRetryTime(new Date());
               taskParam.setTaskDesc("续费后启动云服务器任务");
               taskParam.setPlatform("C");
               taskParam.setParams(JSONUtil.toJsonStr(hostParam));
               taskParam.setRetryCount(0);
               taskParam.setMaxRetry(3);
               taskParam.setTimeoutTime(DateUtil.offsetHour(new Date(), 2));
               this.autoTaskApi.add(taskParam);
            }

            return CommonResult.ok();
         }
      } catch (CommonException var11) {
         log.error("服务器续费失败: {}", var11.getMessage(), var11);
         return CommonResult.error(var11.getMessage());
      } catch (Exception var12) {
         log.error("服务器续费失败", var12);
         return CommonResult.error("系统错误");
      }
   }

   private boolean isExpiredAndStopped(MemberServer server) {
      return BusinessStatusEnum.EXPIRED.getCode().equals(server.getBusinessStatus()) && ServerStatusEnum.STOP.getCode().equals(server.getServerStatus());
   }

   private void assertServerNotLocked(MemberServer server) {
      if (server != null && ServerStatusEnum.LOCKED.getCode().equals(server.getServerStatus())) {
         throw new CommonException("云服务器已锁定，请先解锁后再操作", new Object[0]);
      }
   }

   private void updateServerStatus(MemberServer server, String serverId) {
      server.setBusinessStatus(BusinessStatusEnum.NORMAL.getCode());
      MemberServerIdParam memberServerIdParam = new MemberServerIdParam();
      memberServerIdParam.setId(serverId);
      this.renewServer(memberServerIdParam);
   }

   @Override
   public CommonResult renewServer(MemberServerIdParam memberServerIdParam) {
      try {
         MemberServer memberServer = (MemberServer)this.getOne((Wrapper<MemberServer>)new LambdaQueryWrapper<MemberServer>().eq(MemberServer::getId, memberServerIdParam.getId()));
         if (memberServer.getServerStatus().equals(ServerStatusEnum.CREATING.getCode())) {
            throw new CommonException("服务器创建中，请等待......", new Object[0]);
         } else if (memberServer.getServerStatus().equals(ServerStatusEnum.LOCKED.getCode())) {
            throw new CommonException("服务器已锁定，请联系客服", new Object[0]);
         } else if (memberServer.getServerStatus().equals(ServerStatusEnum.REINSTALLING.getCode())) {
            throw new CommonException("服务器重装系统中，请等待......", new Object[0]);
         } else {
            BaseMemberMaster memberMaster = this.memberMasterApi.selectByLineId(memberServer.getLineId());
            CloudControllerAdapter adapter = this.cloudControllerRouter.getAdapterByGuardVersion(memberMaster.getGuardVersion());
            HostParam hostParam = new HostParam();
            hostParam.setHostName(memberServer.getHostName());
            hostParam.setServerId(memberServer.getId());
            hostParam.setMasterIp(this.getMasterIpForTask(memberMaster));
            hostParam.setApiKey(memberMaster.getApiPassword());
            hostParam.setApiPort(memberMaster.getApiPort());
            log.info("开机请求:{}", JSON.toJSONString(hostParam));
            CommonResult result = adapter.startHost(hostParam);
            log.info("开机结果:{}", JSON.toJSONString(result));
            if (result.getCode().equals(200) && !"3".equals(memberMaster.getGuardVersion())) {
               ((UpdateChainWrapper)((UpdateChainWrapper)this.update().eq("id", memberServer.getId())).set("server_status", ServerStatusEnum.RUNNING.getCode()))
                  .update();
            }

            return result;
         }
      } catch (Exception var7) {
         log.error("启动服务器失败", var7);
         return CommonResult.error(var7.getMessage());
      }
   }

   @Override
   public CommonResult plus(MemberServerTransferRecordAddParam memberServerTransferRecordAddParam) {
      memberServerTransferRecordAddParam.setProductType(ProductTypeEnum.CLOUD_SERVER.getCode());
      return this.memberServerTransferRecordService.add(memberServerTransferRecordAddParam);
   }

   @Override
   public CommonResult updateStatus(MemberServerTransferRecordIdParam memberServerTransferRecordEditParam) {
      return this.memberServerTransferRecordService.updateStatus(memberServerTransferRecordEditParam);
   }

   @Override
   public CommonResult upgradeDefenseServer(String orderNo, String serverId, Integer defense, BigDecimal renewPrice) {
      String lockKey = "server:operation_lock:" + serverId;
      String lockValue = UUID.randomUUID().toString();
      long expireTime = 300L;
      boolean locked = false;

      CommonResult hostParam;
      try {
         if (!this.tryLock(lockKey, lockValue, expireTime)) {
            return CommonResult.error("服务器正在执行其他操作，请稍后再试");
         }

         locked = true;
         MemberServer server = (MemberServer)this.getOne((Wrapper<MemberServer>)new LambdaQueryWrapper<MemberServer>().eq(MemberServer::getId, serverId));
         if (null == server) {
            return CommonResult.error("服务器不存在");
         }

         this.assertServerNotLocked(server);
         BaseMemberMaster memberMaster = this.memberMasterApi.selectByMasterId(server.getMasterId());
         log.info("宿主机memberMaster，参数：{}", JSON.toJSONString(memberMaster));
         if (null != memberMaster) {
            server.setDefense(defense);
            server.setRenewPrice(renewPrice);
            HostParam hostParamx = new HostParam();
            hostParamx.setHostName(server.getHostName());
            hostParamx.setServerId(server.getId());
            hostParamx.setMasterIp(this.getMasterIpForTask(memberMaster));
            hostParamx.setApiKey(memberMaster.getApiPassword());
            hostParamx.setApiPort(memberMaster.getApiPort());
            hostParamx.setDefense(defense);
            BaseAutoTask taskParam = new BaseAutoTask();
            taskParam.setMasterId(server.getMasterId());
            taskParam.setServerId(server.getId());
            taskParam.setProductType(1);
            taskParam.setTaskType(TaskTypeEnum.UPGRADE_DEFENSE.getCode());
            taskParam.setExecuteTime(CommonTimeFormatUtil.formatNowGmt8());
            taskParam.setTaskStatus(1);
            taskParam.setNextRetryTime(new Date());
            taskParam.setTaskDesc("升级云服务器防御任务");
            taskParam.setPlatform("C");
            taskParam.setParams(JSONUtil.toJsonStr(hostParamx));
            taskParam.setRetryCount(0);
            taskParam.setMaxRetry(3);
            taskParam.setTimeoutTime(DateUtil.offsetHour(new Date(), 2));
            return this.createTaskAndUpdateServerStatus(taskParam, server, ServerStatusEnum.UPGRADE.getCode(), "升级防御任务已创建");
         }

         hostParam = CommonResult.error("宿主机不存在");
      } catch (Exception var18) {
         log.error("升级服务器防御失败", var18);
         return CommonResult.error(var18.getMessage());
      } finally {
         if (locked) {
            this.releaseLock(lockKey, lockValue);
         }
      }

      return hostParam;
   }

   @Override
   public void isRenew(MemberServerRenewAutoParam memberServerRenewAutoParam) {
      if (memberServerRenewAutoParam != null && !StrUtil.isBlank(memberServerRenewAutoParam.getId()) && memberServerRenewAutoParam.getRenewAuto() != null) {
         if (memberServerRenewAutoParam.getRenewAuto() != 0 && memberServerRenewAutoParam.getRenewAuto() != 1) {
            throw new CommonException("自动续费开关只能为0或1", new Object[0]);
         } else {
            SaBaseClientLoginUser clientLoginUser = StpClientLoginUserUtil.getClientLoginUser();
            MemberServer server = (MemberServer)this.getOne((Wrapper<MemberServer>)new LambdaQueryWrapper<MemberServer>().eq(MemberServer::getId, memberServerRenewAutoParam.getId()));
            if (null == server) {
               throw new CommonException("服务器不存在", new Object[0]);
            } else if (StrUtil.isBlank(server.getMemberNo()) || !server.getMemberNo().equals(String.valueOf(clientLoginUser.getId()))) {
               throw new CommonException("无权操作该服务器", new Object[0]);
            } else if (BusinessStatusEnum.DELETE.getCode().equals(server.getBusinessStatus())) {
               throw new CommonException("已删除服务器不支持修改自动续费", new Object[0]);
            } else if (Integer.valueOf(1).equals(memberServerRenewAutoParam.getRenewAuto())
               && BusinessStatusEnum.EXPIRED.getCode().equals(server.getBusinessStatus())) {
               throw new CommonException("服务器已过期，无法开启自动续费", new Object[0]);
            } else {
               UpdateWrapper<MemberServer> updateWrapper = new UpdateWrapper();
               ((UpdateWrapper)updateWrapper.eq("id", memberServerRenewAutoParam.getId())).set("renew_auto", memberServerRenewAutoParam.getRenewAuto());
               this.update(updateWrapper);
            }
         }
      } else {
         throw new CommonException("参数不能为空", new Object[0]);
      }
   }

   @Override
   public CommonResult<List<MemberServerNetworkHistoryResult>> getNetworkHistory(MemberServerIdParam memberServerIdParam) {
      try {
         SaBaseClientLoginUser clientLoginUser = StpClientLoginUserUtil.getClientLoginUser();
         MemberServer memberServer = this.queryEntity(memberServerIdParam.getId());
         if (!memberServer.getMemberNo().equals(String.valueOf(clientLoginUser.getId()))) {
            throw new CommonException("您没有权限操作该服务器", new Object[0]);
         } else {
            CommonResult result;
            if (this.isResourcePoolServer(memberServer)) {
               result = this.serverApi.networkHistoryCloudServer(memberServer.getId());
            } else {
               BaseMemberMaster memberMaster = this.memberMasterApi.selectByMasterId(memberServer.getMasterId());
               CloudControllerAdapter adapter = this.cloudControllerRouter.getAdapterByGuardVersion(memberMaster.getGuardVersion());
               log.info(
                  "网络历史数据路由: serverId={}, masterId={}, guardVersion={}, adapter={}, hostName={}",
                  new Object[]{
                     memberServer.getId(), memberServer.getMasterId(), memberMaster.getGuardVersion(), adapter.getClass().getName(), memberServer.getHostName()
                  }
               );
               HostParam hostParam = new HostParam();
               hostParam.setHostName(memberServer.getHostName());
               hostParam.setServerId(memberServer.getId());
               hostParam.setMasterIp(this.getMasterIpForTask(memberMaster));
               hostParam.setApiKey(memberMaster.getApiPassword());
               hostParam.setApiPort(memberMaster.getApiPort());
               result = adapter.getNetworkHistory(hostParam);
            }

            if (!result.getCode().equals(200)) {
               return CommonResult.error(result.getMsg());
            } else {
               Object data = result.getData();
               if (data == null) {
                  log.warn("网络历史数据为空，serverId={}", memberServer.getId());
                  return CommonResult.data(new ArrayList());
               } else {
                  List<MemberServerNetworkHistoryResult> historyList = new ArrayList<>();

                  try {
                     String jsonString = JSON.toJSONString(data);
                     log.debug("网络历史数据原始返回: {}", jsonString);
                     String trimmedJson = jsonString.trim();
                     if (trimmedJson.startsWith("[")) {
                        List<Map<String, Object>> dataList = (List<Map<String, Object>>)JSON.parseObject(jsonString, List.class);
                        if (dataList != null && !dataList.isEmpty()) {
                           for (Map<String, Object> item : dataList) {
                              MemberServerNetworkHistoryResult historyResult = this.convertToNetworkHistoryResult(item);
                              if (historyResult != null) {
                                 historyList.add(historyResult);
                              }
                           }
                        }
                     } else if (trimmedJson.startsWith("{")) {
                        Map<String, Object> singleItem = (Map<String, Object>)JSON.parseObject(jsonString, Map.class);
                        if (singleItem != null) {
                           MemberServerNetworkHistoryResult historyResult = this.convertToNetworkHistoryResult(singleItem);
                           if (historyResult != null) {
                              historyList.add(historyResult);
                           }
                        }
                     } else {
                        log.warn("网络历史数据格式未知: {}", jsonString);
                     }
                  } catch (Exception var13) {
                     log.error("解析网络历史数据失败，serverId={}", memberServer.getId(), var13);
                     if (data instanceof List) {
                        for (Object itemx : (List)data) {
                           MemberServerNetworkHistoryResult historyResult = this.convertToNetworkHistoryResult(itemx);
                           if (historyResult != null) {
                              historyList.add(historyResult);
                           }
                        }
                     } else {
                        MemberServerNetworkHistoryResult historyResult = this.convertToNetworkHistoryResult(data);
                        if (historyResult != null) {
                           historyList.add(historyResult);
                        }
                     }
                  }

                  log.info("网络历史数据转换完成，serverId={}, 记录数={}", memberServer.getId(), historyList.size());
                  return CommonResult.data(historyList);
               }
            }
         }
      } catch (Exception var14) {
         log.error("获取云服务器网络历史数据失败", var14);
         return CommonResult.error("获取网络历史数据失败: " + var14.getMessage());
      }
   }

   @Override
   public CommonResult<List<MemberServerCpuHistoryResult>> getCpuHistory(MemberServerIdParam memberServerIdParam) {
      try {
         SaBaseClientLoginUser clientLoginUser = StpClientLoginUserUtil.getClientLoginUser();
         MemberServer memberServer = this.queryEntity(memberServerIdParam.getId());
         if (!memberServer.getMemberNo().equals(String.valueOf(clientLoginUser.getId()))) {
            throw new CommonException("您没有权限操作该服务器", new Object[0]);
         } else {
            CommonResult result;
            if (this.isResourcePoolServer(memberServer)) {
               result = this.serverApi.cpuHistoryCloudServer(memberServer.getId());
            } else {
               BaseMemberMaster memberMaster = this.memberMasterApi.selectByMasterId(memberServer.getMasterId());
               CloudControllerAdapter adapter = this.cloudControllerRouter.getAdapterByGuardVersion(memberMaster.getGuardVersion());
               log.info(
                  "CPU历史数据路由: serverId={}, masterId={}, guardVersion={}, adapter={}, hostName={}",
                  new Object[]{
                     memberServer.getId(), memberServer.getMasterId(), memberMaster.getGuardVersion(), adapter.getClass().getName(), memberServer.getHostName()
                  }
               );
               HostParam hostParam = new HostParam();
               hostParam.setHostName(memberServer.getHostName());
               hostParam.setServerId(memberServer.getId());
               hostParam.setMasterIp(this.getMasterIpForTask(memberMaster));
               hostParam.setApiKey(memberMaster.getApiPassword());
               hostParam.setApiPort(memberMaster.getApiPort());
               result = adapter.getCpuHistory(hostParam);
            }

            if (!result.getCode().equals(200)) {
               return CommonResult.error(result.getMsg());
            } else {
               Object data = result.getData();
               if (data == null) {
                  log.warn("CPU历史数据为空，serverId={}", memberServer.getId());
                  return CommonResult.data(new ArrayList());
               } else {
                  List<MemberServerCpuHistoryResult> historyList = new ArrayList<>();

                  try {
                     String jsonString = JSON.toJSONString(data);
                     log.debug("CPU历史数据原始返回: {}", jsonString);
                     String trimmedJson = jsonString.trim();
                     if (trimmedJson.startsWith("[")) {
                        List<Map<String, Object>> dataList = (List<Map<String, Object>>)JSON.parseObject(jsonString, List.class);
                        if (dataList != null && !dataList.isEmpty()) {
                           for (Map<String, Object> item : dataList) {
                              MemberServerCpuHistoryResult historyResult = this.convertToCpuHistoryResult(item);
                              if (historyResult != null) {
                                 historyList.add(historyResult);
                              }
                           }
                        }
                     } else if (trimmedJson.startsWith("{")) {
                        Map<String, Object> singleItem = (Map<String, Object>)JSON.parseObject(jsonString, Map.class);
                        if (singleItem != null) {
                           MemberServerCpuHistoryResult historyResult = this.convertToCpuHistoryResult(singleItem);
                           if (historyResult != null) {
                              historyList.add(historyResult);
                           }
                        }
                     } else {
                        log.warn("CPU历史数据格式未知: {}", jsonString);
                     }
                  } catch (Exception var13) {
                     log.error("解析CPU历史数据失败，serverId={}", memberServer.getId(), var13);
                     if (data instanceof List) {
                        for (Object itemx : (List)data) {
                           MemberServerCpuHistoryResult historyResult = this.convertToCpuHistoryResult(itemx);
                           if (historyResult != null) {
                              historyList.add(historyResult);
                           }
                        }
                     } else {
                        MemberServerCpuHistoryResult historyResult = this.convertToCpuHistoryResult(data);
                        if (historyResult != null) {
                           historyList.add(historyResult);
                        }
                     }
                  }

                  log.info("CPU历史数据转换完成，serverId={}, 记录数={}", memberServer.getId(), historyList.size());
                  return CommonResult.data(historyList);
               }
            }
         }
      } catch (Exception var14) {
         log.error("获取云服务器CPU历史数据失败", var14);
         return CommonResult.error("获取CPU历史数据失败: " + var14.getMessage());
      }
   }

   private MemberServerNetworkHistoryResult convertToNetworkHistoryResult(Object item) {
      try {
         Map<String, Object> map;
         if (item instanceof Map) {
            map = (Map<String, Object>)item;
         } else {
            String jsonString = JSON.toJSONString(item);
            map = (Map<String, Object>)JSON.parseObject(jsonString, Map.class);
         }

         if (map == null) {
            log.warn("转换网络历史数据失败：数据为空");
            return null;
         } else {
            MemberServerNetworkHistoryResult result = new MemberServerNetworkHistoryResult();
            Object vmValue = map.get("vm_value");
            if (vmValue != null) {
               result.setVm_value(vmValue.toString());
            } else {
               log.warn("网络历史数据缺少vm_value字段，使用默认值");
               result.setVm_value("[0,0]");
            }

            Object optime = map.get("optime");
            if (optime != null) {
               if (optime instanceof Number) {
                  long timestamp = ((Number)optime).longValue();
                  if (timestamp > 10000000000L) {
                     result.setOptime(DateUtil.format(new Date(timestamp), "yyyy-MM-dd HH:mm:ss"));
                  } else {
                     result.setOptime(DateUtil.format(new Date(timestamp * 1000L), "yyyy-MM-dd HH:mm:ss"));
                  }
               } else {
                  result.setOptime(optime.toString());
               }
            } else {
               log.warn("网络历史数据缺少optime字段，使用当前时间");
               result.setOptime(DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
            }

            return result;
         }
      } catch (Exception var8) {
         log.error("转换网络历史数据失败", var8);
         return null;
      }
   }

   private MemberServerCpuHistoryResult convertToCpuHistoryResult(Object item) {
      try {
         Map<String, Object> map;
         if (item instanceof Map) {
            map = (Map<String, Object>)item;
         } else {
            String jsonString = JSON.toJSONString(item);
            map = (Map<String, Object>)JSON.parseObject(jsonString, Map.class);
         }

         if (map == null) {
            log.warn("转换CPU历史数据失败：数据为空");
            return null;
         } else {
            MemberServerCpuHistoryResult result = new MemberServerCpuHistoryResult();
            Object vmValue = map.get("vm_value");
            if (vmValue != null) {
               result.setVm_value(vmValue.toString());
            } else {
               log.warn("CPU历史数据缺少vm_value字段，使用默认值");
               result.setVm_value("0");
            }

            Object optime = map.get("optime");
            if (optime != null) {
               if (optime instanceof Number) {
                  long timestamp = ((Number)optime).longValue();
                  if (timestamp > 10000000000L) {
                     result.setOptime(DateUtil.format(new Date(timestamp), "yyyy-MM-dd HH:mm:ss"));
                  } else {
                     result.setOptime(DateUtil.format(new Date(timestamp * 1000L), "yyyy-MM-dd HH:mm:ss"));
                  }
               } else {
                  result.setOptime(optime.toString());
               }
            } else {
               log.warn("CPU历史数据缺少optime字段，使用当前时间");
               result.setOptime(DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
            }

            return result;
         }
      } catch (Exception var8) {
         log.error("转换CPU历史数据失败", var8);
         return null;
      }
   }

   private boolean tryLock(String lockKey, String lockValue, long expireTime) {
      return true;
   }

   private void releaseLock(String lockKey, String lockValue) {
      try {
         Object current = this.redisTemplate.opsForValue().get(lockKey);
         if (current != null && lockValue.equals(String.valueOf(current))) {
            this.redisTemplate.delete(lockKey);
         }
      } catch (Exception var4) {
         log.warn("释放分布式锁失败，lockKey={}", lockKey, var4);
      }
   }

   private CommonResult createTaskAndUpdateServerStatus(BaseAutoTask taskParam, MemberServer server, String serverStatus, String successMessage) {
      this.autoTaskApi.add(taskParam);
      server.setServerStatus(serverStatus);
      this.updateById(server);
      return CommonResult.ok(successMessage);
   }

   private String getMasterIpForTask(BaseMemberMaster memberMaster) {
      if (memberMaster == null) {
         return null;
      } else {
         String ip = memberMaster.getIp();
         if (ip != null && !ip.isEmpty()) {
            if ("2".equals(memberMaster.getGuardVersion()) && memberMaster.getMfcNodeId() != null && !memberMaster.getMfcNodeId().isEmpty()) {
               ip = ip + " | 节点: " + memberMaster.getMfcNodeId();
            }

            return ip;
         } else {
            return ip;
         }
      }
   }

   private boolean isSharedIp(MemberServer server, BaseMemberMaster memberMaster) {
      return server != null && Integer.valueOf(1).equals(server.getShareIp()) || memberMaster != null && Integer.valueOf(1).equals(memberMaster.getShareIp());
   }

   private List<HostIp> buildAttachPublicIps(List<MemberServerIp> serverIps) {
      List<HostIp> hostIps = new ArrayList<>();
      if (serverIps != null && !serverIps.isEmpty()) {
         for (MemberServerIp serverIp : serverIps) {
            if (!Integer.valueOf(1).equals(serverIp.getIsMain()) && "PUBLIC".equals(serverIp.getType()) && !StrUtil.isBlank(serverIp.getIp())) {
               HostIp hostIp = new HostIp();
               hostIp.setIp(serverIp.getIp());
               hostIp.setMask(serverIp.getMask());
               hostIps.add(hostIp);
            }
         }

         return hostIps;
      } else {
         return hostIps;
      }
   }

   private MemberServerIp resolvePrimaryPublicServerIp(MemberServer server, BaseMemberMaster memberMaster, List<MemberServerIp> serverIps) {
      if (serverIps != null && !serverIps.isEmpty()) {
         boolean sharedIp = this.isSharedIp(server, memberMaster);
         MemberServerIp primaryIp = this.findServerIp(serverIps, sharedIp ? "NAT" : "PUBLIC", true);
         if (primaryIp != null) {
            return primaryIp;
         } else {
            if (sharedIp) {
               primaryIp = this.findServerIp(serverIps, "PUBLIC", true);
               if (primaryIp != null) {
                  return primaryIp;
               }

               primaryIp = this.findServerIp(serverIps, "NAT", false);
               if (primaryIp != null) {
                  return primaryIp;
               }
            }

            return this.findServerIp(serverIps, "PUBLIC", false);
         }
      } else {
         return null;
      }
   }

   private MemberServerIp resolvePrivateServerIp(List<MemberServerIp> serverIps) {
      if (serverIps != null && !serverIps.isEmpty()) {
         MemberServerIp privateIp = this.findServerIp(serverIps, "PRIVATE", true);
         return privateIp != null ? privateIp : this.findServerIp(serverIps, "PRIVATE", false);
      } else {
         return null;
      }
   }

   private MemberServerIp findServerIp(List<MemberServerIp> serverIps, String type, boolean mainOnly) {
      for (MemberServerIp serverIp : serverIps) {
         if (type.equals(serverIp.getType()) && !StrUtil.isBlank(serverIp.getIp()) && (!mainOnly || Integer.valueOf(1).equals(serverIp.getIsMain()))) {
            return serverIp;
         }
      }

      return null;
   }
}
