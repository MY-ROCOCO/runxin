package com.rxinyun.admin.business.modular.esc.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.UpdateChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.admin.business.modular.esc.entity.Server;
import com.rxinyun.admin.business.modular.esc.entity.ServerBackupRecord;
import com.rxinyun.admin.business.modular.esc.entity.ServerIp;
import com.rxinyun.admin.business.modular.esc.entity.ServerSnapshotRecord;
import com.rxinyun.admin.business.modular.esc.mapper.ServerBackupRecordMapper;
import com.rxinyun.admin.business.modular.esc.mapper.ServerIpMapper;
import com.rxinyun.admin.business.modular.esc.mapper.ServerMapper;
import com.rxinyun.admin.business.modular.esc.mapper.ServerSnapshotRecordMapper;
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
import com.rxinyun.admin.business.modular.esc.service.HostStatsRefreshService;
import com.rxinyun.admin.business.modular.esc.service.ServerIpService;
import com.rxinyun.admin.business.modular.esc.service.ServerPortService;
import com.rxinyun.admin.business.modular.esc.service.ServerService;
import com.rxinyun.admin.business.modular.esc.service.ServerTransferRecordService;
import com.rxinyun.admin.business.modular.esc.util.ResourcePoolUpstreamClient;
import com.rxinyun.admin.business.modular.esc.util.ResourcePoolUpstreamDataApplier;
import com.rxinyun.admin.business.modular.esc.util.ResourcePoolUpstreamSyncClient;
import com.rxinyun.admin.business.modular.task.param.AutoTaskAddParam;
import com.rxinyun.admin.business.modular.task.service.AutoTaskTaskService;
import com.rxinyun.business.api.ServerApi;
import com.rxinyun.business.core.enums.TaskTypeEnum;
import com.rxinyun.business.core.pojo.BaseServer;
import com.rxinyun.common.controller.CloudControllerAdapter;
import com.rxinyun.common.controller.CloudControllerRouter;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import com.rxinyun.common.enums.business.BusinessStatusEnum;
import com.rxinyun.common.enums.business.ServerStatusEnum;
import com.rxinyun.common.enums.order.ProductTypeEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.common.pojo.CommonEntity;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.common.qzsystem.entity.HostBackupParam;
import com.rxinyun.common.qzsystem.entity.HostIp;
import com.rxinyun.common.qzsystem.entity.HostParam;
import com.rxinyun.common.qzsystem.result.HostMonitorResult;
import com.rxinyun.common.qzsystem.utils.HostOpenerUtil;
import com.rxinyun.common.util.CommonTimeFormatUtil;
import com.rxinyun.common.util.OsInstancePasswordValidator;
import com.rxinyun.common.util.SnowflakeIdGenerator;
import com.rxinyun.common.util.TimeUtil;
import com.rxinyun.dev.api.DevConfigApi;
import com.rxinyun.dev.api.ResourceChangeLogApi;
import com.rxinyun.dev.api.ResourcePoolApi;
import com.rxinyun.dev.core.pojo.BaseResourceChangeLog;
import com.rxinyun.dev.core.pojo.BaseResourcePool;
import com.rxinyun.dev.core.pojo.BaseResourcePoolRequest;
import com.rxinyun.production.admin.ServerLocalLineApi;
import com.rxinyun.production.admin.core.pojo.BaseLocalLine;
import com.rxinyun.production.api.MemberMasterApi;
import com.rxinyun.production.api.MemberSystemVersionApi;
import com.rxinyun.production.core.pojo.BaseMemberMaster;
import com.rxinyun.production.core.pojo.BaseMemberSystemVersion;
import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServerServiceImpl extends ServiceImpl<ServerMapper, Server> implements ServerService {
   private static final Logger log = LoggerFactory.getLogger(ServerServiceImpl.class);
   private static final String LOCKED_OPERATION_MESSAGE = "云服务器已锁定，请先解锁后再操作";
   @Resource
   private ServerTransferRecordService serverTransferRecordService;
   @Resource
   private ServerMapper serverMapper;
   @Resource
   private MemberMasterApi memberMasterApi;
   @Resource
   private MemberSystemVersionApi memberOsVersionApi;
   @Resource
   private ServerIpService serverIpService;
   @Resource
   private ServerIpMapper serverIpMapper;
   @Resource
   private ServerApi serverApi;
   @Resource
   private AutoTaskTaskService autoTaskTaskService;
   @Resource
   private RedisTemplate<String, Object> redisTemplate;
   @Resource
   private CloudControllerRouter cloudControllerRouter;
   @Resource
   private ResourcePoolApi resourcePoolApi;
   @Resource
   private ResourcePoolUpstreamClient resourcePoolUpstreamClient;
   @Resource
   private ResourcePoolUpstreamSyncClient resourcePoolUpstreamSyncClient;
   @Resource
   private HostStatsRefreshService hostStatsRefreshService;
   @Autowired
   private ServerLocalLineApi serverLocalLineApi;
   @Resource
   private ServerPortService serverPortService;
   @Resource
   private ServerBackupRecordMapper serverBackupRecordMapper;
   @Resource
   private ServerSnapshotRecordMapper serverSnapshotRecordMapper;
   @Resource
   private ResourceChangeLogApi resourceChangeLogApi;

   @Override
   public Page<Server> page(ServerPageParam pageParam) {
      QueryWrapper<Server> queryWrapper = new QueryWrapper().checkSqlInjection();
      if (ObjectUtil.isNotEmpty(pageParam.getBusinessStatus())) {
         queryWrapper.lambda().and(q -> q.eq(Server::getBusinessStatus, pageParam.getBusinessStatus()));
      }

      if (ObjectUtil.isNotEmpty(pageParam.getServerStatus())) {
         queryWrapper.lambda().and(q -> q.eq(Server::getServerStatus, pageParam.getServerStatus()));
      }

      if (ObjectUtil.isNotEmpty(pageParam.getMemberNo())) {
         queryWrapper.lambda().and(q -> q.eq(Server::getMemberNo, pageParam.getMemberNo()));
      }

      if (StrUtil.isNotBlank(pageParam.getMasterId())) {
         queryWrapper.lambda().and(q -> q.eq(Server::getMasterId, pageParam.getMasterId()));
      }

      if (StrUtil.isNotBlank(pageParam.getServerIp())) {
         List<String> serverIds = this.serverIpService.listServerIdByIp(pageParam.getServerIp());
         queryWrapper.lambda()
            .and(
               q -> {
                  if (ObjectUtil.isNotEmpty(serverIds)) {
                     ((LambdaQueryWrapper<Server>)q.in(Server::getId, serverIds)).or();
                  }

                  ((LambdaQueryWrapper<Server>)((LambdaQueryWrapper<Server>)q.like(Server::getPublicIp, pageParam.getServerIp())).or())
                     .like(Server::getPrivateIp, pageParam.getServerIp());
               }
            );
      }

      if (StrUtil.isNotBlank(pageParam.getServerName())) {
         queryWrapper.lambda().and(q -> q.like(Server::getHostName, pageParam.getServerName()));
      }

      if (ObjectUtil.isNotEmpty(pageParam.getSearchKey())) {
         queryWrapper.lambda()
            .and(
               q -> ((LambdaQueryWrapper<Server>)((LambdaQueryWrapper<Server>)((LambdaQueryWrapper<Server>)((LambdaQueryWrapper<Server>)((LambdaQueryWrapper<Server>)((LambdaQueryWrapper<Server>)q.like(
                                    Server::getHostName, pageParam.getSearchKey()
                                 ))
                                 .or())
                              .like(Server::getPublicIp, pageParam.getSearchKey()))
                           .or())
                        .like(Server::getMemberNo, pageParam.getSearchKey()))
                     .or())
                  .apply(
                     "EXISTS (SELECT 1 FROM server_master sm WHERE sm.id = MASTER_ID AND sm.delete_flag = 'NOT_DELETE' AND sm.ip LIKE CONCAT('%', {0}, '%'))",
                     new Object[]{pageParam.getSearchKey()}
                  )
            );
      }

      if (ObjectUtil.isAllNotEmpty(new Object[]{pageParam.getSortField(), pageParam.getSortOrder()})) {
         CommonSortOrderEnum.validate(pageParam.getSortOrder());
         queryWrapper.orderBy(true, pageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()), this.resolveServerSortField(pageParam.getSortField()));
         if (!StrUtil.equals(pageParam.getSortField(), "id")) {
            queryWrapper.lambda().orderByDesc(Server::getId);
         }
      } else {
         queryWrapper.lambda().orderByDesc(Server::getCreateTime).orderByDesc(Server::getId);
      }

      Page<Server> pageResult = (Page<Server>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
      this.fillLineAndAreaNames(pageResult.getRecords());
      if (ObjectUtil.isNotEmpty(pageResult.getRecords())) {
         for (Server server : pageResult.getRecords()) {
            if (ObjectUtil.isNotEmpty(server.getResourcePoolId())) {
               BaseResourcePool resourcePool = this.resourcePoolApi.getById(server.getResourcePoolId());
               if (ObjectUtil.isNotEmpty(resourcePool)) {
                  server.setResourcePoolType(resourcePool.getPoolType());
                  server.setResourcePoolName(resourcePool.getName());
               }
            }
         }
      }

      this.fillCurrentIpNum(pageResult.getRecords());
      this.fillMasterDisplayInfo(pageResult.getRecords());
      return pageResult;
   }

   private void fillCurrentIpNum(List<Server> servers) {
      if (servers != null && !servers.isEmpty()) {
         List<String> serverIds = servers.stream().map(Server::getId).collect(Collectors.toList());
         Map<String, Integer> ipNumMap = this.serverIpService.countPublicIpByServerIds(serverIds);

         for (Server server : servers) {
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

   private void fillCurrentIpNum(Server server) {
      if (server != null) {
         Map<String, Integer> ipNumMap = this.serverIpService.countPublicIpByServerIds(List.of(server.getId()));
         server.setIpNum(ipNumMap.getOrDefault(server.getId(), 0));
      }
   }

   private void fillMasterDisplayInfo(List<Server> servers) {
      if (servers != null && !servers.isEmpty()) {
         Set<String> masterIds = servers.stream().map(Server::getMasterId).filter(CharSequenceUtil::isNotBlank).collect(Collectors.toSet());
         if (!masterIds.isEmpty()) {
            Map<String, BaseMemberMaster> masterMap = new HashMap<>();

            for (String masterId : masterIds) {
               try {
                  BaseMemberMaster master = this.memberMasterApi.selectByMasterId(masterId);
                  if (master != null) {
                     masterMap.put(masterId, master);
                  }
               } catch (Exception var7) {
                  log.debug("查询宿主机失败 masterId={}", masterId, var7);
               }
            }

            for (Server server : servers) {
               this.applyMasterDisplayInfo(server, masterMap.get(server.getMasterId()));
            }
         }
      }
   }

   private void fillGuardVersion(Server server) {
      if (server != null && !StrUtil.isBlank(server.getMasterId())) {
         try {
            this.applyMasterDisplayInfo(server, this.memberMasterApi.selectByMasterId(server.getMasterId()));
         } catch (Exception var3) {
            log.debug("填充宿主机展示信息失败 masterId={}", server.getMasterId(), var3);
         }
      }
   }

   private void applyMasterDisplayInfo(Server server, BaseMemberMaster master) {
      if (server != null && master != null) {
         if (master.getGuardVersion() != null) {
            server.setGuardVersion(master.getGuardVersion());
         }

         server.setMasterIp(this.resolveMasterIpForDisplay(master));
         server.setMasterName(master.getMasterName());
      }
   }

   private String resolveMasterIpForDisplay(BaseMemberMaster master) {
      if (master == null) {
         return null;
      } else {
         String ip = master.getIp();
         if (StrUtil.isBlank(ip)) {
            return ip;
         } else {
            return "2".equals(master.getGuardVersion()) && StrUtil.isNotBlank(master.getMfcNodeId()) ? ip + " | 节点: " + master.getMfcNodeId() : ip;
         }
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void add(ServerAddParam serverAddParam) {
      try {
         OsInstancePasswordValidator.validateOrThrow(serverAddParam.getSystemPassword());
         BaseLocalLine line = this.serverLocalLineApi.selectByLineId(serverAddParam.getLineId());
         if (line == null) {
            throw new CommonException("该线路不存在", new Object[0]);
         } else {
            Integer ipNum = serverAddParam.getIpNum();
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
               serverAddParam.setIpNum(ipNum);
               Server server = (Server)BeanUtil.toBean(serverAddParam, Server.class);
               server.setBusinessStatus(BusinessStatusEnum.NORMAL.getCode());
               server.setServerStatus(ServerStatusEnum.CREATING.getCode());
               DevConfigApi devConfigApi = (DevConfigApi)SpringUtil.getBean(DevConfigApi.class);
               String cloudServerRetentionPeriod = devConfigApi.getValueByKey("CLOUD_SERVER_RETENTION_PERIOD");
               server.setDeleteTime(DateUtil.offsetDay(serverAddParam.getEndTime(), Integer.valueOf(cloudServerRetentionPeriod)));
               this.saveOrUpdate(server);
               if (server.getId() == null) {
                  throw new CommonException("ID 未生成", new Object[0]);
               } else {
                  serverAddParam.setId(server.getId());
                  log.info("创建云服务器开始，HOST_NAME:{}", serverAddParam.getHostName());
                  if (!this.resourcePoolUpstreamClient.isResourcePoolServer(server) && server.getMemberNo() != null) {
                     try {
                        String upstreamId = StrUtil.blankToDefault(server.getUpstreamServerId(), server.getId());
                        this.resourcePoolApi
                           .logResourceChange(
                              StrUtil.blankToDefault(server.getOrderNo(), ""), Long.valueOf(server.getMemberNo()), "CREATE", upstreamId, "CLOUD"
                           );
                     } catch (Exception var9) {
                        log.error("记录云服务器创建变更日志失败，订单号：{}", server.getOrderNo(), var9);
                     }
                  }

                  AutoTaskAddParam taskParam = new AutoTaskAddParam();
                  taskParam.setMasterId(server.getMasterId());
                  taskParam.setServerId(server.getId());
                  taskParam.setProductType(1);
                  taskParam.setTaskType(TaskTypeEnum.CREATE.getCode());
                  taskParam.setExecuteTime(CommonTimeFormatUtil.formatNowGmt8());
                  taskParam.setTaskStatus(1);
                  taskParam.setNextRetryTime(TimeUtil.toDate(TimeUtil.now()));
                  taskParam.setTaskDesc("创建云服务器任务");
                  taskParam.setPlatform("C");
                  JSONObject paramObj = new JSONObject();
                  paramObj.put("serverId", server.getId());
                  paramObj.put("ipNum", ipNum);
                  taskParam.setParams(JSON.toJSONString(paramObj));
                  taskParam.setRetryCount(0);
                  taskParam.setMaxRetry(3);
                  taskParam.setTimeoutTime(DateUtil.offsetHour(TimeUtil.toDate(TimeUtil.now()), 2));
                  this.autoTaskTaskService.add(taskParam);
               }
            }
         }
      } catch (CommonException var10) {
         log.error("创建云服务器失败: {}", var10.getMessage(), var10);
         throw var10;
      } catch (Exception var11) {
         log.error("创建云服务器时发生未知错误: {}", var11.getMessage(), var11);
         throw new CommonException("创建云服务器失败", new Object[]{var11.getMessage()});
      }
   }

   @Override
   public void edit(ServerEditParam serverEditParam) {
      Server serverAvailableRegion = this.queryEntity(serverEditParam.getId());
      BeanUtil.copyProperties(serverEditParam, serverAvailableRegion, new String[0]);
      this.updateById(serverAvailableRegion);
   }

   @Override
   public Server detail(ServerIdParam serverIdParam) {
      Server server = this.queryEntity(serverIdParam.getId());
      this.fillLineAndAreaNames(server);
      this.fillCurrentIpNum(server);
      this.fillGuardVersion(server);
      return server;
   }

   @Override
   public Server queryEntity(String id) {
      Server serverAvailableRegion = (Server)this.getById(id);
      if (ObjectUtil.isEmpty(serverAvailableRegion)) {
         throw new CommonException("云服务器不存在，id值为：{}", new Object[]{id});
      } else {
         return serverAvailableRegion;
      }
   }

   private void fillLineAndAreaNames(Server server) {
      if (server != null) {
         this.fillLineAndAreaNames(List.of(server));
      }
   }

   private void fillLineAndAreaNames(List<Server> servers) {
      if (servers != null && !servers.isEmpty()) {
         List<String> lineIds = servers.stream().map(Server::getLineId).filter(CharSequenceUtil::isNotBlank).distinct().collect(Collectors.toList());
         if (!lineIds.isEmpty()) {
            Map<String, BaseLocalLine> lineMap = this.serverLocalLineApi.selectByLineIds(lineIds);

            for (Server server : servers) {
               BaseLocalLine line = lineMap.get(server.getLineId());
               if (line != null) {
                  server.setLineName(line.getLineName());
                  server.setAreaName(line.getAvailableName());
               }
            }
         }
      }
   }

   @Override
   public Long getServerCountByCreateTime(LocalDateTime startTime, LocalDateTime endTime) {
      LambdaQueryWrapper<Server> lambdaQueryWrapper = new LambdaQueryWrapper();
      List<Integer> list = new ArrayList<>();
      list.add(BusinessStatusEnum.NORMAL.getCode());
      list.add(BusinessStatusEnum.EXPIRED.getCode());
      lambdaQueryWrapper.ge(startTime != null, Server::getCreateTime, startTime)
            .le(endTime != null, Server::getCreateTime, endTime)
            .in(Server::getBusinessStatus, list);
      return this.serverMapper.selectCount(lambdaQueryWrapper);
   }

   @Override
   public CommonResult startServer(ServerIdParam serverIdParam) {
      try {
         Server server = this.queryEntity(serverIdParam.getId());
         if (server.getServerStatus().equals(ServerStatusEnum.CREATING.getCode())) {
            throw new CommonException("服务器创建中，请等待......", new Object[0]);
         } else if (server.getServerStatus().equals(ServerStatusEnum.LOCKED.getCode())) {
            throw new CommonException("服务器已锁定，请联系客服", new Object[0]);
         } else if (server.getServerStatus().equals(ServerStatusEnum.REINSTALLING.getCode())) {
            throw new CommonException("服务器重装系统中，请等待......", new Object[0]);
         } else {
            return this.resourcePoolUpstreamClient.isResourcePoolServer(server) ? this.startResourcePoolServer(server) : this.startSelfProducedServer(server);
         }
      } catch (Exception var3) {
         log.error("启动服务器失败", var3);
         return CommonResult.error(var3.getMessage());
      }
   }

   @Override
   public CommonResult closeServer(ServerIdParam serverIdParam) {
      try {
         Server server = this.queryEntity(serverIdParam.getId());
         this.assertServerNotLocked(server);
         return this.resourcePoolUpstreamClient.isResourcePoolServer(server)
            ? this.closeResourcePoolServer(server)
            : this.closeSelfProducedServer(server, "关闭云服务器任务");
      } catch (Exception var3) {
         log.error("关闭服务器失败", var3);
         return CommonResult.error(var3.getMessage());
      }
   }

   private CommonResult startResourcePoolServer(Server server) {
      CommonResult<?> upstreamResult = this.resourcePoolUpstreamClient.callOrderServerApi(server, "start", null);
      if (upstreamResult != null && Integer.valueOf(200).equals(upstreamResult.getCode())) {
         server.setServerStatus(ServerStatusEnum.STARTING.getCode());
         this.updateById(server);
      }

      return upstreamResult;
   }

   private CommonResult startSelfProducedServer(Server server) {
      BaseMemberMaster master = this.memberMasterApi.selectByLineId(server.getLineId());
      HostParam hostParam = new HostParam();
      hostParam.setHostName(server.getHostName());
      hostParam.setServerId(server.getId());
      hostParam.setMasterIp(this.getMasterIpForTask(master));
      hostParam.setApiKey(master.getApiPassword());
      hostParam.setApiPort(master.getApiPort());
      log.info("开机请求:{}", JSON.toJSONString(hostParam));
      AutoTaskAddParam taskParam = new AutoTaskAddParam();
      taskParam.setMasterId(master.getId());
      taskParam.setServerId(server.getId());
      taskParam.setProductType(1);
      taskParam.setTaskType(TaskTypeEnum.START.getCode());
      taskParam.setExecuteTime(CommonTimeFormatUtil.formatNowGmt8());
      taskParam.setTaskStatus(1);
      taskParam.setNextRetryTime(new Date());
      taskParam.setTaskDesc("启动云服务器任务");
      taskParam.setPlatform("B");
      taskParam.setParams(JSONUtil.toJsonStr(hostParam));
      taskParam.setRetryCount(0);
      taskParam.setMaxRetry(3);
      taskParam.setTimeoutTime(DateUtil.offsetHour(new Date(), 2));
      return this.createTaskAndUpdateServerStatus(taskParam, server, ServerStatusEnum.STARTING.getCode(), "启动任务已创建", true);
   }

   private CommonResult closeResourcePoolServer(Server server) {
      CommonResult<?> upstreamResult = this.resourcePoolUpstreamClient.callOrderServerApi(server, "stop", null);
      if (upstreamResult != null && Integer.valueOf(200).equals(upstreamResult.getCode())) {
         server.setServerStatus(ServerStatusEnum.STOPPING.getCode());
         this.updateById(server);
      }

      return upstreamResult;
   }

   private CommonResult closeSelfProducedServer(Server server, String taskDesc) {
      BaseMemberMaster memberMaster = this.memberMasterApi.selectByMasterId(server.getMasterId());
      HostParam hostParam = new HostParam();
      hostParam.setHostName(server.getHostName());
      hostParam.setServerId(server.getId());
      hostParam.setMasterIp(this.getMasterIpForTask(memberMaster));
      hostParam.setApiKey(memberMaster.getApiPassword());
      hostParam.setApiPort(memberMaster.getApiPort());
      log.info("关机请求:{}", JSON.toJSONString(hostParam));
      AutoTaskAddParam taskParam = new AutoTaskAddParam();
      taskParam.setMasterId(memberMaster.getId());
      taskParam.setServerId(server.getId());
      taskParam.setProductType(1);
      taskParam.setTaskType(TaskTypeEnum.STOP.getCode());
      taskParam.setExecuteTime(CommonTimeFormatUtil.formatNowGmt8());
      taskParam.setTaskStatus(1);
      taskParam.setNextRetryTime(new Date());
      taskParam.setTaskDesc(taskDesc);
      taskParam.setPlatform("B");
      taskParam.setParams(JSONUtil.toJsonStr(hostParam));
      taskParam.setRetryCount(0);
      taskParam.setMaxRetry(3);
      taskParam.setTimeoutTime(DateUtil.offsetHour(new Date(), 2));
      return this.createTaskAndUpdateServerStatus(taskParam, server, ServerStatusEnum.STOPPING.getCode(), "关闭任务已创建");
   }

   private CommonResult lockResourcePoolServer(Server server, String lockReason) {
      JSONObject body = new JSONObject();
      body.put("lockReason", lockReason);
      CommonResult<?> upstreamResult = this.resourcePoolUpstreamClient.callOrderServerApi(server, "lock", body);
      if (upstreamResult != null && Integer.valueOf(200).equals(upstreamResult.getCode())) {
         server.setServerStatus(ServerStatusEnum.LOCKING.getCode());
         server.setLockReason(lockReason);
         this.updateById(server);
      }

      return upstreamResult;
   }

   private CommonResult lockSelfProducedServer(Server server, String lockReason) {
      BaseMemberMaster memberMaster = this.memberMasterApi.selectByMasterId(server.getMasterId());
      HostParam hostParam = new HostParam();
      hostParam.setHostName(server.getHostName());
      hostParam.setServerId(server.getId());
      hostParam.setMasterIp(this.getMasterIpForTask(memberMaster));
      hostParam.setApiKey(memberMaster.getApiPassword());
      hostParam.setApiPort(memberMaster.getApiPort());
      AutoTaskAddParam taskParam = new AutoTaskAddParam();
      taskParam.setMasterId(memberMaster.getId());
      taskParam.setServerId(server.getId());
      taskParam.setProductType(1);
      taskParam.setTaskType(TaskTypeEnum.LOCK.getCode());
      taskParam.setExecuteTime(CommonTimeFormatUtil.formatNowGmt8());
      taskParam.setTaskStatus(1);
      taskParam.setNextRetryTime(new Date());
      taskParam.setTaskDesc("锁定云服务器任务");
      taskParam.setPlatform("B");
      taskParam.setParams(JSONUtil.toJsonStr(hostParam));
      taskParam.setRetryCount(0);
      taskParam.setMaxRetry(3);
      taskParam.setTimeoutTime(DateUtil.offsetHour(new Date(), 2));
      server.setLockReason(lockReason);
      return this.createTaskAndUpdateServerStatus(taskParam, server, ServerStatusEnum.LOCKING.getCode(), "锁定任务已创建");
   }

   private CommonResult unlockResourcePoolServer(Server server) {
      CommonResult<?> upstreamResult = this.resourcePoolUpstreamClient.callOrderServerApi(server, "unlock", null);
      if (upstreamResult != null && Integer.valueOf(200).equals(upstreamResult.getCode())) {
         server.setServerStatus(ServerStatusEnum.UNLOCKING.getCode());
         this.clearServerLockReason(server.getId(), ServerStatusEnum.UNLOCKING.getCode());
      }

      return upstreamResult;
   }

   private CommonResult unlockSelfProducedServer(Server server) {
      BaseMemberMaster memberMaster = this.memberMasterApi.selectByMasterId(server.getMasterId());
      HostParam hostParam = new HostParam();
      hostParam.setHostName(server.getHostName());
      hostParam.setServerId(server.getId());
      hostParam.setMasterIp(this.getMasterIpForTask(memberMaster));
      hostParam.setApiKey(memberMaster.getApiPassword());
      hostParam.setApiPort(memberMaster.getApiPort());
      AutoTaskAddParam taskParam = new AutoTaskAddParam();
      taskParam.setMasterId(memberMaster.getId());
      taskParam.setServerId(server.getId());
      taskParam.setProductType(1);
      taskParam.setTaskType(TaskTypeEnum.UNLOCK.getCode());
      taskParam.setExecuteTime(CommonTimeFormatUtil.formatNowGmt8());
      taskParam.setTaskStatus(1);
      taskParam.setNextRetryTime(new Date());
      taskParam.setTaskDesc("解锁云服务器任务");
      taskParam.setPlatform("B");
      taskParam.setParams(JSONUtil.toJsonStr(hostParam));
      taskParam.setRetryCount(0);
      taskParam.setMaxRetry(3);
      taskParam.setTimeoutTime(DateUtil.offsetHour(new Date(), 2));
      CommonResult result = this.createTaskAndUpdateServerStatus(taskParam, server, ServerStatusEnum.UNLOCKING.getCode(), "解锁任务已创建");
      if (result != null && Integer.valueOf(200).equals(result.getCode())) {
         this.clearServerLockReason(server.getId(), ServerStatusEnum.UNLOCKING.getCode());
      }

      return result;
   }

   private CommonResult restartResourcePoolServer(Server server) {
      CommonResult<?> upstreamResult = this.resourcePoolUpstreamClient.callOrderServerApi(server, "restart", null);
      if (upstreamResult != null && Integer.valueOf(200).equals(upstreamResult.getCode())) {
         server.setServerStatus(ServerStatusEnum.REBOOTING.getCode());
         this.updateById(server);
      }

      return upstreamResult;
   }

   private CommonResult restartSelfProducedServer(Server server) {
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
         AutoTaskAddParam taskParam = new AutoTaskAddParam();
         taskParam.setMasterId(memberMaster.getId());
         taskParam.setServerId(server.getId());
         taskParam.setProductType(1);
         taskParam.setTaskType(TaskTypeEnum.RESTART.getCode());
         taskParam.setExecuteTime(CommonTimeFormatUtil.formatNowGmt8());
         taskParam.setTaskStatus(1);
         taskParam.setNextRetryTime(new Date());
         taskParam.setTaskDesc("重启云服务器任务");
         taskParam.setPlatform("B");
         taskParam.setParams(JSONUtil.toJsonStr(hostParam));
         taskParam.setRetryCount(0);
         taskParam.setMaxRetry(3);
         taskParam.setTimeoutTime(DateUtil.offsetHour(new Date(), 2));
         return this.createTaskAndUpdateServerStatus(taskParam, server, ServerStatusEnum.REBOOTING.getCode(), "重启任务已创建");
      }
   }

   private CommonResult powerResourcePoolServer(Server server) {
      CommonResult<?> upstreamResult = this.resourcePoolUpstreamClient.callOrderServerApi(server, "power", null);
      if (upstreamResult != null && Integer.valueOf(200).equals(upstreamResult.getCode())) {
         server.setServerStatus(ServerStatusEnum.POWERING.getCode());
         this.updateById(server);
      }

      return upstreamResult;
   }

   private CommonResult powerSelfProducedServer(Server server) {
      BaseMemberMaster memberMaster = this.memberMasterApi.selectByMasterId(server.getMasterId());
      log.info("宿主机memberMaster，参数：{}", JSON.toJSONString(memberMaster));
      if (null == memberMaster) {
         return CommonResult.error("宿主机不存在");
      } else {
         this.cloudControllerRouter.getAdapterByGuardVersion(memberMaster.getGuardVersion());
         HostParam hostParam = new HostParam();
         hostParam.setHostName(server.getHostName());
         hostParam.setServerId(server.getId());
         hostParam.setMasterIp(this.getMasterIpForTask(memberMaster));
         hostParam.setApiKey(memberMaster.getApiPassword());
         hostParam.setApiPort(memberMaster.getApiPort());
         AutoTaskAddParam taskParam = new AutoTaskAddParam();
         taskParam.setMasterId(memberMaster.getId());
         taskParam.setServerId(server.getId());
         taskParam.setProductType(1);
         taskParam.setTaskType(TaskTypeEnum.POWER.getCode());
         taskParam.setExecuteTime(CommonTimeFormatUtil.formatNowGmt8());
         taskParam.setTaskStatus(1);
         taskParam.setNextRetryTime(new Date());
         taskParam.setTaskDesc("断开云服务器电源任务");
         taskParam.setPlatform("B");
         taskParam.setParams(JSONUtil.toJsonStr(hostParam));
         taskParam.setRetryCount(0);
         taskParam.setMaxRetry(3);
         taskParam.setTimeoutTime(DateUtil.offsetHour(new Date(), 2));
         return this.createTaskAndUpdateServerStatus(taskParam, server, ServerStatusEnum.POWERING.getCode(), "断开电源任务已创建", true);
      }
   }

   private CommonResult disconnectResourcePoolServer(Server server) {
      CommonResult<?> upstreamResult = this.resourcePoolUpstreamClient.callOrderServerApi(server, "disconnect", null);
      if (upstreamResult != null && Integer.valueOf(200).equals(upstreamResult.getCode())) {
         server.setServerStatus(ServerStatusEnum.DISCONNECTING.getCode());
         this.updateById(server);
      }

      return upstreamResult;
   }

   private CommonResult disconnectSelfProducedServer(Server server) {
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
         AutoTaskAddParam taskParam = new AutoTaskAddParam();
         taskParam.setMasterId(memberMaster.getId());
         taskParam.setServerId(server.getId());
         taskParam.setProductType(1);
         taskParam.setTaskType(TaskTypeEnum.DISCONNECT.getCode());
         taskParam.setExecuteTime(CommonTimeFormatUtil.formatNowGmt8());
         taskParam.setTaskStatus(1);
         taskParam.setNextRetryTime(new Date());
         taskParam.setTaskDesc("断开云服务器网络任务");
         taskParam.setPlatform("B");
         taskParam.setParams(JSONUtil.toJsonStr(hostParam));
         taskParam.setRetryCount(0);
         taskParam.setMaxRetry(3);
         taskParam.setTimeoutTime(DateUtil.offsetHour(new Date(), 2));
         return this.createTaskAndUpdateServerStatus(taskParam, server, ServerStatusEnum.DISCONNECTING.getCode(), "断网任务已创建");
      }
   }

   private CommonResult reconnectResourcePoolServer(Server server) {
      CommonResult<?> upstreamResult = this.resourcePoolUpstreamClient.callOrderServerApi(server, "reconnect", null);
      if (upstreamResult != null && Integer.valueOf(200).equals(upstreamResult.getCode())) {
         server.setServerStatus(ServerStatusEnum.CONNECTING.getCode());
         this.updateById(server);
      }

      return upstreamResult;
   }

   private CommonResult reconnectSelfProducedServer(Server server) {
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
         AutoTaskAddParam taskParam = new AutoTaskAddParam();
         taskParam.setMasterId(memberMaster.getId());
         taskParam.setServerId(server.getId());
         taskParam.setProductType(1);
         taskParam.setTaskType(TaskTypeEnum.RECONNECT.getCode());
         taskParam.setExecuteTime(CommonTimeFormatUtil.formatNowGmt8());
         taskParam.setTaskStatus(1);
         taskParam.setNextRetryTime(new Date());
         taskParam.setTaskDesc("恢复云服务器网络任务");
         taskParam.setPlatform("B");
         taskParam.setParams(JSONUtil.toJsonStr(hostParam));
         taskParam.setRetryCount(0);
         taskParam.setMaxRetry(3);
         taskParam.setTimeoutTime(DateUtil.offsetHour(new Date(), 2));
         return this.createTaskAndUpdateServerStatus(taskParam, server, ServerStatusEnum.CONNECTING.getCode(), "恢复网络任务已创建");
      }
   }

   private CommonResult reinstallResourcePoolServer(Server server, ReinstallEditParam reinstallEditParam) {
      JSONObject body = new JSONObject();
      body.put("hostName", reinstallEditParam.getHostName());
      body.put("systemClassId", reinstallEditParam.getSystemClassId());
      body.put("systemId", reinstallEditParam.getSystemId());
      body.put("systemName", reinstallEditParam.getSystemName());
      body.put("systemPassword", reinstallEditParam.getSystemPassword());
      body.put("remotePort", reinstallEditParam.getRemotePort());
      body.put("formatDataDisk", reinstallEditParam.getFormatDataDisk());
      CommonResult<?> upstreamResult = this.resourcePoolUpstreamClient.callOrderServerApi(server, "reinstall", body);
      if (upstreamResult != null && Integer.valueOf(200).equals(upstreamResult.getCode())) {
         BaseMemberSystemVersion memberOsVersion = this.memberOsVersionApi.selectByOsVersionId(reinstallEditParam.getSystemId());
         server.setServerStatus(ServerStatusEnum.REINSTALLING.getCode());
         server.setSystemClassId(reinstallEditParam.getSystemClassId());
         server.setSystemId(reinstallEditParam.getSystemId());
         server.setSystemPassword(reinstallEditParam.getSystemPassword());
         if (memberOsVersion != null) {
            server.setImageDirectoryName(memberOsVersion.getImageDirectoryName());
            server.setSystemClass(memberOsVersion.getOsClassName());
            server.setSystemName(memberOsVersion.getOsName());
            server.setSystemUserName(memberOsVersion.getDefaultUserName());
            server.setPort(this.resolveReinstallPort(reinstallEditParam.getRemotePort(), memberOsVersion.getDefaultPort()));
         } else {
            server.setSystemName(reinstallEditParam.getSystemName());
         }

         this.updateById(server);
      }

      return upstreamResult;
   }

   private CommonResult reinstallSelfProducedServer(Server server, ReinstallEditParam reinstallEditParam) {
      BaseMemberMaster memberMaster = this.memberMasterApi.selectByMasterId(server.getMasterId());
      if (null == memberMaster) {
         return CommonResult.error("宿主机不存在");
      } else {
         BaseMemberSystemVersion memberOsVersion = this.memberOsVersionApi.selectByOsVersionId(reinstallEditParam.getSystemId());
         if (null == memberOsVersion) {
            return CommonResult.error("操作系统版本信息不存在");
         } else {
            List<ServerIp> serverIps = this.serverIpService.queryByServerId(server.getId());
            List<HostIp> hostIps = this.buildAttachPublicIps(serverIps);
            ServerIp primaryPublicIp = this.resolvePrimaryPublicServerIp(server, memberMaster, serverIps);
            ServerIp privateServerIp = this.resolvePrivateServerIp(serverIps);
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
            hostParam.setSystemPassword(reinstallEditParam.getSystemPassword());
            hostParam.setMfcSystemId(memberOsVersion.getMfcSystemId());
            int reinstallPort = !"2".equals(memberMaster.getGuardVersion()) && !"3".equals(memberMaster.getGuardVersion())
               ? memberOsVersion.getDefaultPort()
               : this.resolveReinstallPort(reinstallEditParam.getRemotePort(), memberOsVersion.getDefaultPort());
            hostParam.setPort(reinstallPort);
            if ("2".equals(memberMaster.getGuardVersion()) || "3".equals(memberMaster.getGuardVersion())) {
               hostParam.setFormatDataDisk(this.normalizeFormatDataDisk(reinstallEditParam.getFormatDataDisk()));
            }

            hostParam.setOsPath(memberMaster.getOsPath());
            hostParam.setDisksPath(memberMaster.getDisksPath());
            hostParam.setPrivateSwitchName(memberMaster.getPrivateSwitchName());
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
            AutoTaskAddParam taskParam = new AutoTaskAddParam();
            taskParam.setMasterId(memberMaster.getId());
            taskParam.setServerId(server.getId());
            taskParam.setProductType(1);
            taskParam.setTaskType(TaskTypeEnum.REINSTALL.getCode());
            taskParam.setExecuteTime(CommonTimeFormatUtil.formatNowGmt8());
            taskParam.setTaskStatus(1);
            taskParam.setNextRetryTime(new Date());
            taskParam.setTaskDesc("重装云服务器系统");
            taskParam.setPlatform("B");
            taskParam.setParams(JSONUtil.toJsonStr(hostParam));
            taskParam.setRetryCount(0);
            taskParam.setMaxRetry(3);
            taskParam.setTimeoutTime(DateUtil.offsetHour(new Date(), 2));
            server.setServerStatus(ServerStatusEnum.REINSTALLING.getCode());
            server.setImageDirectoryName(memberOsVersion.getImageDirectoryName());
            server.setSystemClassId(reinstallEditParam.getSystemClassId());
            server.setSystemId(reinstallEditParam.getSystemId());
            server.setSystemClass(memberOsVersion.getOsClassName());
            server.setSystemName(memberOsVersion.getOsName());
            server.setSystemUserName(memberOsVersion.getDefaultUserName());
            server.setPort(reinstallPort);
            server.setSystemPassword(reinstallEditParam.getSystemPassword());
            this.autoTaskTaskService.add(taskParam);
            this.updateById(server);
            log.info("开始重装服务器，参数：{}", server);
            return CommonResult.ok("重装任务已创建");
         }
      }
   }

   private CommonResult updatePasswordResourcePoolServer(Server server, PasswordEditParam passwordEditParam) {
      JSONObject body = new JSONObject();
      body.put("hostName", passwordEditParam.getHostName());
      body.put("osPassword", passwordEditParam.getOsPassword());
      CommonResult<?> upstreamResult = this.resourcePoolUpstreamClient.callOrderServerApi(server, "updatePassword", body);
      if (upstreamResult != null && Integer.valueOf(200).equals(upstreamResult.getCode())) {
         server.setServerStatus(ServerStatusEnum.REBOOTING.getCode());
         server.setSystemPassword(passwordEditParam.getOsPassword());
         this.updateById(server);
      }

      return upstreamResult;
   }

   private CommonResult updatePasswordSelfProducedServer(Server server, PasswordEditParam passwordEditParam) {
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
         hostParam.setSystemPassword(passwordEditParam.getOsPassword());
         AutoTaskAddParam taskParam = new AutoTaskAddParam();
         taskParam.setMasterId(memberMaster.getId());
         taskParam.setServerId(server.getId());
         taskParam.setProductType(1);
         taskParam.setTaskType(TaskTypeEnum.UPDATE_PASSWORD.getCode());
         taskParam.setExecuteTime(CommonTimeFormatUtil.formatNowGmt8());
         taskParam.setTaskStatus(1);
         taskParam.setNextRetryTime(new Date());
         taskParam.setTaskDesc("修改云服务器密码");
         taskParam.setPlatform("B");
         taskParam.setParams(JSONUtil.toJsonStr(hostParam));
         taskParam.setRetryCount(0);
         taskParam.setMaxRetry(3);
         taskParam.setTimeoutTime(DateUtil.offsetHour(new Date(), 2));
         server.setServerStatus(ServerStatusEnum.REBOOTING.getCode());
         server.setSystemPassword(passwordEditParam.getOsPassword());
         this.autoTaskTaskService.add(taskParam);
         this.updateById(server);
         return CommonResult.ok("密码修改任务已创建");
      }
   }

   private CommonResult rebuildResourcePoolServer(Server server, Integer fullRebuild) {
      JSONObject body = new JSONObject();
      if (fullRebuild != null) {
         body.put("fullRebuild", fullRebuild);
      }

      CommonResult<?> upstreamResult = this.resourcePoolUpstreamClient.callOrderServerApi(server, "rebuild", body);
      if (upstreamResult != null && Integer.valueOf(200).equals(upstreamResult.getCode())) {
         server.setServerStatus(ServerStatusEnum.REBUILDING.getCode());
         this.updateById(server);
      }

      return upstreamResult;
   }

   private CommonResult rebuildSelfProducedServer(Server server, Integer fullRebuild) {
      BaseMemberMaster memberMaster = this.memberMasterApi.selectByMasterId(server.getMasterId());
      log.info("宿主机memberMaster，参数：{}", JSON.toJSONString(memberMaster));
      if (null == memberMaster) {
         return CommonResult.error("宿主机不存在");
      } else {
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
         hostParam.setHardDisks(server.getDataDisks().get(0));
         hostParam.setSystemPassword(server.getSystemPassword());
         hostParam.setOsPath(memberMaster.getOsPath());
         hostParam.setDisksPath(memberMaster.getDisksPath());
         hostParam.setPublicSwitchName(memberMaster.getPublicSwitchName());
         hostParam.setPrivateSwitchName(memberMaster.getPrivateSwitchName());
         hostParam.setDisksMaxIops(memberMaster.getDisksMaxiops());
         hostParam.setOsMaxIops(memberMaster.getOsMaxiops());
         hostParam.setOsDisk(server.getSystemDisk());
         hostParam.setRebuildFlag(1);
         hostParam.setFullRebuild(fullRebuild);
         List<ServerIp> serverIps = this.serverIpService.queryByServerId(server.getId());
         List<HostIp> hostIps = this.buildAttachPublicIps(serverIps);
         ServerIp primaryPublicIp = this.resolvePrimaryPublicServerIp(server, memberMaster, serverIps);
         ServerIp privateServerIp = this.resolvePrivateServerIp(serverIps);
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
         if (server.getDataDisks() != null && !server.getDataDisks().isEmpty()) {
            if (server.getDataDisks().size() == 1) {
               hostParam.setHardDisks(server.getDataDisks().get(0));
            } else {
               hostParam.setData1DiskSize(server.getDataDisks().get(0));
               hostParam.setData2DiskSize(server.getDataDisks().size() >= 2 ? server.getDataDisks().get(1) : null);
               hostParam.setData3DiskSize(server.getDataDisks().size() >= 3 ? server.getDataDisks().get(2) : null);
            }
         }

         if ("2".equals(memberMaster.getGuardVersion())) {
            hostParam.setMfcNodeId(memberMaster.getMfcNodeId());
            hostParam.setMfcLoginAccount(memberMaster.getMfcLoginAccount());
            hostParam.setMfcLoginPassword(memberMaster.getMfcLoginPassword());
            hostParam.setPort(server.getPort());
            BaseMemberSystemVersion memberOsVersion = this.memberOsVersionApi.selectByOsVersionId(server.getSystemId());
            if (memberOsVersion != null && memberOsVersion.getMfcSystemId() != null) {
               hostParam.setMfcSystemId(memberOsVersion.getMfcSystemId());
            }
         }

         log.info("重建服务器请求参数：{}", JSON.toJSONString(hostParam));
         AutoTaskAddParam taskParam = new AutoTaskAddParam();
         taskParam.setMasterId(memberMaster.getId());
         taskParam.setServerId(server.getId());
         taskParam.setProductType(1);
         taskParam.setTaskType(TaskTypeEnum.REBUILD.getCode());
         taskParam.setExecuteTime(CommonTimeFormatUtil.formatNowGmt8());
         taskParam.setTaskStatus(1);
         taskParam.setNextRetryTime(new Date());
         taskParam.setTaskDesc("重建云服务器");
         taskParam.setPlatform("B");
         taskParam.setParams(JSONUtil.toJsonStr(hostParam));
         taskParam.setRetryCount(0);
         taskParam.setMaxRetry(3);
         taskParam.setTimeoutTime(DateUtil.offsetHour(new Date(), 2));
         server.setServerStatus(ServerStatusEnum.REBUILDING.getCode());
         this.autoTaskTaskService.add(taskParam);
         this.updateById(server);
         log.info("开始重建服务器，参数：{}", server);
         return CommonResult.ok("重建任务已创建");
      }
   }

   @Override
   public CommonResult deleteServer(ServerIdParam serverIdParam) {
      return this.deleteServer(serverIdParam, "删除云服务器任务");
   }

   @Override
   public CommonResult deleteServer(ServerIdParam serverIdParam, String taskDesc) {
      try {
         Server server = this.queryEntity(serverIdParam.getId());
         if (Integer.valueOf(1).equals(server.getType())) {
            log.info("资源池服务器删除，发送请求至上游，服务器ID：{}", serverIdParam.getId());
            String upstreamIdA = resolveResourcePoolUpstreamServerId(server);
            if (StrUtil.isBlank(upstreamIdA)) {
               return CommonResult.error("上游服务器ID未同步，无法通知上游，请等待资源池同步完成后再试");
            } else {
               String effectiveOrderNo = resolveResourcePoolOrderNo(server.getOrderNo(), server);
               JSONObject params = new JSONObject();
               if (StrUtil.isNotBlank(effectiveOrderNo)) {
                  params.put("orderNo", effectiveOrderNo);
               }

               putResourcePoolUpstreamServerFields(params, server, upstreamIdA);
               BaseResourcePoolRequest poolRequest = new BaseResourcePoolRequest();
               if (StrUtil.isNotBlank(effectiveOrderNo)) {
                  poolRequest.setOrderNo(effectiveOrderNo);
               }

               poolRequest.setMemberNo(server.getMemberNo() != null ? Long.valueOf(server.getMemberNo()) : null);
               poolRequest.setResourcePoolId(server.getResourcePoolId());
               poolRequest.setOperationType("DELETE");
               poolRequest.setAServerId(upstreamIdA);
               poolRequest.setProductType(ProductTypeEnum.CLOUD_SERVER.getCode());
               poolRequest.setRequestParams(JSON.toJSONString(params));
               this.resourcePoolApi.createRequest(poolRequest);
               this.serverIpMapper.deleteByServerId(server.getId());
               server.setServerStatus(ServerStatusEnum.DELETING.getCode());
               this.updateById(server);
               log.info("✅ 资源池删除请求已创建，等待定时任务发送，服务器ID：{}", serverIdParam.getId());
               return CommonResult.ok("删除任务已创建");
            }
         } else {
            BaseMemberMaster memberMaster = StrUtil.isBlank(server.getMasterId()) ? null : this.memberMasterApi.selectByMasterId(server.getMasterId());
            if (memberMaster == null) {
               return this.finalizeServerDeleteWithoutHost(server);
            } else {
               HostParam hostParam = new HostParam();
               hostParam.setHostName(server.getHostName());
               hostParam.setServerId(server.getId());
               hostParam.setMasterIp(this.getMasterIpForTask(memberMaster));
               hostParam.setApiKey(memberMaster.getApiPassword());
               hostParam.setApiPort(memberMaster.getApiPort());
               hostParam.setBackupPath(memberMaster.getBackupPath());
               AutoTaskAddParam taskParam = new AutoTaskAddParam();
               taskParam.setMasterId(memberMaster.getId());
               taskParam.setServerId(server.getId());
               taskParam.setProductType(1);
               taskParam.setTaskType(TaskTypeEnum.DELETE.getCode());
               taskParam.setExecuteTime(CommonTimeFormatUtil.formatNowGmt8());
               taskParam.setTaskStatus(1);
               taskParam.setNextRetryTime(TimeUtil.toDate(TimeUtil.now()));
               taskParam.setTaskDesc(taskDesc);
               taskParam.setPlatform("B");
               taskParam.setParams(JSONUtil.toJsonStr(hostParam));
               taskParam.setRetryCount(0);
               taskParam.setMaxRetry(3);
               taskParam.setTimeoutTime(DateUtil.offsetHour(TimeUtil.toDate(TimeUtil.now()), 2));
               this.autoTaskTaskService.add(taskParam);
               server.setServerStatus(ServerStatusEnum.DELETING.getCode());
               this.updateById(server);

               try {
                  this.resourcePoolApi
                     .logResourceChange(
                        server.getOrderNo() != null ? server.getOrderNo() : "N/A",
                        Long.valueOf(server.getMemberNo()),
                        "DELETE",
                        StrUtil.blankToDefault(server.getUpstreamServerId(), server.getId()),
                        "CLOUD"
                     );
               } catch (Exception var9) {
                  log.error("记录服务器删除变更日志失败，服务器 ID: {}", server.getId(), var9);
               }

               return CommonResult.ok("删除任务已创建");
            }
         }
      } catch (Exception var10) {
         log.error("服务器删除任务创建失败", var10);
         return CommonResult.error(var10.getMessage());
      }
   }

   private CommonResult finalizeServerDeleteWithoutHost(Server server) {
      log.warn("云服务器宿主机不存在，执行本地删除收尾，serverId={}, masterId={}, serverStatus={}", new Object[]{server.getId(), server.getMasterId(), server.getServerStatus()});
      server.setServerStatus(ServerStatusEnum.STOP.getCode());
      server.setBusinessStatus(BusinessStatusEnum.DELETE.getCode());
      this.updateById(server);
      this.releaseMacAfterDelete(server.getId());
      this.serverIpService.deleteByServerIdWithServerSnapshotFallback(server.getId());
      this.serverPortService.deleteVps(server.getId());
      this.saveResourceChangeLogAfterDeleteSuccess(server.getId());
      return CommonResult.ok("云服务器宿主机不存在，已直接删除收尾");
   }

   @Override
   public CommonResult monitorServer(ServerMonitorParam serverIdParam) {
      Server server = this.queryEntity(serverIdParam.getId());
      if (this.resourcePoolUpstreamClient.isResourcePoolServer(server)) {
         return this.resourcePoolUpstreamClient.callOrderServerApi(server, "monitor", null);
      } else {
         BaseMemberMaster memberMaster = this.memberMasterApi.selectByMasterId(server.getMasterId());
         CloudControllerAdapter adapter = this.cloudControllerRouter.getAdapterByGuardVersion(memberMaster.getGuardVersion());
         log.info(
            "监控路由: serverId={}, masterId={}, guardVersion={}, adapter={}",
            new Object[]{server.getId(), server.getMasterId(), memberMaster.getGuardVersion(), adapter.getClass().getName()}
         );
         HostParam hostParam = new HostParam();
         hostParam.setHostName(server.getHostName());
         hostParam.setServerId(server.getId());
         hostParam.setMasterIp(this.getMasterIpForTask(memberMaster));
         hostParam.setApiKey(memberMaster.getApiPassword());
         hostParam.setApiPort(memberMaster.getApiPort());
         this.fillMonitorQueryParam(hostParam, serverIdParam);
         if ("2".equals(memberMaster.getGuardVersion())) {
            hostParam.setMfcCloudId(server.getMfcCloudId());
            hostParam.setMfcLoginAccount(memberMaster.getMfcLoginAccount());
            hostParam.setMfcLoginPassword(memberMaster.getMfcLoginPassword());
         }

         CommonResult result = adapter.monitorHost(hostParam);
         if (!result.getCode().equals(200)) {
            return result;
         } else {
            Object data = result.getData();
            HostMonitorResult vo = (HostMonitorResult)JSON.parseObject(JSON.toJSONString(data), HostMonitorResult.class);
            return CommonResult.data(vo);
         }
      }
   }

   private void fillMonitorQueryParam(HostParam hostParam, ServerMonitorParam param) {
      if (hostParam != null && param != null) {
         hostParam.setDateType(param.getDateType());
         hostParam.setMinDateTime(param.getMinDateTime());
         hostParam.setMaxDateTime(param.getMaxDateTime());
         hostParam.setSort(param.getSort());
      }
   }

   @Override
   public CommonResult reinstallServer(ReinstallEditParam reinstallEditParam) {
      try {
         String pwdErr = OsInstancePasswordValidator.validate(reinstallEditParam.getSystemPassword());
         if (pwdErr != null) {
            return CommonResult.error(pwdErr);
         } else {
            Server server = this.queryEntity(reinstallEditParam.getId());
            if (null == server) {
               return CommonResult.error("服务器不存在");
            } else {
               this.assertServerNotLocked(server);
               return this.resourcePoolUpstreamClient.isResourcePoolServer(server)
                  ? this.reinstallResourcePoolServer(server, reinstallEditParam)
                  : this.reinstallSelfProducedServer(server, reinstallEditParam);
            }
         }
      } catch (Exception var4) {
         log.error("重装服务器失败", var4);
         return CommonResult.error(var4.getMessage());
      }
   }

   private int resolveReinstallPort(Integer remotePort, int defaultPort) {
      return remotePort != null && remotePort > 0 && remotePort <= 65535 ? remotePort : defaultPort;
   }

   private int normalizeFormatDataDisk(Integer formatDataDisk) {
      return Integer.valueOf(1).equals(formatDataDisk) ? 1 : 0;
   }

   @Override
   public CommonResult lockServer(ServerLockParam serverLockParam) {
      try {
         if (serverLockParam == null || StrUtil.isBlank(serverLockParam.getId())) {
            return CommonResult.error("服务器ID不能为空");
         } else if (StrUtil.isBlank(serverLockParam.getLockReason())) {
            return CommonResult.error("锁定原因不能为空");
         } else {
            Server server = this.queryEntity(serverLockParam.getId());
            if (server.getServerStatus().equals(ServerStatusEnum.LOCKED.getCode())) {
               throw new CommonException("服务器已锁定，请联系客服解锁", new Object[0]);
            } else {
               return this.resourcePoolUpstreamClient.isResourcePoolServer(server)
                  ? this.lockResourcePoolServer(server, serverLockParam.getLockReason())
                  : this.lockSelfProducedServer(server, serverLockParam.getLockReason());
            }
         }
      } catch (Exception var3) {
         log.error("锁定服务器失败", var3);
         return CommonResult.error(var3.getMessage());
      }
   }

   @Override
   public CommonResult unlockServer(ServerIdParam serverIdParam) {
      try {
         if (serverIdParam != null && !StrUtil.isBlank(serverIdParam.getId())) {
            Server server = this.queryEntity(serverIdParam.getId());
            if (!server.getServerStatus().equals(ServerStatusEnum.LOCKED.getCode())) {
               throw new CommonException("服务器状态异常，无法解锁", new Object[0]);
            } else {
               return this.resourcePoolUpstreamClient.isResourcePoolServer(server)
                  ? this.unlockResourcePoolServer(server)
                  : this.unlockSelfProducedServer(server);
            }
         } else {
            return CommonResult.error("服务器ID不能为空");
         }
      } catch (Exception var3) {
         log.error("解锁服务器失败", var3);
         return CommonResult.error(var3.getMessage());
      }
   }

   private void clearServerLockReason(String serverId, String status) {
      this.update(
         (Wrapper<Server>)((LambdaUpdateWrapper<Server>)((LambdaUpdateWrapper<Server>)((LambdaUpdateWrapper<Server>)new LambdaUpdateWrapper<Server>().eq(Server::getId, serverId))
                  .set(Server::getServerStatus, status))
               .set(Server::getLockReason, null))
            .set(CommonEntity::getUpdateTime, new Date())
      );
   }

   private void assertServerNotLocked(Server server) {
      if (server != null && ServerStatusEnum.LOCKED.getCode().equals(server.getServerStatus())) {
         throw new CommonException("云服务器已锁定，请先解锁后再操作", new Object[0]);
      }
   }

   @Override
   public CommonResult rebuildServer(ServerIdParam serverIdParam) {
      try {
         Server server = this.queryEntity(serverIdParam.getId());
         if (null == server) {
            return CommonResult.error("服务器不存在");
         } else {
            this.assertServerNotLocked(server);
            return this.resourcePoolUpstreamClient.isResourcePoolServer(server)
               ? this.rebuildResourcePoolServer(server, serverIdParam.getFullRebuild())
               : this.rebuildSelfProducedServer(server, serverIdParam.getFullRebuild());
         }
      } catch (Exception var3) {
         log.error("重建服务器失败", var3);
         return CommonResult.error(var3.getMessage());
      }
   }

   @Override
   public CommonResult updatePasswordServer(PasswordEditParam passwordEditParam) {
      try {
         String pwdErr = OsInstancePasswordValidator.validate(passwordEditParam.getOsPassword());
         if (pwdErr != null) {
            return CommonResult.error(pwdErr);
         } else {
            Server server = this.queryEntity(passwordEditParam.getId());
            if (null == server) {
               return CommonResult.error("服务器不存在");
            } else {
               this.assertServerNotLocked(server);
               return this.resourcePoolUpstreamClient.isResourcePoolServer(server)
                  ? this.updatePasswordResourcePoolServer(server, passwordEditParam)
                  : this.updatePasswordSelfProducedServer(server, passwordEditParam);
            }
         }
      } catch (Exception var4) {
         log.error("重建服务器失败", var4);
         return CommonResult.error(var4.getMessage());
      }
   }

   @Override
   public CommonResult restartServer(ServerIdParam serverIdParam) {
      try {
         Server server = this.queryEntity(serverIdParam.getId());
         if (null == server) {
            return CommonResult.error("服务器不存在");
         } else {
            this.assertServerNotLocked(server);
            return this.resourcePoolUpstreamClient.isResourcePoolServer(server)
               ? this.restartResourcePoolServer(server)
               : this.restartSelfProducedServer(server);
         }
      } catch (Exception var3) {
         log.error("重启服务器失败", var3);
         return CommonResult.error(var3.getMessage());
      }
   }

   @Override
   public CommonResult powerServer(ServerIdParam serverIdParam) {
      try {
         Server server = this.queryEntity(serverIdParam.getId());
         if (null == server) {
            return CommonResult.error("服务器不存在");
         } else {
            this.assertServerNotLocked(server);
            return this.resourcePoolUpstreamClient.isResourcePoolServer(server) ? this.powerResourcePoolServer(server) : this.powerSelfProducedServer(server);
         }
      } catch (Exception var3) {
         log.error("服务器断开电源失败", var3);
         return CommonResult.error(var3.getMessage());
      }
   }

   @Override
   public CommonResult thumbnailServer(ServerIdParam serverIdParam) {
      try {
         Server server = this.queryEntity(serverIdParam.getId());
         if (null == server) {
            return CommonResult.error("服务器不存在");
         } else if (this.resourcePoolUpstreamClient.isResourcePoolServer(server)) {
            return this.resourcePoolUpstreamClient.callOrderServerApi(server, "thumbnail", null);
         } else {
            BaseMemberMaster memberMaster = this.memberMasterApi.selectByMasterId(server.getMasterId());
            log.info("宿主机memberMaster，参数：{}", JSON.toJSONString(memberMaster));
            if (null == memberMaster) {
               return CommonResult.error("宿主机不存在");
            } else {
               CloudControllerAdapter adapter = this.cloudControllerRouter.getAdapterByGuardVersion(memberMaster.getGuardVersion());
               HostParam hostParam = new HostParam();
               hostParam.setHostName(server.getHostName());
               hostParam.setServerId(server.getId());
               hostParam.setMasterIp(this.getMasterIpForTask(memberMaster));
               hostParam.setApiKey(memberMaster.getApiPassword());
               hostParam.setApiPort(memberMaster.getApiPort());
               CommonResult result = adapter.thumbnailHost(hostParam);
               log.info("获取主机运行图片结果：{}", JSON.toJSONString(result));
               return result.getCode().equals(200) ? result : CommonResult.error(result.getMsg());
            }
         }
      } catch (Exception var7) {
         log.error("服务器获取主机运行图片失败", var7);
         return CommonResult.error(var7.getMessage());
      }
   }

   @Override
   public CommonResult syncStatusServer(ServerIdParam serverIdParam) {
      try {
         Server server = this.queryEntity(serverIdParam.getId());
         if (null == server) {
            return CommonResult.error("服务器不存在");
         } else {
            this.assertServerNotLocked(server);
            if (this.resourcePoolUpstreamClient.isResourcePoolServer(server)) {
               CommonResult<?> syncResult = this.resourcePoolUpstreamClient.callOrderServerApi(server, "sync", null);
               if (syncResult != null && Integer.valueOf(200).equals(syncResult.getCode())) {
                  Object data = syncResult.getData();
                  String state = null;
                  if (data instanceof Map) {
                     state = (String)((Map)data).get("state");
                  } else if (data != null) {
                     state = JSONUtil.parseObj(data).getStr("state");
                  }

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
                     log.info("资源池同步云服务器状态成功，serverId={}, state={}, 已更新数据库 serverStatus={}", new Object[]{server.getId(), state, serverStatus});
                  }
               }

               return syncResult;
            } else {
               BaseMemberMaster memberMaster = this.memberMasterApi.selectByMasterId(server.getMasterId());
               log.info("宿主机memberMaster，参数：{}", JSON.toJSONString(memberMaster));
               if (null == memberMaster) {
                  return CommonResult.error("宿主机不存在");
               } else {
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
                     if ("1".equals(memberMaster.getGuardVersion()) || "2".equals(memberMaster.getGuardVersion()) || "3".equals(memberMaster.getGuardVersion())
                        )
                      {
                        Object datax = result.getData();
                        if (datax instanceof Map) {
                           String statex = (String)((Map)datax).get("state");
                           if (StrUtil.isNotBlank(statex)) {
                              String serverStatus;
                              if ("Running".equalsIgnoreCase(statex)) {
                                 serverStatus = ServerStatusEnum.RUNNING.getCode();
                              } else if ("Stopped".equalsIgnoreCase(statex)) {
                                 serverStatus = ServerStatusEnum.STOP.getCode();
                              } else {
                                 serverStatus = ServerStatusEnum.UNKNOWN.getCode();
                              }

                              server.setServerStatus(serverStatus);
                              this.updateById(server);
                              log.info(
                                 "同步云服务器状态成功(宿主机版本{})，serverId={}, state={}, 已更新数据库 serverStatus={}",
                                 new Object[]{memberMaster.getGuardVersion(), server.getId(), statex, serverStatus}
                              );
                           }
                        }
                     }

                     return result;
                  }
               }
            }
         }
      } catch (Exception var10) {
         log.error("服务器同步主机运行状态失败", var10);
         return CommonResult.error(var10.getMessage());
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public CommonResult syncUpstreamOrder(ServerIdParam serverIdParam) {
      try {
         Server server = this.queryEntity(serverIdParam.getId());
         if (server == null) {
            return CommonResult.error("服务器不存在");
         } else if (!this.resourcePoolUpstreamClient.isResourcePoolServer(server)) {
            return CommonResult.error("当前云服务器不是资源池服务器");
         } else {
            String upstreamServerId = StrUtil.blankToDefault(StrUtil.trimToNull(server.getUpstreamServerId()), server.getId());
            JSONObject data = this.resourcePoolUpstreamSyncClient.queryServerDetail(server.getResourcePoolId(), upstreamServerId, "CLOUD");
            BaseServer upstream = new BaseServer();
            BeanUtils.copyProperties(server, upstream);
            BigDecimal localRenewPrice = server.getRenewPrice();
            ResourcePoolUpstreamDataApplier.apply(data, upstream, server.getId());
            upstream.setId(server.getId());
            upstream.setRenewPrice(localRenewPrice);
            this.serverApi.syncFromUpstream(upstream);
            this.serverIpService.replaceFromResourcePoolUpstreamSnapshot(server.getId(), data);
            return CommonResult.ok("同步上游云服务器订单成功");
         }
      } catch (Exception var7) {
         log.error("同步上游云服务器订单失败 serverId={}", serverIdParam == null ? null : serverIdParam.getId(), var7);
         return CommonResult.error(var7.getMessage());
      }
   }

   @Override
   public CommonResult clearStatusServer(ServerIdParam serverIdParam) {
      try {
         Server server = this.queryEntity(serverIdParam.getId());
         if (server == null) {
            return CommonResult.error("服务器不存在");
         } else {
            server.setServerStatus(ServerStatusEnum.RUNNING.getCode());
            this.updateById(server);
            log.info("清除云服务器运行状态成功，已回滚为运行中，serverId={}", server.getId());
            return CommonResult.ok("运行状态已清除并设置为运行中");
         }
      } catch (Exception var3) {
         log.error("清除云服务器运行状态失败", var3);
         return CommonResult.error(var3.getMessage());
      }
   }

   @Override
   public CommonResult disconnectServer(ServerIdParam serverIdParam) {
      try {
         Server server = this.queryEntity(serverIdParam.getId());
         if (null == server) {
            return CommonResult.error("服务器不存在");
         } else {
            return this.resourcePoolUpstreamClient.isResourcePoolServer(server)
               ? this.disconnectResourcePoolServer(server)
               : this.disconnectSelfProducedServer(server);
         }
      } catch (Exception var3) {
         log.error("服务器断网失败", var3);
         return CommonResult.error(var3.getMessage());
      }
   }

   @Override
   public CommonResult reconnectServer(ServerIdParam serverIdParam) {
      try {
         Server server = this.queryEntity(serverIdParam.getId());
         if (null == server) {
            return CommonResult.error("服务器不存在");
         } else {
            return this.resourcePoolUpstreamClient.isResourcePoolServer(server)
               ? this.reconnectResourcePoolServer(server)
               : this.reconnectSelfProducedServer(server);
         }
      } catch (Exception var3) {
         log.error("服务器恢复网络失败", var3);
         return CommonResult.error(var3.getMessage());
      }
   }

   @Override
   public CommonResult resetIpServer(ServerIdParam serverIdParam) {
      try {
         if (serverIdParam != null && !StrUtil.isBlank(serverIdParam.getId())) {
            Server server = this.queryEntity(serverIdParam.getId());
            if (server == null) {
               return CommonResult.error("服务器不存在");
            } else {
               this.assertServerNotLocked(server);
               if (this.resourcePoolUpstreamClient.isResourcePoolServer(server)) {
                  return this.resourcePoolUpstreamClient.callOrderServerApi(server, "resetIp", null);
               } else {
                  BaseMemberMaster memberMaster = this.memberMasterApi.selectByMasterId(server.getMasterId());
                  if (memberMaster == null) {
                     return CommonResult.error("宿主机不存在");
                  } else if ("3".equals(StrUtil.trim(memberMaster.getGuardVersion()))) {
                     HostParam hostParam = new HostParam();
                     hostParam.setHostName(server.getHostName());
                     hostParam.setServerId(server.getId());
                     hostParam.setMasterIp(this.getMasterIpForTask(memberMaster));
                     hostParam.setApiKey(memberMaster.getApiPassword());
                     hostParam.setApiPort(memberMaster.getApiPort());
                     AutoTaskAddParam taskParam = new AutoTaskAddParam();
                     taskParam.setMasterId(memberMaster.getId());
                     taskParam.setServerId(server.getId());
                     taskParam.setProductType(1);
                     taskParam.setTaskType(TaskTypeEnum.RESET_IP.getCode());
                     taskParam.setExecuteTime(CommonTimeFormatUtil.formatNowGmt8());
                     taskParam.setTaskStatus(1);
                     taskParam.setNextRetryTime(new Date());
                     taskParam.setTaskDesc("重置云服务器IP任务");
                     taskParam.setPlatform("B");
                     taskParam.setParams(JSONUtil.toJsonStr(hostParam));
                     taskParam.setRetryCount(0);
                     taskParam.setMaxRetry(3);
                     taskParam.setTimeoutTime(DateUtil.offsetHour(new Date(), 2));
                     this.autoTaskTaskService.add(taskParam);
                     server.setServerStatus(ServerStatusEnum.UPGRADE.getCode());
                     this.updateById(server);
                     return CommonResult.ok("重置IP任务已创建");
                  } else {
                     return !"1".equals(StrUtil.trim(memberMaster.getGuardVersion()))
                        ? CommonResult.error("当前受控版本暂不支持重置云服务器IP")
                        : this.serverIpService.resetCurrentIp(server.getId());
                  }
               }
            }
         } else {
            return CommonResult.error("服务器ID不能为空");
         }
      } catch (Exception var6) {
         log.error("重置云服务器IP失败", var6);
         return CommonResult.error(var6.getMessage());
      }
   }

   @Override
   public CommonResult vncUrl(ServerIdParam serverIdParam) {
      try {
         Server server = this.queryEntity(serverIdParam.getId());
         if (null == server) {
            return CommonResult.error("服务器不存在");
         } else if (this.resourcePoolUpstreamClient.isResourcePoolServer(server)) {
            return this.resourcePoolUpstreamClient.callOrderServerApi(server, "vnc", null);
         } else {
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
               if ("2".equals(memberMaster.getGuardVersion())) {
                  hostParam.setMfcCloudId(server.getMfcCloudId());
                  hostParam.setMfcLoginAccount(memberMaster.getMfcLoginAccount());
                  hostParam.setMfcLoginPassword(memberMaster.getMfcLoginPassword());
               }

               CloudControllerAdapter adapter = this.cloudControllerRouter.getAdapterByGuardVersion(memberMaster.getGuardVersion());
               CommonResult result = adapter.guidHost(hostParam);
               log.info("vncUrl结果：{}", JSON.toJSONString(result));
               return result.getCode().equals(200) ? result : CommonResult.error(result.getMsg());
            }
         }
      } catch (Exception var7) {
         log.error("获取vncUrl失败", var7);
         return CommonResult.error(var7.getMessage());
      }
   }

   @Override
   public CommonResult<ServerBackupRecord> createBackup(ServerBackupCreateParam serverBackupCreateParam) {
      try {
         if (serverBackupCreateParam == null || StrUtil.isBlank(serverBackupCreateParam.getServerId())) {
            return CommonResult.error("服务器ID不能为空");
         } else if (StrUtil.isBlank(serverBackupCreateParam.getBackupName())) {
            return CommonResult.error("备份名称不能为空");
         } else {
            Server server = this.queryEntity(serverBackupCreateParam.getServerId());
            if (server == null) {
               return CommonResult.error("服务器不存在");
            } else {
               Long duplicate = this.serverBackupRecordMapper
                  .selectCount((Wrapper<ServerBackupRecord>)new LambdaQueryWrapper<ServerBackupRecord>().eq(ServerBackupRecord::getServerId, server.getId()));
               if (duplicate != null && duplicate > 0L) {
                  return CommonResult.error("该云服务器已存在备份，每台云服务器仅允许一个备份");
               } else {
                  ServerBackupRecord record;
                  if (this.resourcePoolUpstreamClient.isResourcePoolServer(server)) {
                     JSONObject body = new JSONObject();
                     body.put("backupName", serverBackupCreateParam.getBackupName());
                     CommonResult<?> upstreamResult = this.resourcePoolUpstreamClient.callOrderServerApi(server, "backup/create", body);
                     if (!Integer.valueOf(200).equals(upstreamResult.getCode())) {
                        return CommonResult.error(upstreamResult.getMsg());
                     }

                     record = new ServerBackupRecord();
                     record.setId(SnowflakeIdGenerator.generateId());
                     record.setServerId(server.getId());
                     record.setMemberNo(server.getMemberNo());
                     record.setBackupName(serverBackupCreateParam.getBackupName());
                     record.setBackupPath(this.resolveBackupPathFromUpstream(upstreamResult));
                     record.setCreateTime(new Date());
                     this.serverBackupRecordMapper.insert(record);
                  } else {
                     BaseMemberMaster memberMaster = this.memberMasterApi.selectByMasterId(server.getMasterId());
                     if (memberMaster == null) {
                        return CommonResult.error("宿主机不存在");
                     }

                     if (StrUtil.isBlank(memberMaster.getBackupPath())) {
                        return CommonResult.error("宿主机未配置备份目录");
                     }

                     CloudControllerAdapter adapter = this.cloudControllerRouter.getAdapterByGuardVersion(memberMaster.getGuardVersion());
                     HostBackupParam hostBackupParam = this.buildHostBackupParam(server, memberMaster, serverBackupCreateParam.getBackupName());
                     CommonResult result = adapter.createBackupHost(hostBackupParam);
                     if (!Integer.valueOf(200).equals(result.getCode())) {
                        return result;
                     }

                     record = new ServerBackupRecord();
                     record.setId(SnowflakeIdGenerator.generateId());
                     record.setServerId(server.getId());
                     record.setMemberNo(server.getMemberNo());
                     record.setBackupName(serverBackupCreateParam.getBackupName());
                     record.setBackupPath(memberMaster.getBackupPath());
                     record.setCreateTime(new Date());
                     this.serverBackupRecordMapper.insert(record);
                  }

                  return CommonResult.data(record);
               }
            }
         }
      } catch (Exception var9) {
         log.error("创建云服务器备份失败", var9);
         return CommonResult.error(var9.getMessage());
      }
   }

   @Override
   public CommonResult restoreBackup(ServerBackupIdParam serverBackupIdParam) {
      try {
         ServerBackupRecord record = (ServerBackupRecord)this.serverBackupRecordMapper.selectById(serverBackupIdParam.getId());
         if (record == null) {
            return CommonResult.error("备份记录不存在");
         } else {
            Server server = this.queryEntity(record.getServerId());
            if (server == null) {
               return CommonResult.error("服务器不存在");
            } else if (this.resourcePoolUpstreamClient.isResourcePoolServer(server)) {
               JSONObject body = new JSONObject();
               body.put("backupName", record.getBackupName());
               return this.resourcePoolUpstreamClient.callOrderServerApi(server, "backup/restore", body);
            } else {
               BaseMemberMaster memberMaster = this.memberMasterApi.selectByMasterId(server.getMasterId());
               if (memberMaster == null) {
                  return CommonResult.error("宿主机不存在");
               } else if (StrUtil.isBlank(memberMaster.getBackupPath())) {
                  return CommonResult.error("宿主机未配置备份目录");
               } else {
                  CloudControllerAdapter adapter = this.cloudControllerRouter.getAdapterByGuardVersion(memberMaster.getGuardVersion());
                  HostBackupParam hostBackupParam = this.buildHostBackupParam(server, memberMaster, record.getBackupName());
                  return adapter.restoreBackupHost(hostBackupParam);
               }
            }
         }
      } catch (Exception var7) {
         log.error("还原云服务器备份失败", var7);
         return CommonResult.error(var7.getMessage());
      }
   }

   @Override
   public CommonResult deleteBackup(ServerBackupIdParam serverBackupIdParam) {
      try {
         ServerBackupRecord record = (ServerBackupRecord)this.serverBackupRecordMapper.selectById(serverBackupIdParam.getId());
         if (record == null) {
            return CommonResult.error("备份记录不存在");
         } else {
            Server server = this.queryEntity(record.getServerId());
            if (server == null) {
               return CommonResult.error("服务器不存在");
            } else if (this.resourcePoolUpstreamClient.isResourcePoolServer(server)) {
               JSONObject body = new JSONObject();
               body.put("backupName", record.getBackupName());
               CommonResult<?> upstreamResult = this.resourcePoolUpstreamClient.callOrderServerApi(server, "backup/delete", body);
               if (!Integer.valueOf(200).equals(upstreamResult.getCode())) {
                  return upstreamResult;
               } else {
                  this.serverBackupRecordMapper.deleteById(record.getId());
                  return CommonResult.ok("删除备份成功");
               }
            } else {
               BaseMemberMaster memberMaster = this.memberMasterApi.selectByMasterId(server.getMasterId());
               if (memberMaster == null) {
                  return CommonResult.error("宿主机不存在");
               } else if (StrUtil.isBlank(memberMaster.getBackupPath())) {
                  return CommonResult.error("宿主机未配置备份目录");
               } else {
                  CloudControllerAdapter adapter = this.cloudControllerRouter.getAdapterByGuardVersion(memberMaster.getGuardVersion());
                  HostBackupParam hostBackupParam = this.buildHostBackupParam(server, memberMaster, record.getBackupName());
                  CommonResult result = adapter.removeBackupHost(hostBackupParam);
                  if (!Integer.valueOf(200).equals(result.getCode())) {
                     return result;
                  } else {
                     this.serverBackupRecordMapper.deleteById(record.getId());
                     return CommonResult.ok("删除备份成功");
                  }
               }
            }
         }
      } catch (Exception var8) {
         log.error("删除云服务器备份失败", var8);
         return CommonResult.error(var8.getMessage());
      }
   }

   @Override
   public CommonResult<List<ServerBackupRecord>> listBackup(ServerIdParam serverIdParam) {
      try {
         Server server = this.queryEntity(serverIdParam.getId());
         if (server == null) {
            return CommonResult.error("服务器不存在");
         } else {
            List<ServerBackupRecord> list = this.serverBackupRecordMapper
               .selectList(
                  (Wrapper<ServerBackupRecord>)((LambdaQueryWrapper<ServerBackupRecord>)new LambdaQueryWrapper<ServerBackupRecord>().eq(ServerBackupRecord::getServerId, server.getId()))
                     .orderByDesc(ServerBackupRecord::getCreateTime)
               );
            return CommonResult.data(list);
         }
      } catch (Exception var4) {
         log.error("查询云服务器备份列表失败", var4);
         return CommonResult.error(var4.getMessage());
      }
   }

   @Override
   public CommonResult<ServerSnapshotRecord> createSnapshot(ServerSnapshotCreateParam serverSnapshotCreateParam) {
      try {
         if (serverSnapshotCreateParam != null && !StrUtil.isBlank(serverSnapshotCreateParam.getServerId())) {
            Server server = this.queryEntity(serverSnapshotCreateParam.getServerId());
            if (server == null) {
               return CommonResult.error("服务器不存在");
            } else {
               CommonResult limitResult = this.checkSnapshotLimit(server);
               if (!Integer.valueOf(200).equals(limitResult.getCode())) {
                  return limitResult;
               } else {
                  String snapshotName = this.generateUniqueSnapshotName(server.getHostName());
                  if (this.resourcePoolUpstreamClient.isResourcePoolServer(server)) {
                     JSONObject body = new JSONObject();
                     body.put("snapshotName", snapshotName);
                     CommonResult<?> upstreamResult = this.resourcePoolUpstreamClient.callOrderServerApi(server, "snapshot/add", body);
                     if (!Integer.valueOf(200).equals(upstreamResult.getCode())) {
                        return CommonResult.error(upstreamResult.getMsg());
                     }
                  } else {
                     BaseMemberMaster memberMaster = this.memberMasterApi.selectByMasterId(server.getMasterId());
                     if (memberMaster == null) {
                        return CommonResult.error("宿主机不存在");
                     }

                     CloudControllerAdapter adapter = this.cloudControllerRouter.getAdapterByGuardVersion(memberMaster.getGuardVersion());
                     HostBackupParam hostBackupParam = this.buildHostSnapshotParam(server, memberMaster, snapshotName);
                     CommonResult result = adapter.createSnapshotHost(hostBackupParam);
                     if (!Integer.valueOf(200).equals(result.getCode())) {
                        return result;
                     }
                  }

                  ServerSnapshotRecord record = new ServerSnapshotRecord();
                  record.setId(SnowflakeIdGenerator.generateId());
                  record.setServerId(server.getId());
                  record.setMemberNo(server.getMemberNo());
                  record.setSnapshotName(snapshotName);
                  record.setCreateTime(new Date());
                  this.serverSnapshotRecordMapper.insert(record);
                  return CommonResult.data(record);
               }
            }
         } else {
            return CommonResult.error("服务器ID不能为空");
         }
      } catch (Exception var9) {
         log.error("创建云服务器快照失败", var9);
         return CommonResult.error(var9.getMessage());
      }
   }

   @Override
   public CommonResult restoreSnapshot(ServerSnapshotIdParam serverSnapshotIdParam) {
      try {
         ServerSnapshotRecord record = (ServerSnapshotRecord)this.serverSnapshotRecordMapper.selectById(serverSnapshotIdParam.getId());
         if (record == null) {
            return CommonResult.error("快照记录不存在");
         } else {
            Server server = this.queryEntity(record.getServerId());
            if (server == null) {
               return CommonResult.error("服务器不存在");
            } else if (this.resourcePoolUpstreamClient.isResourcePoolServer(server)) {
               JSONObject body = new JSONObject();
               body.put("snapshotName", record.getSnapshotName());
               return this.resourcePoolUpstreamClient.callOrderServerApi(server, "snapshot/restore", body);
            } else {
               BaseMemberMaster memberMaster = this.memberMasterApi.selectByMasterId(server.getMasterId());
               if (memberMaster == null) {
                  return CommonResult.error("宿主机不存在");
               } else {
                  CloudControllerAdapter adapter = this.cloudControllerRouter.getAdapterByGuardVersion(memberMaster.getGuardVersion());
                  HostBackupParam hostBackupParam = this.buildHostSnapshotParam(server, memberMaster, record.getSnapshotName());
                  return adapter.restoreSnapshotHost(hostBackupParam);
               }
            }
         }
      } catch (Exception var7) {
         log.error("还原云服务器快照失败", var7);
         return CommonResult.error(var7.getMessage());
      }
   }

   @Override
   public CommonResult deleteSnapshot(ServerSnapshotIdParam serverSnapshotIdParam) {
      try {
         ServerSnapshotRecord record = (ServerSnapshotRecord)this.serverSnapshotRecordMapper.selectById(serverSnapshotIdParam.getId());
         if (record == null) {
            return CommonResult.error("快照记录不存在");
         } else {
            Server server = this.queryEntity(record.getServerId());
            if (server == null) {
               return CommonResult.error("服务器不存在");
            } else {
               CommonResult result;
               if (this.resourcePoolUpstreamClient.isResourcePoolServer(server)) {
                  JSONObject body = new JSONObject();
                  body.put("snapshotName", record.getSnapshotName());
                  result = this.resourcePoolUpstreamClient.callOrderServerApi(server, "snapshot/remove", body);
               } else {
                  BaseMemberMaster memberMaster = this.memberMasterApi.selectByMasterId(server.getMasterId());
                  if (memberMaster == null) {
                     return CommonResult.error("宿主机不存在");
                  }

                  CloudControllerAdapter adapter = this.cloudControllerRouter.getAdapterByGuardVersion(memberMaster.getGuardVersion());
                  HostBackupParam hostBackupParam = this.buildHostSnapshotParam(server, memberMaster, record.getSnapshotName());
                  result = adapter.removeSnapshotHost(hostBackupParam);
               }

               if (!Integer.valueOf(200).equals(result.getCode())) {
                  return result;
               } else {
                  this.serverSnapshotRecordMapper.deleteById(record.getId());
                  return CommonResult.ok("删除快照成功");
               }
            }
         }
      } catch (Exception var8) {
         log.error("删除云服务器快照失败", var8);
         return CommonResult.error(var8.getMessage());
      }
   }

   @Override
   public CommonResult<List<ServerSnapshotRecord>> listSnapshot(ServerIdParam serverIdParam) {
      try {
         Server server = this.queryEntity(serverIdParam.getId());
         if (server == null) {
            return CommonResult.error("服务器不存在");
         } else {
            List<ServerSnapshotRecord> list = this.serverSnapshotRecordMapper
               .selectList(
                  (Wrapper<ServerSnapshotRecord>)((LambdaQueryWrapper<ServerSnapshotRecord>)new LambdaQueryWrapper<ServerSnapshotRecord>().eq(ServerSnapshotRecord::getServerId, server.getId()))
                     .orderByDesc(ServerSnapshotRecord::getCreateTime)
               );
            return CommonResult.data(list);
         }
      } catch (Exception var4) {
         log.error("查询云服务器快照列表失败", var4);
         return CommonResult.error(var4.getMessage());
      }
   }

   private HostBackupParam buildHostBackupParam(Server server, BaseMemberMaster memberMaster, String backupName) {
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

   private HostBackupParam buildHostSnapshotParam(Server server, BaseMemberMaster memberMaster, String snapshotName) {
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
         Long duplicate = this.serverSnapshotRecordMapper
            .selectCount((Wrapper<ServerSnapshotRecord>)new LambdaQueryWrapper<ServerSnapshotRecord>().eq(ServerSnapshotRecord::getSnapshotName, snapshotName));
         if (duplicate == null || duplicate == 0L) {
            return snapshotName;
         }
      }

      return prefix + "-" + System.currentTimeMillis();
   }

   private int randomSixDigits() {
      return 100000 + (int)(Math.random() * 900000.0);
   }

   private CommonResult checkSnapshotLimit(Server server) {
      if (server != null && !StrUtil.isBlank(server.getLineId())) {
         BaseLocalLine line = this.serverLocalLineApi.selectByLineId(server.getLineId());
         if (line == null) {
            return CommonResult.error("线路不存在");
         } else if (!Integer.valueOf(1).equals(line.getSnapPriceType())) {
            return CommonResult.error("当前线路未开启快照");
         } else {
            Integer maxSnapshotNum = line.getSnapOpenMax();
            if (maxSnapshotNum != null && maxSnapshotNum > 0) {
               Long currentCount = this.serverSnapshotRecordMapper
                  .selectCount((Wrapper<ServerSnapshotRecord>)new LambdaQueryWrapper<ServerSnapshotRecord>().eq(ServerSnapshotRecord::getServerId, server.getId()));
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

   private String resolveBackupPathFromUpstream(CommonResult<?> upstreamResult) {
      if (upstreamResult != null && upstreamResult.getData() != null) {
         JSONObject data = JSON.parseObject(JSON.toJSONString(upstreamResult.getData()));
         return data != null ? StrUtil.blankToDefault(data.getString("backupPath"), "") : "";
      } else {
         return "";
      }
   }

   @Transactional
   @Override
   public CommonResult upgradeServer(ServerUpgradeParam serverUpgradeParam) {
      try {
         Server server = this.queryEntity(serverUpgradeParam.getServerId());
         if (null == server) {
            return CommonResult.error("服务器不存在");
         } else {
            this.assertServerNotLocked(server);
            BaseMemberMaster memberMaster = this.memberMasterApi.selectByMasterId(server.getMasterId());
            log.info("宿主机memberMaster，参数：{}", JSON.toJSONString(memberMaster));
            if (null == memberMaster) {
               return CommonResult.error("宿主机不存在");
            } else {
               server.setCpu(serverUpgradeParam.getCpu());
               server.setRam(serverUpgradeParam.getRam());
               server.setDataDisks(serverUpgradeParam.getDataDisks());
               server.setBandwidth(serverUpgradeParam.getBandwidth());
               server.setRam(serverUpgradeParam.getRam());
               this.updateById(server);
               this.hostStatsRefreshService.refreshByMasterId(server.getMasterId());
               List<ServerIp> serverIps = this.serverIpService.queryByServerId(server.getId());
               String ipStr = serverIps.stream().map(ServerIp::getIp).collect(Collectors.joining(","));
               HostParam hostParam = new HostParam();
               hostParam.setHostName(server.getHostName());
               hostParam.setServerId(server.getId());
               hostParam.setMasterIp(this.getMasterIpForTask(memberMaster));
               hostParam.setApiKey(memberMaster.getApiPassword());
               hostParam.setApiPort(memberMaster.getApiPort());
               hostParam.setIpStr(ipStr);
               CommonResult result = HostOpenerUtil.updateHost(hostParam);
               log.info("vncUrl结果：{}", JSON.toJSONString(result));
               return result.getCode().equals(200) ? result : CommonResult.error(result.getMsg());
            }
         }
      } catch (Exception var8) {
         log.error("升级服务器失败", var8);
         return CommonResult.error(var8.getMessage());
      }
   }

   @Override
   public List<Server> getServersToCheck(LocalDateTime sevenDaysLater) {
      return this.serverMapper.getServersToCheck(sevenDaysLater);
   }

   @Override
   public List<Server> getServersForAutoRenew(LocalDateTime windowEnd) {
      return this.serverMapper.getServersForAutoRenew(windowEnd);
   }

   @Override
   public void isRenew(ServerRenewAutoParam param) {
      if (param != null && !StrUtil.isBlank(param.getId()) && param.getRenewAuto() != null) {
         if (param.getRenewAuto() != 0 && param.getRenewAuto() != 1) {
            throw new CommonException("自动续费开关只能为0或1", new Object[0]);
         } else {
            Server server = (Server)this.getById(param.getId());
            if (server == null) {
               throw new CommonException("服务器不存在", new Object[0]);
            } else if (BusinessStatusEnum.DELETE.getCode().equals(server.getBusinessStatus())) {
               throw new CommonException("已删除服务器不支持修改自动续费", new Object[0]);
            } else if (Integer.valueOf(1).equals(param.getRenewAuto()) && BusinessStatusEnum.EXPIRED.getCode().equals(server.getBusinessStatus())) {
               throw new CommonException("服务器已过期，无法开启自动续费", new Object[0]);
            } else {
               this.update(
                  (Wrapper<Server>)((LambdaUpdateWrapper<Server>)new LambdaUpdateWrapper<Server>().eq(Server::getId, param.getId())).set(Server::getRenewAuto, param.getRenewAuto())
               );
            }
         }
      } else {
         throw new CommonException("参数不能为空", new Object[0]);
      }
   }

   @Override
   public void updateServerStatus(String serverId) {
      this.updateServerStatus(serverId, "到期自动关机");
   }

   @Override
   public void updateServerStatus(String serverId, String taskDesc) {
      log.info("更新服务器状态，参数：{}", serverId);
      Server server = this.queryEntity(serverId);
      if (null != server) {
         CommonResult result = this.resourcePoolUpstreamClient.isResourcePoolServer(server)
            ? this.closeResourcePoolServer(server)
            : this.closeSelfProducedServer(server, taskDesc);
         if (!result.getCode().equals(200)) {
            log.error("服务器关机任务创建失败，参数：{}，返回结果：{}", serverId, JSON.toJSONString(result));
         } else {
            log.info("服务器 {} 关机任务已创建", serverId);
         }

         this.serverMapper.updateServerStatus(serverId);
         log.info("服务器 {} 业务状态已更新为过期", serverId);
      }
   }

   @Override
   public void updateRenewPrice(ServerRenewPriceParam serverRenewPriceParam) {
      if (serverRenewPriceParam != null && !ObjectUtil.isEmpty(serverRenewPriceParam.getServerIds())) {
         List<String> serverIds = serverRenewPriceParam.getServerIds().stream().distinct().collect(Collectors.toList());
         List<Server> serverList = this.list((Wrapper<Server>)new LambdaQueryWrapper<Server>().in(Server::getId, serverIds));
         if (serverList.size() != serverIds.size()) {
            throw new CommonException("存在不存在的服务器", new Object[0]);
         } else {
            this.update(
               (Wrapper<Server>)((LambdaUpdateWrapper<Server>)((LambdaUpdateWrapper<Server>)new LambdaUpdateWrapper<Server>().in(Server::getId, serverIds))
                     .set(Server::getRenewPrice, serverRenewPriceParam.getRenewPrice()))
                  .set(CommonEntity::getUpdateTime, new Date())
            );
         }
      } else {
         throw new CommonException("服务器标识列表不能为空", new Object[0]);
      }
   }

   @Override
   public void updateEndTime(ServerEndTimeParam serverEndTimeParam) {
      Server server = this.queryEntity(serverEndTimeParam.getId());
      if (server == null) {
         throw new CommonException("云服务器不存在", new Object[0]);
      } else {
         server.setEndTime(serverEndTimeParam.getEndTime());
         DevConfigApi devConfigApi = (DevConfigApi)SpringUtil.getBean(DevConfigApi.class);
         String cloudServerRetentionPeriod = devConfigApi.getValueByKey("CLOUD_SERVER_RETENTION_PERIOD");
         server.setDeleteTime(DateUtil.offsetDay(server.getEndTime(), Integer.parseInt(cloudServerRetentionPeriod)));
         this.updateById(server);
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void batchAddEndDays(ServerAddEndDaysParam param) {
      if (param != null && !ObjectUtil.isEmpty(param.getServerIds())) {
         if (param.getDays() != null && param.getDays() > 0) {
            List<String> serverIds = param.getServerIds().stream().distinct().collect(Collectors.toList());
            List<Server> serverList = this.list((Wrapper<Server>)new LambdaQueryWrapper<Server>().in(Server::getId, serverIds));
            if (serverList.size() != serverIds.size()) {
               throw new CommonException("存在不存在的服务器", new Object[0]);
            } else {
               DevConfigApi devConfigApi = (DevConfigApi)SpringUtil.getBean(DevConfigApi.class);
               String cloudServerRetentionPeriod = devConfigApi.getValueByKey("CLOUD_SERVER_RETENTION_PERIOD");
               int retentionDays = Integer.parseInt(cloudServerRetentionPeriod);
               Date now = new Date();

               for (Server server : serverList) {
                  if (server.getEndTime() == null) {
                     throw new CommonException("云服务器到期时间为空，无法增加天数，id值为：{}", new Object[]{server.getId()});
                  }

                  server.setEndTime(DateUtil.offsetDay(server.getEndTime(), param.getDays()));
                  server.setDeleteTime(DateUtil.offsetDay(server.getEndTime(), retentionDays));
                  server.setUpdateTime(now);
               }

               this.updateBatchById(serverList);
            }
         } else {
            throw new CommonException("增加的到期天数必须大于0", new Object[0]);
         }
      } else {
         throw new CommonException("服务器标识列表不能为空", new Object[0]);
      }
   }

   @Override
   public List<Server> getServersToDeleteCheck() {
      return this.serverMapper.getServersToDeleteCheck();
   }

   @Override
   public CommonResult plus(ServerTransferRecordAddParam serverTransferRecordAddParam) {
      return this.serverTransferRecordService.add(serverTransferRecordAddParam);
   }

   @Override
   public CommonResult updateStatus(ServerTransferRecordIdParam serverTransferRecordIdParam) {
      return this.serverTransferRecordService.updateStatus(serverTransferRecordIdParam);
   }

   @Override
   public void editRemark(ServerRemarkEditParam serverRemarkEditParam) {
      Server server = (Server)this.getOne((Wrapper<Server>)new LambdaQueryWrapper<Server>().eq(Server::getId, serverRemarkEditParam.getId()));
      if (ObjectUtil.isEmpty(server)) {
         throw new CommonException("云服务器不存在，id值为：{}", new Object[]{serverRemarkEditParam.getId()});
      } else {
         server.setRemark(serverRemarkEditParam.getRemark());
         this.updateById(server);
      }
   }

   @Override
   public CommonResult renewServer(String orderNo, String serverId, Integer months) {
      try {
         Server server = (Server)this.getOne((Wrapper<Server>)new LambdaQueryWrapper<Server>().eq(Server::getId, serverId));
         if (server == null) {
            return CommonResult.error("服务器不存在");
         } else {
            this.assertServerNotLocked(server);
            if (Integer.valueOf(1).equals(server.getType())) {
               log.info("资源池服务器续费，发送请求至上游，订单号：{}，服务器ID：{}", orderNo, serverId);
               String upstreamIdA = resolveResourcePoolUpstreamServerId(server);
               if (StrUtil.isBlank(upstreamIdA)) {
                  return CommonResult.error("上游服务器ID未同步，无法通知上游，请等待资源池同步完成后再试");
               } else {
                  String effectiveOrderNo = resolveResourcePoolOrderNo(orderNo, server);
                  if (StrUtil.isBlank(effectiveOrderNo)) {
                     return CommonResult.error("订单号不能为空，无法通知上游");
                  } else {
                     this.applyRenewEndTime(server, months);
                     JSONObject params = new JSONObject();
                     params.put("downstreamOrderNo", effectiveOrderNo);
                     params.put("orderNo", effectiveOrderNo);
                     putResourcePoolUpstreamServerFields(params, server, upstreamIdA);
                     params.put("months", months);
                     BigDecimal payPrice = server.getRenewPrice() != null
                        ? server.getRenewPrice().multiply(BigDecimal.valueOf((long)months.intValue()))
                        : BigDecimal.ZERO;
                     params.put("payPrice", payPrice);
                     BaseResourcePoolRequest poolRequest = new BaseResourcePoolRequest();
                     poolRequest.setOrderNo(effectiveOrderNo);
                     poolRequest.setMemberNo(server.getMemberNo() != null ? Long.valueOf(server.getMemberNo()) : null);
                     poolRequest.setResourcePoolId(server.getResourcePoolId());
                     poolRequest.setOperationType("RENEW");
                     poolRequest.setAServerId(upstreamIdA);
                     poolRequest.setMonths(months);
                     poolRequest.setProductType(ProductTypeEnum.CLOUD_SERVER.getCode());
                     poolRequest.setRequestParams(JSON.toJSONString(params));
                     this.resourcePoolApi.createRequest(poolRequest);
                     log.info("✅ 资源池续费请求已创建，等待定时任务发送，订单号：{}", effectiveOrderNo);
                     return CommonResult.ok();
                  }
               }
            } else {
               boolean needStartAfterRenew = this.applyRenewEndTime(server, months);
               BaseMemberMaster memberMaster = this.memberMasterApi.selectByMasterId(server.getMasterId());
               if (memberMaster == null) {
                  log.warn("续费已更新到期时间，宿主机不存在，跳过底层通知，orderNo={}，serverId={}", orderNo, serverId);
               } else {
                  this.notifyRkHyperVRenewIfNeeded(server, memberMaster, months);
               }

               if (needStartAfterRenew && memberMaster != null) {
                  log.info("服务器已过期且已停止，自动启动服务器");
                  HostParam hostParam = new HostParam();
                  hostParam.setHostName(server.getHostName());
                  hostParam.setServerId(server.getId());
                  hostParam.setMasterIp(memberMaster.getIp());
                  hostParam.setApiKey(memberMaster.getApiPassword());
                  hostParam.setApiPort(memberMaster.getApiPort());
                  AutoTaskAddParam taskParam = new AutoTaskAddParam();
                  taskParam.setMasterId(server.getMasterId());
                  taskParam.setServerId(server.getId());
                  taskParam.setProductType(1);
                  taskParam.setTaskType(TaskTypeEnum.RENEW.getCode());
                  taskParam.setExecuteTime(CommonTimeFormatUtil.formatNowGmt8());
                  taskParam.setTaskStatus(1);
                  taskParam.setNextRetryTime(TimeUtil.toDate(TimeUtil.now()));
                  taskParam.setTaskDesc("续费后启动云服务器任务");
                  taskParam.setPlatform("C");
                  taskParam.setParams(JSONUtil.toJsonStr(hostParam));
                  taskParam.setRetryCount(0);
                  taskParam.setMaxRetry(3);
                  taskParam.setTimeoutTime(DateUtil.offsetHour(TimeUtil.toDate(TimeUtil.now()), 2));
                  this.autoTaskTaskService.add(taskParam);
               }

               try {
                  this.resourcePoolApi
                     .logResourceChange(
                        orderNo != null ? orderNo : server.getOrderNo(),
                        Long.valueOf(server.getMemberNo()),
                        "UPDATE",
                        StrUtil.blankToDefault(server.getUpstreamServerId(), server.getId()),
                        "CLOUD"
                     );
               } catch (Exception var10) {
                  log.error("记录服务器续费变更日志失败，服务器 ID: {}", serverId, var10);
               }

               return CommonResult.ok();
            }
         }
      } catch (CommonException var11) {
         log.error("服务器续费失败: {}", var11.getMessage(), var11);
         return CommonResult.error(var11.getMessage());
      } catch (Exception var12) {
         log.error("服务器续费失败", var12);
         return CommonResult.error("系统错误");
      }
   }

   @Override
   public CommonResult renewServer(ServerRenewParam serverRenewParam) {
      try {
         Server server = (Server)this.getOne((Wrapper<Server>)new LambdaQueryWrapper<Server>().eq(Server::getId, serverRenewParam.getServerId()));
         if (server == null) {
            return CommonResult.error("服务器不存在");
         } else {
            this.assertServerNotLocked(server);
            server.setEndTime(DateUtil.offsetMonth(server.getEndTime(), serverRenewParam.getMonths()));
            DevConfigApi devConfigApi = (DevConfigApi)SpringUtil.getBean(DevConfigApi.class);
            String cloudServerRetentionPeriod = devConfigApi.getValueByKey("CLOUD_SERVER_RETENTION_PERIOD");
            log.info("云服务器保留天数：{}", cloudServerRetentionPeriod);
            server.setDeleteTime(DateUtil.offsetDay(server.getEndTime(), Integer.valueOf(cloudServerRetentionPeriod)));
            boolean needStartAfterRenew = this.isExpiredAndStopped(server);
            if (BusinessStatusEnum.EXPIRED.getCode().equals(server.getBusinessStatus())) {
               server.setBusinessStatus(BusinessStatusEnum.NORMAL.getCode());
            }

            this.updateById(server);
            BaseMemberMaster memberMaster = this.memberMasterApi.selectByMasterId(server.getMasterId());
            if (memberMaster == null) {
               log.warn("续费已更新到期时间，宿主机不存在，跳过底层通知，serverId={}", serverRenewParam.getServerId());
            } else {
               if (needStartAfterRenew) {
                  log.info("服务器已过期且已停止，自动启动服务器");
                  this.updateServerStatus(server, serverRenewParam.getServerId());
               }

               this.notifyRkHyperVRenewIfNeeded(server, memberMaster, serverRenewParam.getMonths());
            }

            try {
               this.resourcePoolApi
                  .logResourceChange(
                     server.getOrderNo() != null ? server.getOrderNo() : "N/A",
                     Long.valueOf(server.getMemberNo()),
                     "UPDATE",
                     StrUtil.blankToDefault(server.getUpstreamServerId(), server.getId()),
                     "CLOUD"
                  );
            } catch (Exception var8) {
               log.error("记录服务器续费变更日志失败，服务器 ID: {}", serverRenewParam.getServerId(), var8);
            }

            return CommonResult.ok();
         }
      } catch (CommonException var9) {
         log.error("服务器续费失败: {}", var9.getMessage(), var9);
         return CommonResult.error(var9.getMessage());
      } catch (Exception var10) {
         log.error("服务器续费失败", var10);
         return CommonResult.error("系统错误");
      }
   }

   private void notifyRkHyperVRenewIfNeeded(Server server, BaseMemberMaster memberMaster, Integer months) {
      if (server != null && memberMaster != null && "3".equals(memberMaster.getGuardVersion())) {
         HostParam hostParam = new HostParam();
         hostParam.setHostName(server.getHostName());
         hostParam.setServerId(server.getId());
         hostParam.setMasterIp(this.getMasterIpForTask(memberMaster));
         hostParam.setApiKey(memberMaster.getApiPassword());
         hostParam.setApiPort(memberMaster.getApiPort());
         hostParam.setMonths(months);

         try {
            CloudControllerAdapter adapter = this.cloudControllerRouter.getAdapterByGuardVersion(memberMaster.getGuardVersion());
            CommonResult result = adapter.renewHost(hostParam);
            log.info("RkHyper-v实例续费通知结果: serverId={}, months={}, result={}", new Object[]{server.getId(), months, JSON.toJSONString(result)});
            if (result == null || result.getCode() == null || !Integer.valueOf(200).equals(result.getCode())) {
               log.warn("RkHyper-v实例续费通知失败，不影响本地续费时间更新: serverId={}, months={}, result={}", new Object[]{server.getId(), months, JSON.toJSONString(result)});
            }
         } catch (Exception var7) {
            log.warn("RkHyper-v实例续费通知异常，不影响本地续费时间更新: serverId={}, months={}, e={}", new Object[]{server.getId(), months, var7.getMessage(), var7});
         }
      }
   }

   @Override
   public void applyLocalResourcePoolAfterUpstreamSuccess(String operationType, String serverId, String requestParamsJson, String orderNo) {
      if (!StrUtil.isBlank(serverId) && !StrUtil.isBlank(operationType)) {
         Server server = (Server)this.getById(serverId);
         if (this.isPoolManagedCloudServer(server)) {
            cn.hutool.json.JSONObject params = StrUtil.isBlank(requestParamsJson) ? new cn.hutool.json.JSONObject() : JSONUtil.parseObj(requestParamsJson);

            try {
               switch (operationType) {
                  case "RENEW":
                     this.applyPoolRenewLocalAfterUpstream(server, serverId, params);
                     break;
                  case "UPGRADE_BANDWIDTH":
                     Integer bw = params.getInt("bandwidth");
                     if (bw != null) {
                        server.setBandwidth(bw);
                     }

                     this.updatePoolUpgradeRenewPrice(server, params);
                     server.setServerStatus(ServerStatusEnum.RUNNING.getCode());
                     this.updateById(server);
                     break;
                  case "UPGRADE_CPU_RAM":
                     if (params.getInt("cpu") != null) {
                        server.setCpu(params.getInt("cpu"));
                     }

                     if (params.getInt("ram") != null) {
                        server.setRam(params.getInt("ram"));
                     }

                     this.updatePoolUpgradeRenewPrice(server, params);
                     server.setServerStatus(ServerStatusEnum.RUNNING.getCode());
                     this.updateById(server);
                     break;
                  case "UPGRADE_DEFENSE":
                     if (params.getInt("defense") != null) {
                        server.setDefense(params.getInt("defense"));
                     }

                     this.updatePoolUpgradeRenewPrice(server, params);
                     this.updateById(server);
                     break;
                  case "UPGRADE_DISK":
                     JSONArray disks = params.getJSONArray("dataDisks");
                     if (disks != null && !disks.isEmpty()) {
                        List<Integer> diskList = new ArrayList<>(disks.size());

                        for (int i = 0; i < disks.size(); i++) {
                           diskList.add(disks.getInt(i));
                        }

                        server.setDataDisks(diskList);
                     }

                     if (params.getInt("systemDisk") != null) {
                        server.setSystemDisk(params.getInt("systemDisk"));
                     }

                     this.updatePoolUpgradeRenewPrice(server, params);
                     server.setServerStatus(ServerStatusEnum.RUNNING.getCode());
                     this.updateById(server);
                     break;
                  case "DELETE":
                     server.setBusinessStatus(BusinessStatusEnum.DELETE.getCode());
                     server.setServerStatus(ServerStatusEnum.STOP.getCode());
                     server.setPublicIp(null);
                     server.setPrivateIp(null);
                     this.updateById(server);
                     this.releaseMacAfterDelete(serverId);
                     this.serverIpMapper.deleteByServerId(serverId);
                     this.serverPortService.deleteVps(serverId);
               }
            } catch (Exception var13) {
               log.error("上游已成功，本地资源池云服务器同步失败 serverId={} op={}", new Object[]{serverId, operationType, var13});
            }
         }
      }
   }

   private boolean isPoolManagedCloudServer(Server server) {
      return server != null
         && (Integer.valueOf(1).equals(server.getType()) || StrUtil.isNotBlank(server.getResourcePoolId()) || StrUtil.isNotBlank(server.getUpstreamServerId()));
   }

   private boolean applyRenewEndTime(Server server, Integer months) {
      boolean needStartAfterRenew = this.isExpiredAndStopped(server);
      server.setEndTime(DateUtil.offsetMonth(server.getEndTime(), months));
      DevConfigApi devConfigApi = (DevConfigApi)SpringUtil.getBean(DevConfigApi.class);
      String cloudServerRetentionPeriod = devConfigApi.getValueByKey("CLOUD_SERVER_RETENTION_PERIOD");
      log.info("云服务器保留天数：{}", cloudServerRetentionPeriod);
      server.setDeleteTime(DateUtil.offsetDay(server.getEndTime(), Integer.valueOf(cloudServerRetentionPeriod)));
      if (BusinessStatusEnum.EXPIRED.getCode().equals(server.getBusinessStatus())) {
         server.setBusinessStatus(BusinessStatusEnum.NORMAL.getCode());
      }

      this.updateById(server);
      return needStartAfterRenew;
   }

   private void applyPoolRenewLocalAfterUpstream(Server server, String serverId, cn.hutool.json.JSONObject params) {
      Server current = (Server)this.getById(serverId);
      if (current != null) {
         boolean needStartAfterRenew = this.isExpiredAndStopped(current);
         if (BusinessStatusEnum.EXPIRED.getCode().equals(current.getBusinessStatus())) {
            current.setBusinessStatus(BusinessStatusEnum.NORMAL.getCode());
            this.updateById(current);
         }

         server = (Server)this.getById(serverId);
         if (server != null) {
            if (needStartAfterRenew) {
               log.info("资源池续费同步：已过期且停止，已恢复业务状态并创建开机任务 serverId={}", serverId);
               BaseMemberMaster memberMaster = this.memberMasterApi.selectByMasterId(server.getMasterId());
               if (memberMaster != null) {
                  HostParam hostParam = new HostParam();
                  hostParam.setHostName(server.getHostName());
                  hostParam.setServerId(server.getId());
                  hostParam.setMasterIp(this.getMasterIpForTask(memberMaster));
                  hostParam.setApiKey(memberMaster.getApiPassword());
                  hostParam.setApiPort(memberMaster.getApiPort());
                  AutoTaskAddParam taskParam = new AutoTaskAddParam();
                  taskParam.setMasterId(server.getMasterId());
                  taskParam.setServerId(server.getId());
                  taskParam.setProductType(1);
                  taskParam.setTaskType(TaskTypeEnum.RENEW.getCode());
                  taskParam.setExecuteTime(CommonTimeFormatUtil.formatNowGmt8());
                  taskParam.setTaskStatus(1);
                  taskParam.setNextRetryTime(TimeUtil.toDate(TimeUtil.now()));
                  taskParam.setTaskDesc("续费后启动云服务器任务");
                  taskParam.setPlatform("C");
                  taskParam.setParams(JSONUtil.toJsonStr(hostParam));
                  taskParam.setRetryCount(0);
                  taskParam.setMaxRetry(3);
                  taskParam.setTimeoutTime(DateUtil.offsetHour(TimeUtil.toDate(TimeUtil.now()), 2));
                  this.autoTaskTaskService.add(taskParam);
               }
            }
         }
      }
   }

   private void updatePoolUpgradeRenewPrice(Server server, cn.hutool.json.JSONObject params) {
      BigDecimal renewPrice = params.getBigDecimal("renewPrice");
      if (renewPrice == null && params.containsKey("renewPrice")) {
         Object o = params.get("renewPrice");
         if (o != null) {
            try {
               renewPrice = new BigDecimal(o.toString());
            } catch (Exception var6) {
            }
         }
      }

      if (renewPrice != null) {
         server.setRenewPrice(renewPrice);
      } else {
         log.warn("资源池升级回写续费价缺少 renewPrice，serverId={}", server.getId());
      }
   }

   private boolean isExpiredAndStopped(Server server) {
      return BusinessStatusEnum.EXPIRED.getCode().equals(server.getBusinessStatus()) && ServerStatusEnum.STOP.getCode().equals(server.getServerStatus());
   }

   private void updateServerStatus(Server server, String serverId) {
      server.setBusinessStatus(BusinessStatusEnum.NORMAL.getCode());
      ServerIdParam serverIdParam = new ServerIdParam();
      serverIdParam.setId(serverId);
      this.renewServer(serverIdParam);
   }

   private void renewServer(ServerIdParam memberServerIdParam) {
      try {
         Server memberServer = (Server)this.getOne((Wrapper<Server>)new LambdaQueryWrapper<Server>().eq(Server::getId, memberServerIdParam.getId()));
         if (memberServer.getServerStatus().equals(ServerStatusEnum.CREATING.getCode())) {
            throw new CommonException("服务器创建中，请等待......", new Object[0]);
         }

         if (memberServer.getServerStatus().equals(ServerStatusEnum.LOCKED.getCode())) {
            throw new CommonException("服务器已锁定，请联系客服", new Object[0]);
         }

         if (memberServer.getServerStatus().equals(ServerStatusEnum.REINSTALLING.getCode())) {
            throw new CommonException("服务器重装系统中，请等待......", new Object[0]);
         }

         BaseMemberMaster memberMaster = this.memberMasterApi.selectByLineId(memberServer.getLineId());
         HostParam hostParam = new HostParam();
         hostParam.setHostName(memberServer.getHostName());
         hostParam.setServerId(memberServer.getId());
         hostParam.setMasterIp(this.getMasterIpForTask(memberMaster));
         hostParam.setApiKey(memberMaster.getApiPassword());
         hostParam.setApiPort(memberMaster.getApiPort());
         log.info("开机请求:{}", JSON.toJSONString(hostParam));
         CloudControllerAdapter adapter = this.cloudControllerRouter.getAdapterByGuardVersion(memberMaster.getGuardVersion());
         CommonResult result = adapter.startHost(hostParam);
         log.info("开机结果:{}", JSON.toJSONString(result));
         if (result.getCode().equals(200) && !"3".equals(memberMaster.getGuardVersion())) {
            this.updateServerStatusAndLog(memberServer.getId(), ServerStatusEnum.RUNNING.getCode());
         }
      } catch (Exception var7) {
         log.error("启动服务器失败", var7);
      }
   }

   @Override
   public CommonResult upgradeHardDisksServer(String orderNo, String serverId, Integer systemDisk, List<Integer> dataDisks, BigDecimal renewPrice) {
      try {
         Server server = (Server)this.getOne((Wrapper<Server>)new LambdaQueryWrapper<Server>().eq(Server::getId, serverId));
         if (null == server) {
            return CommonResult.error("服务器不存在");
         } else {
            this.assertServerNotLocked(server);
            if (Integer.valueOf(1).equals(server.getType())) {
               log.info("资源池服务器升级磁盘，发送请求至上游，订单号：{}，服务器ID：{}", orderNo, serverId);
               String upstreamIdA = resolveResourcePoolUpstreamServerId(server);
               if (StrUtil.isBlank(upstreamIdA)) {
                  return CommonResult.error("上游服务器ID未同步，无法通知上游，请等待资源池同步完成后再试");
               } else {
                  String effectiveOrderNo = resolveResourcePoolOrderNo(orderNo, server);
                  if (StrUtil.isBlank(effectiveOrderNo)) {
                     return CommonResult.error("订单号不能为空，无法通知上游");
                  } else {
                     JSONObject params = new JSONObject();
                     params.put("downstreamOrderNo", effectiveOrderNo);
                     params.put("orderNo", effectiveOrderNo);
                     putResourcePoolUpstreamServerFields(params, server, upstreamIdA);
                     params.put("dataDisks", dataDisks);
                     if (systemDisk != null) {
                        params.put("systemDisk", systemDisk);
                     }

                     if (renewPrice != null) {
                        params.put("renewPrice", renewPrice);
                     }

                     BaseResourcePoolRequest poolRequest = new BaseResourcePoolRequest();
                     poolRequest.setOrderNo(effectiveOrderNo);
                     poolRequest.setMemberNo(server.getMemberNo() != null ? Long.valueOf(server.getMemberNo()) : null);
                     poolRequest.setResourcePoolId(server.getResourcePoolId());
                     poolRequest.setOperationType("UPGRADE_DISK");
                     poolRequest.setAServerId(upstreamIdA);
                     poolRequest.setProductType(ProductTypeEnum.CLOUD_SERVER.getCode());
                     poolRequest.setRequestParams(JSON.toJSONString(params));
                     this.resourcePoolApi.createRequest(poolRequest);
                     log.info("✅ 资源池升级磁盘请求已创建，等待定时任务发送，订单号：{}", effectiveOrderNo);
                     return CommonResult.ok("升级磁盘任务已创建");
                  }
               }
            } else {
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
                  if ("3".equals(StrUtil.trim(memberMaster.getGuardVersion()))) {
                     Integer changedSystemDisk = this.resolveChangedSystemDisk(server.getSystemDisk(), systemDisk);
                     List<Integer> changedDataDisks = this.resolveChangedDataDisks(server.getDataDisks(), dataDisks);
                     if (changedSystemDisk == null && !this.hasChangedDataDisk(changedDataDisks)) {
                        return CommonResult.error("磁盘容量未变化");
                     }

                     hostParam.setOsDisk(changedSystemDisk);
                     hostParam.setDataDisks(changedDataDisks);
                  } else {
                     if (dataDisks != null && !dataDisks.isEmpty()) {
                        hostParam.setHardDisks(dataDisks.get(0));
                     }

                     hostParam.setDataDisks(dataDisks);
                     hostParam.setOsDisk(systemDisk);
                  }

                  AutoTaskAddParam taskParam = new AutoTaskAddParam();
                  taskParam.setMasterId(memberMaster.getId());
                  taskParam.setServerId(server.getId());
                  taskParam.setProductType(1);
                  taskParam.setTaskType(TaskTypeEnum.UPGRADE_HARD_DISKS.getCode());
                  taskParam.setExecuteTime(CommonTimeFormatUtil.formatNowGmt8());
                  taskParam.setTaskStatus(1);
                  taskParam.setNextRetryTime(TimeUtil.toDate(TimeUtil.now()));
                  taskParam.setTaskDesc("升级云服务器磁盘任务");
                  taskParam.setPlatform("B");
                  taskParam.setParams(JSONUtil.toJsonStr(hostParam));
                  taskParam.setRetryCount(0);
                  taskParam.setMaxRetry(3);
                  taskParam.setTimeoutTime(DateUtil.offsetHour(TimeUtil.toDate(TimeUtil.now()), 2));
                  server.setServerStatus(ServerStatusEnum.UPGRADE.getCode());
                  if (systemDisk != null) {
                     server.setSystemDisk(systemDisk);
                  }

                  if (dataDisks != null) {
                     server.setDataDisks(dataDisks);
                  }

                  server.setRenewPrice(renewPrice);
                  this.autoTaskTaskService.add(taskParam);
                  this.updateById(server);

                  try {
                     this.resourcePoolApi
                        .logResourceChange(
                           orderNo != null ? orderNo : server.getOrderNo(),
                           Long.valueOf(server.getMemberNo()),
                           "UPDATE",
                           StrUtil.blankToDefault(server.getUpstreamServerId(), server.getId()),
                           "CLOUD"
                        );
                  } catch (Exception var11) {
                     log.error("记录服务器升级磁盘变更日志失败，服务器 ID: {}", serverId, var11);
                  }

                  return CommonResult.ok("升级磁盘任务已创建");
               }
            }
         }
      } catch (Exception var12) {
         log.error("升级服务器失败", var12);
         return CommonResult.error(var12.getMessage());
      }
   }

   private Integer resolveChangedSystemDisk(Integer currentSystemDisk, Integer targetSystemDisk) {
      if (targetSystemDisk != null && targetSystemDisk > 0) {
         return currentSystemDisk != null && targetSystemDisk <= currentSystemDisk ? null : targetSystemDisk;
      } else {
         return null;
      }
   }

   private List<Integer> resolveChangedDataDisks(List<Integer> currentDataDisks, List<Integer> targetDataDisks) {
      List<Integer> changedDataDisks = new ArrayList<>();
      if (targetDataDisks != null && !targetDataDisks.isEmpty()) {
         List<Integer> current = (List<Integer>)(currentDataDisks != null ? currentDataDisks : new ArrayList<>());

         for (int i = 0; i < targetDataDisks.size(); i++) {
            Integer targetSize = targetDataDisks.get(i);
            Integer currentSize = i < current.size() ? current.get(i) : null;
            if (targetSize == null || targetSize <= 0 || currentSize != null && targetSize <= currentSize) {
               changedDataDisks.add(null);
            } else {
               changedDataDisks.add(targetSize);
            }
         }

         return changedDataDisks;
      } else {
         return changedDataDisks;
      }
   }

   private boolean hasChangedDataDisk(List<Integer> dataDisks) {
      return dataDisks != null && dataDisks.stream().anyMatch(size -> size != null && size > 0);
   }

   @Override
   public CommonResult upgradeBandwidthServer(String orderNo, String serverId, Integer bandwidth, BigDecimal renewPrice) {
      try {
         Server server = (Server)this.getOne((Wrapper<Server>)new LambdaQueryWrapper<Server>().eq(Server::getId, serverId));
         if (null == server) {
            return CommonResult.error("服务器不存在");
         } else {
            this.assertServerNotLocked(server);
            if (Integer.valueOf(1).equals(server.getType())) {
               log.info("资源池服务器升级带宽，发送请求至上游，订单号：{}，服务器ID：{}", orderNo, serverId);
               String upstreamIdA = resolveResourcePoolUpstreamServerId(server);
               if (StrUtil.isBlank(upstreamIdA)) {
                  return CommonResult.error("上游服务器ID未同步，无法通知上游，请等待资源池同步完成后再试");
               } else {
                  String effectiveOrderNo = resolveResourcePoolOrderNo(orderNo, server);
                  if (StrUtil.isBlank(effectiveOrderNo)) {
                     return CommonResult.error("订单号不能为空，无法通知上游");
                  } else {
                     JSONObject params = new JSONObject();
                     params.put("orderNo", effectiveOrderNo);
                     putResourcePoolUpstreamServerFields(params, server, upstreamIdA);
                     params.put("bandwidth", bandwidth);
                     if (renewPrice != null) {
                        params.put("renewPrice", renewPrice);
                     }

                     BaseResourcePoolRequest poolRequest = new BaseResourcePoolRequest();
                     poolRequest.setOrderNo(effectiveOrderNo);
                     poolRequest.setMemberNo(server.getMemberNo() != null ? Long.valueOf(server.getMemberNo()) : null);
                     poolRequest.setResourcePoolId(server.getResourcePoolId());
                     poolRequest.setOperationType("UPGRADE_BANDWIDTH");
                     poolRequest.setAServerId(upstreamIdA);
                     poolRequest.setProductType(ProductTypeEnum.CLOUD_SERVER.getCode());
                     poolRequest.setRequestParams(JSON.toJSONString(params));
                     this.resourcePoolApi.createRequest(poolRequest);
                     log.info("✅ 资源池升级带宽请求已创建，等待定时任务发送，订单号：{}", effectiveOrderNo);
                     return CommonResult.ok("升级带宽任务已创建");
                  }
               }
            } else {
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
                  hostParam.setBandwidth(bandwidth);
                  AutoTaskAddParam taskParam = new AutoTaskAddParam();
                  taskParam.setMasterId(server.getMasterId());
                  taskParam.setServerId(server.getId());
                  taskParam.setProductType(1);
                  taskParam.setTaskType(TaskTypeEnum.UPGRADE_BANDWIDTH.getCode());
                  taskParam.setExecuteTime(CommonTimeFormatUtil.formatNowGmt8());
                  taskParam.setTaskStatus(1);
                  taskParam.setNextRetryTime(TimeUtil.toDate(TimeUtil.now()));
                  taskParam.setTaskDesc("升级云服务器带宽任务");
                  taskParam.setPlatform("B");
                  taskParam.setParams(JSONUtil.toJsonStr(hostParam));
                  taskParam.setRetryCount(0);
                  taskParam.setMaxRetry(3);
                  taskParam.setTimeoutTime(DateUtil.offsetHour(TimeUtil.toDate(TimeUtil.now()), 2));
                  server.setServerStatus(ServerStatusEnum.UPGRADE.getCode());
                  server.setBandwidth(bandwidth);
                  server.setRenewPrice(renewPrice);
                  this.autoTaskTaskService.add(taskParam);
                  this.updateById(server);

                  try {
                     this.resourcePoolApi
                        .logResourceChange(
                           orderNo != null ? orderNo : server.getOrderNo(),
                           Long.valueOf(server.getMemberNo()),
                           "UPDATE",
                           StrUtil.blankToDefault(server.getUpstreamServerId(), server.getId()),
                           "CLOUD"
                        );
                  } catch (Exception var10) {
                     log.error("记录服务器升级带宽变更日志失败，服务器 ID: {}", serverId, var10);
                  }

                  return CommonResult.ok("升级带宽任务已创建");
               }
            }
         }
      } catch (Exception var11) {
         log.error("升级服务器带宽失败", var11);
         return CommonResult.error(var11.getMessage());
      }
   }

   @Override
   public CommonResult upgradeCpuRamServer(String orderNo, String serverId, Integer cpu, Integer ram, BigDecimal renewPrice) {
      try {
         Server server = (Server)this.getOne((Wrapper<Server>)new LambdaQueryWrapper<Server>().eq(Server::getId, serverId));
         if (null == server) {
            return CommonResult.error("服务器不存在");
         } else {
            this.assertServerNotLocked(server);
            if (Integer.valueOf(1).equals(server.getType())) {
               log.info("资源池服务器升级CPU/内存，发送请求至上游，订单号：{}，服务器ID：{}", orderNo, serverId);
               String upstreamIdA = resolveResourcePoolUpstreamServerId(server);
               if (StrUtil.isBlank(upstreamIdA)) {
                  return CommonResult.error("上游服务器ID未同步，无法通知上游，请等待资源池同步完成后再试");
               } else {
                  String effectiveOrderNo = resolveResourcePoolOrderNo(orderNo, server);
                  if (StrUtil.isBlank(effectiveOrderNo)) {
                     return CommonResult.error("订单号不能为空，无法通知上游");
                  } else {
                     JSONObject params = new JSONObject();
                     params.put("orderNo", effectiveOrderNo);
                     putResourcePoolUpstreamServerFields(params, server, upstreamIdA);
                     params.put("cpu", cpu);
                     params.put("ram", ram);
                     if (renewPrice != null) {
                        params.put("renewPrice", renewPrice);
                     }

                     BaseResourcePoolRequest poolRequest = new BaseResourcePoolRequest();
                     poolRequest.setOrderNo(effectiveOrderNo);
                     poolRequest.setMemberNo(server.getMemberNo() != null ? Long.valueOf(server.getMemberNo()) : null);
                     poolRequest.setResourcePoolId(server.getResourcePoolId());
                     poolRequest.setOperationType("UPGRADE_CPU_RAM");
                     poolRequest.setAServerId(upstreamIdA);
                     poolRequest.setProductType(ProductTypeEnum.CLOUD_SERVER.getCode());
                     poolRequest.setRequestParams(JSON.toJSONString(params));
                     this.resourcePoolApi.createRequest(poolRequest);
                     log.info("✅ 资源池升级CPU/内存请求已创建，等待定时任务发送，订单号：{}", effectiveOrderNo);
                     return CommonResult.ok("升级 CPU 和内存任务已创建");
                  }
               }
            } else {
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
                  hostParam.setCpu(cpu);
                  hostParam.setRam(ram);
                  hostParam.setCpuLimit(memberMaster.getCpuLimit());
                  hostParam.setCpuReservePercentage(memberMaster.getCpuReservePercentage());
                  hostParam.setCpuRelativeWeight(memberMaster.getCpuRelativeWeight());
                  hostParam.setMemoryDynamic(memberMaster.getMemoryDynamic());
                  hostParam.setRamStart(memberMaster.getRamStart());
                  hostParam.setMemoryBufferPercentage(memberMaster.getMemoryBufferPercentage());
                  hostParam.setMemoryPriority(memberMaster.getMemoryPriority());
                  AutoTaskAddParam taskParam = new AutoTaskAddParam();
                  taskParam.setMasterId(server.getMasterId());
                  taskParam.setServerId(server.getId());
                  taskParam.setProductType(1);
                  taskParam.setTaskType(TaskTypeEnum.UPGRADE_CPU_RAM.getCode());
                  taskParam.setExecuteTime(CommonTimeFormatUtil.formatNowGmt8());
                  taskParam.setTaskStatus(1);
                  taskParam.setNextRetryTime(TimeUtil.toDate(TimeUtil.now()));
                  taskParam.setTaskDesc("升级云服务器核心任务");
                  taskParam.setPlatform("B");
                  taskParam.setParams(JSONUtil.toJsonStr(hostParam));
                  taskParam.setRetryCount(0);
                  taskParam.setMaxRetry(3);
                  taskParam.setTimeoutTime(DateUtil.offsetHour(TimeUtil.toDate(TimeUtil.now()), 2));
                  server.setServerStatus(ServerStatusEnum.UPGRADE.getCode());
                  server.setCpu(cpu);
                  server.setRam(ram);
                  server.setRenewPrice(renewPrice);
                  this.autoTaskTaskService.add(taskParam);
                  this.updateById(server);

                  try {
                     this.resourcePoolApi
                        .logResourceChange(
                           orderNo != null ? orderNo : server.getOrderNo(),
                           Long.valueOf(server.getMemberNo()),
                           "UPDATE",
                           StrUtil.blankToDefault(server.getUpstreamServerId(), server.getId()),
                           "CLOUD"
                        );
                  } catch (Exception var11) {
                     log.error("记录服务器升级 CPU/内存变更日志失败，服务器 ID: {}", serverId, var11);
                  }

                  return CommonResult.ok("升级 CPU 和内存任务已创建");
               }
            }
         }
      } catch (Exception var12) {
         log.error("升级服务器核心失败", var12);
         return CommonResult.error(var12.getMessage());
      }
   }

   @Override
   public CommonResult upgradeDefenseServer(String orderNo, String serverId, Integer defense, BigDecimal renewPrice) {
      try {
         Server server = (Server)this.getOne((Wrapper<Server>)new LambdaQueryWrapper<Server>().eq(Server::getId, serverId));
         if (null == server) {
            return CommonResult.error("服务器不存在");
         } else {
            this.assertServerNotLocked(server);
            if (Integer.valueOf(1).equals(server.getType())) {
               log.info("资源池服务器升级防御，发送请求至上游，订单号：{}，服务器ID：{}", orderNo, serverId);
               String upstreamIdA = resolveResourcePoolUpstreamServerId(server);
               if (StrUtil.isBlank(upstreamIdA)) {
                  return CommonResult.error("上游服务器ID未同步，无法通知上游，请等待资源池同步完成后再试");
               } else {
                  String effectiveOrderNo = resolveResourcePoolOrderNo(orderNo, server);
                  if (StrUtil.isBlank(effectiveOrderNo)) {
                     return CommonResult.error("订单号不能为空，无法通知上游");
                  } else {
                     JSONObject params = new JSONObject();
                     params.put("orderNo", effectiveOrderNo);
                     putResourcePoolUpstreamServerFields(params, server, upstreamIdA);
                     params.put("defense", defense);
                     if (renewPrice != null) {
                        params.put("renewPrice", renewPrice);
                     }

                     BaseResourcePoolRequest poolRequest = new BaseResourcePoolRequest();
                     poolRequest.setOrderNo(effectiveOrderNo);
                     poolRequest.setMemberNo(server.getMemberNo() != null ? Long.valueOf(server.getMemberNo()) : null);
                     poolRequest.setResourcePoolId(server.getResourcePoolId());
                     poolRequest.setOperationType("UPGRADE_DEFENSE");
                     poolRequest.setAServerId(upstreamIdA);
                     poolRequest.setProductType(ProductTypeEnum.CLOUD_SERVER.getCode());
                     poolRequest.setRequestParams(JSON.toJSONString(params));
                     this.resourcePoolApi.createRequest(poolRequest);
                     log.info("✅ 资源池升级防御请求已创建，等待定时任务发送，订单号：{}", effectiveOrderNo);
                     return CommonResult.ok("升级防御成功");
                  }
               }
            } else {
               server.setDefense(defense);
               server.setRenewPrice(renewPrice);
               boolean updated = this.updateById(server);
               if (!updated) {
                  return CommonResult.error("更新服务器防御配置失败");
               } else {
                  log.info("服务器 [ID:{}] 升级防御成功，防御值：{}，续费价格：{}", new Object[]{serverId, defense, renewPrice});

                  try {
                     this.resourcePoolApi
                        .logResourceChange(
                           orderNo != null ? orderNo : server.getOrderNo(),
                           Long.valueOf(server.getMemberNo()),
                           "UPDATE",
                           StrUtil.blankToDefault(server.getUpstreamServerId(), server.getId()),
                           "CLOUD"
                        );
                  } catch (Exception var10) {
                     log.error("记录服务器升级防御变更日志失败，服务器 ID: {}", serverId, var10);
                  }

                  return CommonResult.ok("升级防御成功");
               }
            }
         }
      } catch (Exception var11) {
         log.error("升级服务器防御失败[ID:{}]", serverId, var11);
         return CommonResult.error("升级防御失败: " + var11.getMessage());
      }
   }

   @Override
   public CommonResult<List<ServerNetworkHistoryResult>> getNetworkHistory(ServerIdParam serverIdParam) {
      try {
         Server server = this.queryEntity(serverIdParam.getId());
         if (this.resourcePoolUpstreamClient.isResourcePoolServer(server)) {
            return (CommonResult<List<ServerNetworkHistoryResult>>)this.resourcePoolUpstreamClient.callOrderServerApi(server, "networkHistory", null);
         } else {
            BaseMemberMaster memberMaster = this.memberMasterApi.selectByMasterId(server.getMasterId());
            CloudControllerAdapter adapter = this.cloudControllerRouter.getAdapterByGuardVersion(memberMaster.getGuardVersion());
            log.info(
               "网络历史数据路由: serverId={}, masterId={}, guardVersion={}, adapter={}, hostName={}",
               new Object[]{server.getId(), server.getMasterId(), memberMaster.getGuardVersion(), adapter.getClass().getName(), server.getHostName()}
            );
            HostParam hostParam = new HostParam();
            hostParam.setHostName(server.getHostName());
            hostParam.setServerId(server.getId());
            hostParam.setMasterIp(this.getMasterIpForTask(memberMaster));
            hostParam.setApiKey(memberMaster.getApiPassword());
            hostParam.setApiPort(memberMaster.getApiPort());
            CommonResult result = adapter.getNetworkHistory(hostParam);
            if (!result.getCode().equals(200)) {
               return CommonResult.error(result.getMsg());
            } else {
               Object data = result.getData();
               if (data == null) {
                  log.warn("网络历史数据为空，serverId={}", server.getId());
                  return CommonResult.data(new ArrayList());
               } else {
                  List<ServerNetworkHistoryResult> historyList = new ArrayList<>();

                  try {
                     String jsonString = JSON.toJSONString(data);
                     log.debug("网络历史数据原始返回: {}", jsonString);
                     String trimmedJson = jsonString.trim();
                     if (trimmedJson.startsWith("[")) {
                        List<Map<String, Object>> dataList = (List<Map<String, Object>>)JSON.parseObject(jsonString, List.class);
                        if (dataList != null && !dataList.isEmpty()) {
                           for (Map<String, Object> item : dataList) {
                              ServerNetworkHistoryResult historyResult = this.convertToNetworkHistoryResult(item);
                              if (historyResult != null) {
                                 historyList.add(historyResult);
                              }
                           }
                        }
                     } else if (trimmedJson.startsWith("{")) {
                        Map<String, Object> singleItem = (Map<String, Object>)JSON.parseObject(jsonString, Map.class);
                        if (singleItem != null) {
                           ServerNetworkHistoryResult historyResult = this.convertToNetworkHistoryResult(singleItem);
                           if (historyResult != null) {
                              historyList.add(historyResult);
                           }
                        }
                     } else {
                        log.warn("网络历史数据格式未知: {}", jsonString);
                     }
                  } catch (Exception var15) {
                     log.error("解析网络历史数据失败，serverId={}", server.getId(), var15);
                     if (data instanceof List) {
                        for (Object itemx : (List)data) {
                           ServerNetworkHistoryResult historyResult = this.convertToNetworkHistoryResult(itemx);
                           if (historyResult != null) {
                              historyList.add(historyResult);
                           }
                        }
                     } else {
                        ServerNetworkHistoryResult historyResult = this.convertToNetworkHistoryResult(data);
                        if (historyResult != null) {
                           historyList.add(historyResult);
                        }
                     }
                  }

                  log.info("网络历史数据转换完成，serverId={}, 记录数={}", server.getId(), historyList.size());
                  return CommonResult.data(historyList);
               }
            }
         }
      } catch (Exception var16) {
         log.error("获取云服务器网络历史数据失败", var16);
         return CommonResult.error("获取网络历史数据失败: " + var16.getMessage());
      }
   }

   private ServerNetworkHistoryResult convertToNetworkHistoryResult(Object item) {
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
            ServerNetworkHistoryResult result = new ServerNetworkHistoryResult();
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

   @Override
   public CommonResult<List<ServerCpuHistoryResult>> getCpuHistory(ServerIdParam serverIdParam) {
      try {
         Server server = this.queryEntity(serverIdParam.getId());
         if (this.resourcePoolUpstreamClient.isResourcePoolServer(server)) {
            return (CommonResult<List<ServerCpuHistoryResult>>)this.resourcePoolUpstreamClient.callOrderServerApi(server, "cpuHistory", null);
         } else {
            BaseMemberMaster memberMaster = this.memberMasterApi.selectByMasterId(server.getMasterId());
            CloudControllerAdapter adapter = this.cloudControllerRouter.getAdapterByGuardVersion(memberMaster.getGuardVersion());
            log.info(
               "CPU历史数据路由: serverId={}, masterId={}, guardVersion={}, adapter={}, hostName={}",
               new Object[]{server.getId(), server.getMasterId(), memberMaster.getGuardVersion(), adapter.getClass().getName(), server.getHostName()}
            );
            HostParam hostParam = new HostParam();
            hostParam.setHostName(server.getHostName());
            hostParam.setServerId(server.getId());
            hostParam.setMasterIp(this.getMasterIpForTask(memberMaster));
            hostParam.setApiKey(memberMaster.getApiPassword());
            hostParam.setApiPort(memberMaster.getApiPort());
            CommonResult result = adapter.getCpuHistory(hostParam);
            if (!result.getCode().equals(200)) {
               return CommonResult.error(result.getMsg());
            } else {
               Object data = result.getData();
               if (data == null) {
                  log.warn("CPU历史数据为空，serverId={}", server.getId());
                  return CommonResult.data(new ArrayList());
               } else {
                  List<ServerCpuHistoryResult> historyList = new ArrayList<>();

                  try {
                     String jsonString = JSON.toJSONString(data);
                     log.debug("CPU历史数据原始返回: {}", jsonString);
                     String trimmedJson = jsonString.trim();
                     if (trimmedJson.startsWith("[")) {
                        List<Map<String, Object>> dataList = (List<Map<String, Object>>)JSON.parseObject(jsonString, List.class);
                        if (dataList != null && !dataList.isEmpty()) {
                           for (Map<String, Object> item : dataList) {
                              ServerCpuHistoryResult historyResult = this.convertToCpuHistoryResult(item);
                              if (historyResult != null) {
                                 historyList.add(historyResult);
                              }
                           }
                        }
                     } else if (trimmedJson.startsWith("{")) {
                        Map<String, Object> singleItem = (Map<String, Object>)JSON.parseObject(jsonString, Map.class);
                        if (singleItem != null) {
                           ServerCpuHistoryResult historyResult = this.convertToCpuHistoryResult(singleItem);
                           if (historyResult != null) {
                              historyList.add(historyResult);
                           }
                        }
                     } else {
                        log.warn("CPU历史数据格式未知: {}", jsonString);
                     }
                  } catch (Exception var15) {
                     log.error("解析CPU历史数据失败，serverId={}", server.getId(), var15);
                     if (data instanceof List) {
                        for (Object itemx : (List)data) {
                           ServerCpuHistoryResult historyResult = this.convertToCpuHistoryResult(itemx);
                           if (historyResult != null) {
                              historyList.add(historyResult);
                           }
                        }
                     } else {
                        ServerCpuHistoryResult historyResult = this.convertToCpuHistoryResult(data);
                        if (historyResult != null) {
                           historyList.add(historyResult);
                        }
                     }
                  }

                  log.info("CPU历史数据转换完成，serverId={}, 记录数={}", server.getId(), historyList.size());
                  return CommonResult.data(historyList);
               }
            }
         }
      } catch (Exception var16) {
         log.error("获取云服务器CPU历史数据失败", var16);
         return CommonResult.error("获取CPU历史数据失败: " + var16.getMessage());
      }
   }

   @Override
   public void updateServerStatusAndLog(String serverId, String serverStatus) {
      Server server = (Server)this.serverMapper.selectById(serverId);
      if (server == null) {
         log.warn("服务器不存在，serverId={}", serverId);
         throw new CommonException("服务器不存在", new Object[0]);
      } else {
         server.setServerStatus(serverStatus);
         this.updateById(server);
         this.saveResourceChangeLogForDownstreamIfNeeded(serverId, "UPDATE");
      }
   }

   private CommonResult createTaskAndUpdateServerStatus(AutoTaskAddParam taskParam, Server server, String serverStatus, String successMessage) {
      return this.createTaskAndUpdateServerStatus(taskParam, server, serverStatus, successMessage, false);
   }

   private CommonResult createTaskAndUpdateServerStatus(
      AutoTaskAddParam taskParam, Server server, String serverStatus, String successMessage, boolean syncDownstream
   ) {
      this.autoTaskTaskService.add(taskParam);
      server.setServerStatus(serverStatus);
      this.updateById(server);
      if (syncDownstream) {
         this.saveResourceChangeLogForDownstreamIfNeeded(server.getId(), "UPDATE");
      }

      return CommonResult.ok(successMessage);
   }

   @Override
   public void saveResourceChangeLogAfterDeleteSuccess(String serverId) {
      this.saveResourceChangeLogForDownstreamIfNeeded(serverId, "DELETE");
   }

   @Override
   public void releaseMacAfterDelete(String serverId) {
      if (!StrUtil.isBlank(serverId)) {
         ((UpdateChainWrapper)((UpdateChainWrapper)((UpdateChainWrapper)this.update().eq("id", serverId)).set("public_mac", null)).set("private_mac", null))
            .update();
      }
   }

   private void saveResourceChangeLogForDownstreamIfNeeded(String serverId, String changeType) {
      Server server = (Server)this.serverMapper.selectById(serverId);
      if (server != null && !StrUtil.isBlank(server.getId())) {
         if (!this.resourcePoolUpstreamClient.isResourcePoolServer(server)) {
            String upstreamId = StrUtil.blankToDefault(server.getUpstreamServerId(), server.getId());
            String memberNo = server.getMemberNo();
            BaseResourceChangeLog changeLog = new BaseResourceChangeLog();
            changeLog.setId(SnowflakeIdGenerator.generateId());
            changeLog.setOrderNo(server.getOrderNo());
            changeLog.setMemberNo(Long.valueOf(memberNo));
            changeLog.setUpstreamServerId(upstreamId);
            changeLog.setPoolSyncTarget("CLOUD");
            changeLog.setChangeType(changeType != null ? changeType : "UPDATE");
            changeLog.setProcessed(0);
            changeLog.setCreateTime(new Date());
            this.resourceChangeLogApi.save(changeLog);
            log.info(
               "✅ 已写入资源变更日志(供下游同步)，changeType={}，订单号：{}，upstreamServerId：{}，poolSyncTarget=CLOUD", new Object[]{changeType, server.getOrderNo(), upstreamId}
            );
         }
      }
   }

   private ServerCpuHistoryResult convertToCpuHistoryResult(Object item) {
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
            ServerCpuHistoryResult result = new ServerCpuHistoryResult();
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

   private static String resolveResourcePoolOrderNo(String orderNo, Server server) {
      if (StrUtil.isNotBlank(orderNo)) {
         return orderNo;
      } else {
         return server != null ? server.getOrderNo() : null;
      }
   }

   private static String resolveResourcePoolUpstreamServerId(Server server) {
      return server == null ? null : StrUtil.blankToDefault(server.getUpstreamServerId(), server.getMasterId());
   }

   private static void putResourcePoolUpstreamServerFields(JSONObject params, Server server, String upstreamIdA) {
      params.put("upstreamServerId", upstreamIdA);
   }

   private boolean isSharedIp(Server server, BaseMemberMaster memberMaster) {
      return server != null && Integer.valueOf(1).equals(server.getShareIp()) || memberMaster != null && Integer.valueOf(1).equals(memberMaster.getShareIp());
   }

   private List<HostIp> buildAttachPublicIps(List<ServerIp> serverIps) {
      List<HostIp> hostIps = new ArrayList<>();
      if (serverIps != null && !serverIps.isEmpty()) {
         for (ServerIp serverIp : serverIps) {
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

   private ServerIp resolvePrimaryPublicServerIp(Server server, BaseMemberMaster memberMaster, List<ServerIp> serverIps) {
      if (serverIps != null && !serverIps.isEmpty()) {
         boolean sharedIp = this.isSharedIp(server, memberMaster);
         ServerIp primaryIp = this.findServerIp(serverIps, sharedIp ? "NAT" : "PUBLIC", true);
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

   private ServerIp resolvePrivateServerIp(List<ServerIp> serverIps) {
      if (serverIps != null && !serverIps.isEmpty()) {
         ServerIp privateIp = this.findServerIp(serverIps, "PRIVATE", true);
         return privateIp != null ? privateIp : this.findServerIp(serverIps, "PRIVATE", false);
      } else {
         return null;
      }
   }

   private ServerIp findServerIp(List<ServerIp> serverIps, String type, boolean mainOnly) {
      for (ServerIp serverIp : serverIps) {
         if (type.equals(serverIp.getType()) && !StrUtil.isBlank(serverIp.getIp()) && (!mainOnly || Integer.valueOf(1).equals(serverIp.getIsMain()))) {
            return serverIp;
         }
      }

      return null;
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
}
