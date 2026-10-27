package com.rxinyun.admin.business.modular.task.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.admin.business.modular.esc.entity.CallbackLog;
import com.rxinyun.admin.business.modular.esc.entity.Server;
import com.rxinyun.admin.business.modular.esc.entity.ServerIp;
import com.rxinyun.admin.business.modular.esc.mapper.ServerIpMapper;
import com.rxinyun.admin.business.modular.esc.mapper.ServerMapper;
import com.rxinyun.admin.business.modular.esc.service.CallbackLogService;
import com.rxinyun.admin.business.modular.task.entity.AutoTask;
import com.rxinyun.admin.business.modular.task.mapper.AutoTaskMapper;
import com.rxinyun.admin.business.modular.task.param.AutoTaskAddParam;
import com.rxinyun.admin.business.modular.task.param.AutoTaskIdParam;
import com.rxinyun.admin.business.modular.task.param.AutoTaskPageParam;
import com.rxinyun.admin.business.modular.task.service.AutoTaskTaskService;
import com.rxinyun.business.api.IndependentApi;
import com.rxinyun.business.core.pojo.BaseIndependent;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.production.api.MemberMasterApi;
import com.rxinyun.production.core.pojo.BaseMemberMaster;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AutoTaskServiceImpl extends ServiceImpl<AutoTaskMapper, AutoTask> implements AutoTaskTaskService {
   private static final Logger log = LoggerFactory.getLogger(AutoTaskServiceImpl.class);
   private static final String SERVER_CALLBACK = "SERVER_CALLBACK";
   @Resource
   private CallbackLogService callbackLogService;
   @Resource
   private MemberMasterApi memberMasterApi;
   @Resource
   private ServerMapper serverMapper;
   @Resource
   private ServerIpMapper serverIpMapper;
   @Resource
   private IndependentApi independentApi;

   @Override
   public Page<AutoTask> page(AutoTaskPageParam pageParam) {
      QueryWrapper<AutoTask> queryWrapper = new QueryWrapper().checkSqlInjection();
      if (StrUtil.isNotBlank(pageParam.getMasterId())) {
         queryWrapper.lambda().eq(AutoTask::getMasterId, pageParam.getMasterId());
      }

      if (StrUtil.isNotBlank(pageParam.getServerId())) {
         queryWrapper.lambda().eq(AutoTask::getServerId, pageParam.getServerId());
      }

      if (StrUtil.isNotBlank(pageParam.getServerIp())) {
         List<String> serverIds = this.listCloudServerIdsByIp(pageParam.getServerIp());
         if (CollUtil.isEmpty(serverIds)) {
            queryWrapper.lambda().eq(AutoTask::getId, "-1");
         } else {
            queryWrapper.lambda().in(AutoTask::getServerId, serverIds);
         }
      }

      if (pageParam.getTaskType() != null) {
         queryWrapper.lambda().eq(AutoTask::getTaskType, pageParam.getTaskType());
      }

      if (pageParam.getTaskStatus() != null) {
         queryWrapper.lambda().eq(AutoTask::getTaskStatus, pageParam.getTaskStatus());
      }

      if (pageParam.getTaskStatusList() != null && !pageParam.getTaskStatusList().isEmpty()) {
         queryWrapper.lambda().in(AutoTask::getTaskStatus, pageParam.getTaskStatusList());
      }

      if (pageParam.getPlatform() != null) {
         queryWrapper.lambda().eq(AutoTask::getPlatform, pageParam.getPlatform());
      }

      if (pageParam.getSearchKey() != null) {
         queryWrapper.lambda().like(AutoTask::getTaskDesc, pageParam.getSearchKey());
      }

      if (pageParam.getSortField() == null || pageParam.getSortOrder() == null) {
         queryWrapper.lambda().orderByDesc(AutoTask::getCreateTime);
      } else if ("ASCEND".equalsIgnoreCase(pageParam.getSortOrder())) {
         queryWrapper.orderByAsc(pageParam.getSortField());
      } else {
         queryWrapper.orderByDesc(pageParam.getSortField());
      }

      Page<AutoTask> page = (Page<AutoTask>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
      this.appendGuardVersion(page.getRecords());
      this.appendServerIp(page.getRecords());
      this.appendCallbackInfo(page.getRecords());
      return page;
   }

   @Override
   public List<AutoTask> queryPendingTaskList() {
      QueryWrapper<AutoTask> queryWrapper = new QueryWrapper().checkSqlInjection();
      queryWrapper.lambda().le(AutoTask::getNextRetryTime, new Date());
      queryWrapper.lambda().in(AutoTask::getTaskStatus, Arrays.asList(1));
      return this.list(queryWrapper);
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void recoverStuckTasks() {
      long thresholdMs = 1800000L;
      Date threshold = new Date(System.currentTimeMillis() - thresholdMs);
      QueryWrapper<AutoTask> wrapper = new QueryWrapper().checkSqlInjection();
      ((LambdaQueryWrapper<AutoTask>)((LambdaQueryWrapper<AutoTask>)wrapper.lambda().eq(AutoTask::getTaskStatus, 2)).isNotNull(AutoTask::getStartTime))
         .lt(AutoTask::getStartTime, threshold);

      for (AutoTask task : this.list(wrapper)) {
         task.setTaskStatus(-1);
         task.setExecuteResult("任务执行超时（超过30分钟未完成）");
         this.updateById(task);
         log.warn("任务[ID:{}]执行超时，已标记为失败", task.getId());
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void add(AutoTaskAddParam autoTaskAddParam) {
      this.validateNoPendingTask(autoTaskAddParam.getServerId());
      AutoTask autoTask = (AutoTask)BeanUtil.toBean(autoTaskAddParam, AutoTask.class);
      this.save(autoTask);
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void addTask(String taskType, String serverId, String params) {
      this.validateNoPendingTask(serverId);
      AutoTask task = new AutoTask();
      task.setTaskType(taskType);
      task.setServerId(serverId);
      task.setParams(params);
      task.setTaskStatus(1);
      task.setCreateTime(new Date());
      task.setRetryCount(0);
      task.setMaxRetry(3);
      if ("CREATE".equals(taskType)) {
         task.setTaskDesc("创建云服务器");
      } else if ("UPGRADE".equals(taskType)) {
         task.setTaskDesc("升级云服务器配置");
      }

      this.save(task);
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public CommonResult<String> retryFailedTask(AutoTaskIdParam autoTaskIdParam) {
      AutoTask task = (AutoTask)this.getById(autoTaskIdParam.getId());
      if (task == null) {
         return CommonResult.error("任务不存在");
      } else {
         this.appendCallbackInfo(Arrays.asList(task));
         if (!this.isRetryableTask(task)) {
            return CommonResult.error("该任务不支持本次操作");
         } else {
            task.setTaskStatus(1);
            task.setRetryCount(0);
            task.setNextRetryTime(new Date());
            task.setStartTime(null);
            task.setEndTime(null);
            task.setExecuteResult("手动重试中");
            this.updateById(task);
            log.info("手动重试任务[ID:{}]成功", autoTaskIdParam.getId());
            return CommonResult.ok("手动重试任务成功");
         }
      }
   }

   private boolean isRetryableTask(AutoTask task) {
      return task != null && (Integer.valueOf(-1).equals(task.getTaskStatus()) || Integer.valueOf(0).equals(task.getCallbackStatus()));
   }

   private void validateNoPendingTask(String serverId) {
      if (!StrUtil.isBlank(serverId)) {
         Long activeTaskCount = this.count(
            (Wrapper<AutoTask>)((LambdaQueryWrapper<AutoTask>)new LambdaQueryWrapper<AutoTask>().eq(AutoTask::getServerId, serverId)).in(AutoTask::getTaskStatus, new Object[]{1, 2})
         );
         if (activeTaskCount != null && activeTaskCount > 0L) {
            throw new CommonException("服务器存在未处理的任务，请等待......", new Object[0]);
         }
      }
   }

   private void appendCallbackInfo(List<AutoTask> tasks) {
      if (!CollUtil.isEmpty(tasks)) {
         Set<String> serverIds = new HashSet<>();
         Set<String> callbackActions = new HashSet<>();
         Date minCreateTime = null;
         Map<String, List<AutoTask>> taskGroup = new HashMap<>();
         Map<String, AutoTask> rkQueueTaskMap = new HashMap<>();

         for (AutoTask task : tasks) {
            Set<String> actions = this.expectedCallbackActions(task.getTaskType());
            if (!CollUtil.isEmpty(actions) && !StrUtil.isBlank(task.getServerId())) {
               serverIds.add(task.getServerId());
               callbackActions.addAll(actions);
               if (task.getCreateTime() != null && (minCreateTime == null || task.getCreateTime().before(minCreateTime))) {
                  minCreateTime = task.getCreateTime();
               }

               for (String action : actions) {
                  taskGroup.computeIfAbsent(this.buildCallbackKey(task.getServerId(), action), k -> new ArrayList<>()).add(task);
               }

               String rkQueueId = this.resolveRkQueueIdFromTask(task);
               if (StrUtil.isNotBlank(rkQueueId)) {
                  rkQueueTaskMap.put(rkQueueId, task);
               }
            }
         }

         if (!serverIds.isEmpty() && !callbackActions.isEmpty()) {
            LambdaQueryWrapper<CallbackLog> callbackQuery = (LambdaQueryWrapper<CallbackLog>)((LambdaQueryWrapper<CallbackLog>)((LambdaQueryWrapper<CallbackLog>)((LambdaQueryWrapper<CallbackLog>)new LambdaQueryWrapper<CallbackLog>()
                        .eq(CallbackLog::getCallbackType, "SERVER_CALLBACK"))
                     .in(CallbackLog::getAction, callbackActions))
                  .and(q -> ((LambdaQueryWrapper<CallbackLog>)((LambdaQueryWrapper<CallbackLog>)q.in(CallbackLog::getServerId, serverIds)).or()).isNull(CallbackLog::getServerId)))
               .orderByAsc(CallbackLog::getCallbackTime);
            if (minCreateTime != null) {
               callbackQuery.ge(CallbackLog::getCallbackTime, DateUtil.offsetDay(minCreateTime, -1));
            }

            List<CallbackLog> logs = this.callbackLogService.list(callbackQuery);
            if (!CollUtil.isEmpty(logs)) {
               Map<String, List<CallbackLog>> logGroup = logs.stream()
                  .collect(Collectors.groupingBy(logx -> this.buildCallbackKey(logx.getServerId(), logx.getAction())));

               for (Entry<String, List<AutoTask>> entry : taskGroup.entrySet()) {
                  List<CallbackLog> logList = logGroup.get(entry.getKey());
                  if (!CollUtil.isEmpty(logList)) {
                     List<AutoTask> taskList = entry.getValue();
                     taskList.sort(Comparator.comparing(AutoTask::getCreateTime, Comparator.nullsLast(Date::compareTo)));
                     logList.sort(Comparator.comparing(CallbackLog::getCallbackTime, Comparator.nullsLast(Date::compareTo)));
                     int logIndex = 0;

                     for (AutoTask taskx : taskList) {
                        while (logIndex < logList.size()) {
                           CallbackLog log = logList.get(logIndex);
                           if (!this.isLogBeforeTask(log, taskx)) {
                              if (!this.isLogOutsideTaskWindow(log, taskx)) {
                                 this.fillTaskCallbackInfo(taskx, log);
                                 logIndex++;
                              }
                              break;
                           }

                           logIndex++;
                        }
                     }
                  }
               }

               this.fillRkCallbackInfoByQueueId(logs, rkQueueTaskMap);
            }
         }
      }
   }

   private void appendGuardVersion(List<AutoTask> tasks) {
      if (!CollUtil.isEmpty(tasks)) {
         Set<String> masterIds = tasks.stream().map(AutoTask::getMasterId).filter(CharSequenceUtil::isNotBlank).collect(Collectors.toSet());
         if (!masterIds.isEmpty()) {
            Map<String, BaseMemberMaster> masterMap = new HashMap<>();

            for (String masterId : masterIds) {
               try {
                  BaseMemberMaster master = this.memberMasterApi.selectByMasterId(masterId);
                  if (master != null) {
                     masterMap.put(masterId, master);
                  }
               } catch (Exception var7) {
                  log.debug("自动化任务列表查询宿主机失败 masterId={}", masterId, var7);
               }
            }

            tasks.forEach(task -> {
               BaseMemberMaster master = masterMap.get(task.getMasterId());
               if (master != null) {
                  task.setGuardVersion(master.getGuardVersion());
                  task.setMasterIp(this.resolveMasterIpForDisplay(master));
                  task.setMasterName(master.getMasterName());
               }
            });
         }
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

   private List<String> listCloudServerIdsByIp(String serverIp) {
      Set<String> serverIds = new HashSet<>();
      List<ServerIp> serverIps = this.serverIpMapper
         .selectList(((LambdaQueryWrapper<ServerIp>)new LambdaQueryWrapper<ServerIp>().like(ServerIp::getIp, serverIp)).select(ServerIp::getServerId));
      if (CollUtil.isNotEmpty(serverIps)) {
         for (ServerIp ip : serverIps) {
            if (ip != null && StrUtil.isNotBlank(ip.getServerId())) {
               serverIds.add(ip.getServerId());
            }
         }
      }

      List<Server> servers = this.serverMapper
         .selectList(
            ((LambdaQueryWrapper<Server>)((LambdaQueryWrapper<Server>)((LambdaQueryWrapper<Server>)new LambdaQueryWrapper<Server>().like(Server::getPublicIp, serverIp)).or())
                  .like(Server::getPrivateIp, serverIp))
               .select(Server::getId)
         );
      if (CollUtil.isNotEmpty(servers)) {
         for (Server server : servers) {
            if (server != null && StrUtil.isNotBlank(server.getId())) {
               serverIds.add(server.getId());
            }
         }
      }

      return new ArrayList<>(serverIds);
   }

   private void appendServerIp(List<AutoTask> tasks) {
      if (!CollUtil.isEmpty(tasks)) {
         Set<String> cloudServerIds = new HashSet<>();
         Set<String> independentServerIds = new HashSet<>();

         for (AutoTask task : tasks) {
            if (!StrUtil.isBlank(task.getServerId())) {
               if (Integer.valueOf(2).equals(task.getProductType())) {
                  independentServerIds.add(task.getServerId());
               } else {
                  cloudServerIds.add(task.getServerId());
               }
            }
         }

         Map<String, String> cloudIpMap = new HashMap<>();
         if (CollUtil.isNotEmpty(cloudServerIds)) {
            List<Server> servers = this.serverMapper.selectBatchIds(cloudServerIds);
            if (CollUtil.isNotEmpty(servers)) {
               for (Server server : servers) {
                  if (server != null && StrUtil.isNotBlank(server.getId())) {
                     cloudIpMap.put(server.getId(), server.getPublicIp());
                  }
               }
            }
         }

         Map<String, String> independentIpMap = new HashMap<>();

         for (String serverId : independentServerIds) {
            BaseIndependent independent = this.independentApi.getById(serverId);
            if (independent != null) {
               independentIpMap.put(serverId, independent.getServerIp());
            }
         }

         tasks.forEach(taskx -> {
            if (!StrUtil.isBlank(taskx.getServerId())) {
               if (Integer.valueOf(2).equals(taskx.getProductType())) {
                  taskx.setServerIp(independentIpMap.get(taskx.getServerId()));
               } else {
                  taskx.setServerIp(cloudIpMap.get(taskx.getServerId()));
               }
            }
         });
      }
   }

   private boolean isLogBeforeTask(CallbackLog log, AutoTask task) {
      return log.getCallbackTime() != null && task.getCreateTime() != null && log.getCallbackTime().before(task.getCreateTime());
   }

   private boolean isLogOutsideTaskWindow(CallbackLog log, AutoTask task) {
      if (log.getCallbackTime() == null) {
         return false;
      } else {
         Date deadline = task.getTimeoutTime();
         if (deadline == null && task.getCreateTime() != null) {
            deadline = DateUtil.offsetHour(task.getCreateTime(), 2);
         }

         return deadline != null && log.getCallbackTime().after(deadline);
      }
   }

   private void fillTaskCallbackInfo(AutoTask task, CallbackLog log) {
      task.setCallbackAction(log.getAction());
      task.setCallbackStatus(log.getStatus());
      task.setCallbackTime(log.getCallbackTime());
      task.setCallbackErrorMsg(log.getErrorMsg());
      task.setCallbackRequestData(log.getRequestData());
      task.setCallbackResponseData(log.getResponseData());
      String callbackInfo = this.buildCallbackInfo(log);
      task.setCallbackInfo(callbackInfo);
      if (!StrUtil.isBlank(callbackInfo)) {
         if (StrUtil.isBlank(task.getExecuteResult())) {
            task.setExecuteResult(callbackInfo);
         } else {
            if (!task.getExecuteResult().contains(callbackInfo)) {
               task.setExecuteResult(task.getExecuteResult() + "\n" + callbackInfo);
            }
         }
      }
   }

   private String buildCallbackInfo(CallbackLog log) {
      List<String> parts = new ArrayList<>();
      parts.add("回调信息");
      parts.add(Integer.valueOf(1).equals(log.getStatus()) ? "成功" : "失败");
      if (StrUtil.isNotBlank(log.getAction())) {
         parts.add(log.getAction());
      }

      if (log.getCallbackTime() != null) {
         parts.add(DateUtil.formatDateTime(log.getCallbackTime()));
      }

      if (StrUtil.isNotBlank(log.getErrorMsg())) {
         parts.add(log.getErrorMsg());
      }

      return parts.stream().filter(CharSequenceUtil::isNotBlank).collect(Collectors.joining(" | "));
   }

   private void fillRkCallbackInfoByQueueId(List<CallbackLog> logs, Map<String, AutoTask> rkQueueTaskMap) {
      if (!CollUtil.isEmpty(logs) && !rkQueueTaskMap.isEmpty()) {
         for (CallbackLog log : logs) {
            if (Objects.equals("rk_hyperv_callback", log.getAction())) {
               String queueId = this.resolveRkQueueIdFromCallback(log);
               if (!StrUtil.isBlank(queueId)) {
                  AutoTask task = rkQueueTaskMap.get(queueId);
                  if (task != null && !this.isLogBeforeTask(log, task) && !this.isLogOutsideTaskWindow(log, task)) {
                     this.fillTaskCallbackInfo(task, log);
                  }
               }
            }
         }
      }
   }

   private String resolveRkQueueIdFromTask(AutoTask task) {
      if (task != null && !StrUtil.isBlank(task.getParams())) {
         try {
            JSONObject params = JSON.parseObject(task.getParams());
            return this.cleanRkCallbackValue(StrUtil.blankToDefault(params.getString("rkQueueId"), params.getString("queueId")));
         } catch (Exception var3) {
            return null;
         }
      } else {
         return null;
      }
   }

   private String resolveRkQueueIdFromCallback(CallbackLog log) {
      if (log != null && !StrUtil.isBlank(log.getRequestData())) {
         try {
            JSONObject requestJson = JSON.parseObject(log.getRequestData());
            String queueId = this.firstNotBlank(
               requestJson.getString("AnsyQueueId"),
               requestJson.getString("AsyncQueueId"),
               requestJson.getString("QueueID"),
               requestJson.getString("QueueId"),
               requestJson.getString("_QueueId"),
               requestJson.getString("queueId")
            );
            if (StrUtil.isNotBlank(queueId)) {
               return this.cleanRkCallbackValue(queueId);
            } else {
               String executeResult = requestJson.getString("_ExecuteResult");
               if (StrUtil.isBlank(executeResult)) {
                  return null;
               } else {
                  JSONObject executeJson = JSON.parseObject(this.cleanRkCallbackValue(executeResult));
                  return this.cleanRkCallbackValue(
                     this.firstNotBlank(
                        executeJson.getString("AnsyQueueId"),
                        executeJson.getString("AsyncQueueId"),
                        executeJson.getString("QueueID"),
                        executeJson.getString("QueueId"),
                        executeJson.getString("_QueueId"),
                        executeJson.getString("queueId")
                     )
                  );
               }
            }
         } catch (Exception var6) {
            return null;
         }
      } else {
         return null;
      }
   }

   private String cleanRkCallbackValue(String value) {
      if (value == null) {
         return null;
      } else {
         String cleaned = value.trim();

         while (cleaned.length() >= 2 && cleaned.startsWith("\"") && cleaned.endsWith("\"")) {
            cleaned = cleaned.substring(1, cleaned.length() - 1).trim();
         }

         return cleaned;
      }
   }

   private String firstNotBlank(String... values) {
      if (values == null) {
         return null;
      } else {
         for (String value : values) {
            if (StrUtil.isNotBlank(value)) {
               return value;
            }
         }

         return null;
      }
   }

   private Set<String> expectedCallbackActions(String taskType) {
      Set<String> actions = new HashSet<>();
      if (Objects.equals("CREATE", taskType) || Objects.equals("REBUILD", taskType)) {
         actions.add("create_vps");
      }

      if (Objects.equals("UPGRADE_BANDWIDTH", taskType) || Objects.equals("UPGRADE_CPU_RAM", taskType) || Objects.equals("UPGRADE_HARD_DISKS", taskType)) {
         actions.add("update_vps");
      }

      if (Objects.equals("REINSTALL", taskType)) {
         actions.add("reinstall");
      }

      if (Objects.equals("DELETE", taskType)) {
         actions.add("remove_vps");
      }

      if (!actions.isEmpty()
         || Objects.equals("START", taskType)
         || Objects.equals("STOP", taskType)
         || Objects.equals("RESTART", taskType)
         || Objects.equals("UPDATE_PASSWORD", taskType)
         || Objects.equals("LOCK", taskType)
         || Objects.equals("UNLOCK", taskType)
         || Objects.equals("POWER", taskType)
         || Objects.equals("ADD_IP", taskType)
         || Objects.equals("RESET_IP", taskType)) {
         actions.add("rk_hyperv_callback");
      }

      return actions;
   }

   private String buildCallbackKey(String serverId, String action) {
      return StrUtil.blankToDefault(serverId, "") + "#" + StrUtil.blankToDefault(action, "");
   }
}
