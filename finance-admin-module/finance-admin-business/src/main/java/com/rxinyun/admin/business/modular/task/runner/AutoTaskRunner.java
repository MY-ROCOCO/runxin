package com.rxinyun.admin.business.modular.task.runner;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.UpdateChainWrapper;
import com.rxinyun.admin.business.modular.esc.entity.Server;
import com.rxinyun.admin.business.modular.esc.entity.ServerIp;
import com.rxinyun.admin.business.modular.esc.param.ServerIpAddParam;
import com.rxinyun.admin.business.modular.esc.service.HostStatsRefreshService;
import com.rxinyun.admin.business.modular.esc.service.ServerIpService;
import com.rxinyun.admin.business.modular.esc.service.ServerService;
import com.rxinyun.admin.business.modular.task.entity.AutoTask;
import com.rxinyun.admin.business.modular.task.entity.TaskExecutionLog;
import com.rxinyun.admin.business.modular.task.service.AutoTaskTaskService;
import com.rxinyun.admin.business.modular.task.service.TaskExecutionLogService;
import com.rxinyun.business.core.enums.TaskTypeEnum;
import com.rxinyun.common.controller.CloudControllerAdapter;
import com.rxinyun.common.controller.CloudControllerRouter;
import com.rxinyun.common.enums.NotificationTypeEnum;
import com.rxinyun.common.enums.business.BusinessStatusEnum;
import com.rxinyun.common.enums.business.ServerStatusEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.common.qzsystem.entity.HostIp;
import com.rxinyun.common.qzsystem.entity.HostParam;
import com.rxinyun.common.qzsystem.entity.MfcCreateResult;
import com.rxinyun.common.qzsystem.entity.MfcIpItem;
import com.rxinyun.common.timer.CommonTimerTaskRunner;
import com.rxinyun.common.util.MacAddressGenerator;
import com.rxinyun.dev.api.ToolsNotificationApi;
import com.rxinyun.production.api.MemberLocalLineApi;
import com.rxinyun.production.api.MemberMasterApi;
import com.rxinyun.production.api.MemberNatNetworkApi;
import com.rxinyun.production.api.MemberPrivateNetworkApi;
import com.rxinyun.production.api.MemberPublicNetworkApi;
import com.rxinyun.production.api.MemberSystemVersionApi;
import com.rxinyun.production.core.pojo.BaseMemberLocalLine;
import com.rxinyun.production.core.pojo.BaseMemberMaster;
import com.rxinyun.production.core.pojo.BaseMemberNatNetwork;
import com.rxinyun.production.core.pojo.BaseMemberPrivateNetwork;
import com.rxinyun.production.core.pojo.BaseMemberPublicNetwork;
import com.rxinyun.production.core.pojo.BaseMemberSystemVersion;
import jakarta.annotation.Resource;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.StringJoiner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AutoTaskRunner implements CommonTimerTaskRunner {
   private static final Logger log = LoggerFactory.getLogger(AutoTaskRunner.class);
   @Resource
   private AutoTaskTaskService orderAsyncTaskService;
   @Resource
   private ServerService serverService;
   @Resource
   private MemberMasterApi memberMasterApi;
   @Resource
   private MemberLocalLineApi memberLocalLineApi;
   @Resource
   private MemberSystemVersionApi memberSystemVersionApi;
   @Resource
   private MemberPrivateNetworkApi memberPrivateNetworkApi;
   @Resource
   private MemberPublicNetworkApi memberPublicNetworkApi;
   @Resource
   private MemberNatNetworkApi memberNatNetworkApi;
   @Resource
   private TaskExecutionLogService taskExecutionLogService;
   @Resource
   private ServerIpService serverIpService;
   @Resource
   private RedisTemplate<String, Object> redisTemplate;
   @Autowired
   private ToolsNotificationApi toolsNotificationApi;
   @Resource
   private CloudControllerRouter cloudControllerRouter;
   @Resource
   private HostStatsRefreshService hostStatsRefreshService;

   public void action() {
      this.orderAsyncTaskService.recoverStuckTasks();
      List<AutoTask> tasks = this.orderAsyncTaskService.queryPendingTaskList();
      if (tasks.isEmpty()) {
         log.debug("没有待处理的任务");
      } else {
         log.info("开始执行待处理任务，数量：{}", tasks.size());
         tasks.forEach(this::processTask);
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   protected void processTask(AutoTask task) {
      TaskExecutionLog executionLog = new TaskExecutionLog();
      executionLog.setTaskId(task.getId());
      executionLog.setServerId(task.getServerId());
      executionLog.setTaskType(task.getTaskType());
      executionLog.setStartTime(new Date());

      try {
         task.setTaskStatus(2);
         task.setStartTime(new Date());
         this.attachQueueIdToTaskParams(task);
         this.orderAsyncTaskService.updateById(task);
         TaskTypeEnum taskTypeEnum = TaskTypeEnum.getByCode(task.getTaskType());
         if (taskTypeEnum == null) {
            log.error("未知的任务类型: {}", task.getTaskType());
            throw new IllegalArgumentException("未知的任务类型: " + task.getTaskType());
         }

         switch (taskTypeEnum) {
            case CREATE:
               this.processCreateTask(task);
               break;
            case UPGRADE_CPU_RAM:
               this.processUpgradeCpuRamTask(task);
               break;
            case UPGRADE_BANDWIDTH:
               this.processUpgradeBandwidthTask(task);
               break;
            case UPGRADE_HARD_DISKS:
               this.processUpgradeHardDisksTask(task);
               break;
            case RENEW:
               this.processRenewTask(task);
               break;
            case START:
               this.processStartTask(task);
               break;
            case STOP:
               this.processStopTask(task);
               break;
            case DELETE:
               this.processDeleteTask(task);
               break;
            case RESTART:
               this.processRestartTask(task);
               break;
            case REINSTALL:
               this.processReinstallTask(task);
               break;
            case REBUILD:
               this.processRebuildTask(task);
               break;
            case UPDATE_PASSWORD:
               this.processUpdatePasswordTask(task);
               break;
            case LOCK:
               this.processLockTask(task);
               break;
            case UNLOCK:
               this.processUnlockTask(task);
               break;
            case DISCONNECT:
               this.processDisconnectTask(task);
               break;
            case RECONNECT:
               this.processReconnectTask(task);
               break;
            case POWER:
               this.processPowerTask(task);
               break;
            case ADD_IP:
               this.processAddIpTask(task);
               break;
            case RESET_IP:
               this.processResetIpTask(task);
               break;
            default:
               log.warn("未知任务类型: {}", task.getTaskType());
               throw new IllegalArgumentException("未知任务类型: " + task.getTaskType());
         }

         task.setTaskStatus(3);
         task.setEndTime(new Date());
         this.orderAsyncTaskService.updateById(task);
         log.info("任务处理完成[ID:{}]", task.getId());
         executionLog.setStatus("SUCCESS");
         executionLog.setResultDetail(task.getExecuteResult());
         this.releaseDistributedLock(task);
      } catch (Throwable var7) {
         log.error("处理任务失败[ID:{}]", task.getId(), var7);
         this.handleTaskFailure(task, var7);
         executionLog.setStatus("FAILED");
         executionLog.setErrorMessage(this.getExceptionMessage(var7));
         this.releaseDistributedLock(task);
      } finally {
         executionLog.setEndTime(new Date());
         if (executionLog.getStartTime() != null && executionLog.getEndTime() != null) {
            executionLog.setDuration(executionLog.getEndTime().getTime() - executionLog.getStartTime().getTime());
         }

         this.taskExecutionLogService.save(executionLog);
      }
   }

   private void attachQueueIdToTaskParams(AutoTask task) {
      if (task != null && task.getId() != null && task.getParams() != null) {
         try {
            JSONObject params = JSON.parseObject(task.getParams());
            if (params != null && params.containsKey("serverId")) {
               params.put("queueId", task.getId());
               task.setParams(params.toJSONString());
            }
         } catch (Exception var3) {
         }
      }
   }

   private void rememberRkQueueId(AutoTask task, CommonResult result) {
      String rkQueueId = this.extractRkQueueId(result);
      if (task != null && rkQueueId != null && !rkQueueId.trim().isEmpty()) {
         try {
            JSONObject params = task.getParams() != null ? JSON.parseObject(task.getParams()) : new JSONObject();
            params.put("rkQueueId", rkQueueId.trim());
            task.setParams(params.toJSONString());
            this.orderAsyncTaskService.updateById(task);
            log.info("RkHyper-v异步队列ID已绑定任务，taskId={}, rkQueueId={}, params={}", new Object[]{task.getId(), rkQueueId, task.getParams()});
         } catch (Exception var5) {
            log.warn("RkHyper-v异步队列ID写入任务参数失败，taskId={}, rkQueueId={}, e={}", new Object[]{task.getId(), rkQueueId, var5.getMessage()});
         }
      } else {
         log.warn("RkHyper-v异步队列ID未提取到，taskId={}, result={}", task != null ? task.getId() : null, JSON.toJSONString(result));
      }
   }

   private String buildRkWaitingMessage(String actionName, AutoTask task) {
      String rkQueueId = this.resolveRkQueueIdFromTask(task);
      return rkQueueId != null && !rkQueueId.trim().isEmpty()
         ? "RkHyper-v" + actionName + "命令已发送，等待受控回调，队列ID：" + rkQueueId
         : "RkHyper-v" + actionName + "命令已发送，等待受控回调";
   }

   private String resolveRkQueueIdFromTask(AutoTask task) {
      if (task != null && task.getParams() != null) {
         try {
            JSONObject params = JSON.parseObject(task.getParams());
            return this.cleanRkQueueId(params.getString("rkQueueId"));
         } catch (Exception var3) {
            return null;
         }
      } else {
         return null;
      }
   }

   private String extractRkQueueId(CommonResult result) {
      if (result != null && result.getData() != null) {
         try {
            Object dataObj = result.getData();
            JSONObject json;
            if (dataObj instanceof JSONObject) {
               json = (JSONObject)dataObj;
            } else if (dataObj instanceof Map) {
               json = new JSONObject((Map)dataObj);
            } else {
               json = JSON.parseObject(String.valueOf(dataObj));
            }

            String queueId = this.firstNotBlank(
               json.getString("AnsyQueueId"),
               json.getString("AsyncQueueId"),
               json.getString("QueueID"),
               json.getString("QueueId"),
               json.getString("_QueueId"),
               json.getString("queueId")
            );
            JSONObject data = null;
            Object nestedDataObj = json.get("data");
            if (nestedDataObj instanceof JSONObject) {
               data = (JSONObject)nestedDataObj;
            } else if (nestedDataObj instanceof Map) {
               data = new JSONObject((Map)nestedDataObj);
            } else if (nestedDataObj != null && String.valueOf(nestedDataObj).trim().startsWith("{")) {
               data = JSON.parseObject(String.valueOf(nestedDataObj));
            }

            if (queueId == null && data != null) {
               queueId = this.firstNotBlank(
                  data.getString("AnsyQueueId"),
                  data.getString("AsyncQueueId"),
                  data.getString("QueueID"),
                  data.getString("QueueId"),
                  data.getString("_QueueId"),
                  data.getString("queueId")
               );
            }

            String cleaned = this.cleanRkQueueId(queueId);
            log.info(
               "RkHyper-v异步队列ID提取结果，dataClass={}, queueId={}, data={}",
               new Object[]{result.getData().getClass().getName(), cleaned, JSON.toJSONString(result.getData())}
            );
            return cleaned;
         } catch (Exception var8) {
            log.warn("RkHyper-v异步队列ID提取异常，result={}", JSON.toJSONString(result), var8);
            return null;
         }
      } else {
         log.warn("RkHyper-v异步队列ID提取失败，result或data为空，result={}", JSON.toJSONString(result));
         return null;
      }
   }

   private String cleanRkQueueId(String value) {
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
            if (value != null && !value.trim().isEmpty()) {
               return value;
            }
         }

         return null;
      }
   }

   private void processCreateTask(AutoTask task) {
      log.info("处理创建服务器任务[ID:{}]", task.getId());
      List<String> allocatedPublicNetworkIds = new ArrayList<>();
      String allocatedNatNetworkId = null;
      String allocatedPrivateNetworkId = null;
      String serverId = null;

      try {
         JSONObject taskParams = JSON.parseObject(task.getParams());
         serverId = taskParams.getString("serverId");
         int ipNum = taskParams.getIntValue("ipNum");
         if (ipNum <= 0) {
            ipNum = 1;
         }

         Server server = (Server)this.serverService.getById(serverId);
         if (server == null) {
            log.error("服务器不存在[ID:{}]", serverId);
            throw new RuntimeException("服务器不存在: " + serverId);
         } else {
            this.rollbackNetworkAllocation(this.readAllocatedPublicNetworkIds(taskParams), null, null);
            this.clearAllocatedPublicNetworkIds(task);

            try {
               this.serverIpService.deleteByServerId(serverId);
            } catch (Exception var21) {
               log.warn("创建前清理历史服务器IP绑定失败，继续尝试创建，serverId={}", serverId, var21);
            }

            BaseMemberMaster memberMaster = this.memberMasterApi.selectByLineId(server.getLineId());
            if (memberMaster == null) {
               log.error("宿主机不存在[ID:{}]", server.getLineId());
               throw new RuntimeException("宿主机不存在: " + server.getLineId());
            } else {
               BaseMemberSystemVersion memberOsVersion = this.memberSystemVersionApi.selectByOsVersionId(server.getSystemId());
               this.validateOsVersionControlledType(memberOsVersion, memberMaster.getGuardVersion(), server.getSystemId());
               server.setMasterId(memberMaster.getId());
               server.setImagePath(memberMaster.getMirrorPath());
               server.setDataDisksMaxIops(memberMaster.getDisksMaxiops());
               server.setSystemMaxIops(memberMaster.getOsMaxiops());
               server.setCpuLimit(memberMaster.getCpuLimit());
               server.setShareIp(memberMaster.getShareIp());
               this.prepareServerMacForCreate(server, Integer.valueOf(1).equals(memberMaster.getShareIp()));
               task.setMasterId(server.getMasterId());
               this.orderAsyncTaskService.updateById(task);
               HostParam hostParam = new HostParam();
               BeanUtils.copyProperties(server, hostParam);
               hostParam.setIpNum(ipNum);
               if (!"2".equals(memberMaster.getGuardVersion())) {
                  if (memberMaster.getShareIp() != 1) {
                     allocatedPublicNetworkIds = this.setPublicNetworkInformation(hostParam, server, ipNum, allocatedPublicNetworkIds, task);
                  } else {
                     BaseMemberNatNetwork natNetwork = this.setNatNetworkInformation(hostParam, server);
                     allocatedNatNetworkId = natNetwork.getId();
                     server.setPublicIp(memberMaster.getIp());
                  }

                  BaseMemberPrivateNetwork privateNetwork = this.setPrivateNetworkInformation(hostParam, server);
                  allocatedPrivateNetworkId = privateNetwork.getId();
               } else {
                  server.setPublicIp("");
                  server.setPublicGateway("");
                  server.setPublicNetmask("");
               }

               log.info("Saved memberServer with ID: {}", server.getId());
               log.info("Saved Server: {}", server);
               this.serverService.updateById(server);
               hostParam.setOsClass(server.getSystemClass());
               hostParam.setServerId(server.getId());
               hostParam.setMasterIp(memberMaster.getIp());
               hostParam.setApiKey(memberMaster.getApiPassword());
               hostParam.setApiPort(memberMaster.getApiPort());
               hostParam.setOsPath(memberMaster.getOsPath());
               hostParam.setDisksPath(memberMaster.getDisksPath());
               hostParam.setVmConfigPath(memberMaster.getVmConfigPath());
               hostParam.setPublicSwitchName(memberMaster.getPublicSwitchName());
               hostParam.setNatSwitchName(memberMaster.getNatSwitchName());
               hostParam.setPrivateSwitchName(memberMaster.getPrivateSwitchName());
               hostParam.setDisksMaxIops(memberMaster.getDisksMaxiops());
               hostParam.setMinimumIops(memberMaster.getMinimumIops());
               hostParam.setMaximumIops(memberMaster.getMaximumIops());
               hostParam.setOsDisk(server.getSystemDisk());
               hostParam.setOsMaxIops(memberMaster.getOsMaxiops());
               hostParam.setCpuLimit(memberMaster.getCpuLimit());
               hostParam.setCpuReservePercentage(memberMaster.getCpuReservePercentage());
               hostParam.setCpuRelativeWeight(memberMaster.getCpuRelativeWeight());
               hostParam.setMemoryDynamic(memberMaster.getMemoryDynamic());
               hostParam.setMemoryBufferPercentage(memberMaster.getMemoryBufferPercentage());
               hostParam.setMemoryPriority(memberMaster.getMemoryPriority());
               hostParam.setRamStart(memberMaster.getRamStart());
               hostParam.setRebuildFlag(0);
               hostParam.setShareIp(memberMaster.getShareIp());
               BaseMemberLocalLine localLine = this.memberLocalLineApi.selectByLineId(server.getLineId());
               if (localLine != null) {
                  hostParam.setAutoDns(localLine.getAutoDns());
               }

               if (server.getDataDisks() != null && !server.getDataDisks().isEmpty()) {
                  if (server.getDataDisks().size() == 1) {
                     hostParam.setHardDisks(server.getDataDisks().get(0));
                  } else {
                     hostParam.setData1DiskSize(server.getDataDisks().get(0));
                     hostParam.setData2DiskSize(server.getDataDisks().size() >= 2 ? server.getDataDisks().get(1) : null);
                     hostParam.setData3DiskSize(server.getDataDisks().size() >= 3 ? server.getDataDisks().get(2) : null);
                  }
               }

               hostParam.setSystemPassword(server.getSystemPassword());
               if ("2".equals(memberMaster.getGuardVersion())) {
                  hostParam.setMfcNodeId(memberMaster.getMfcNodeId());
                  hostParam.setMfcLoginAccount(memberMaster.getMfcLoginAccount());
                  hostParam.setMfcLoginPassword(memberMaster.getMfcLoginPassword());
                  hostParam.setPort(server.getPort());
                  if (memberOsVersion != null && memberOsVersion.getMfcSystemId() != null) {
                     hostParam.setMfcSystemId(memberOsVersion.getMfcSystemId());
                  }
               }

               log.info("调用受控创建服务器请求参数：hostParam：{}", JSON.toJSONString(hostParam));
               CloudControllerAdapter adapter = this.cloudControllerRouter.getAdapterByGuardVersion(memberMaster.getGuardVersion());
               log.info(
                  "版本路由(创建): serverId={}, masterId={}, guardVersion={}", new Object[]{server.getId(), memberMaster.getId(), memberMaster.getGuardVersion()}
               );
               CommonResult openResult = adapter.openHost(hostParam);
               if (openResult != null && Integer.valueOf(200).equals(openResult.getCode())) {
                  if ("2".equals(memberMaster.getGuardVersion()) && openResult.getData() != null && openResult.getData() instanceof MfcCreateResult) {
                     MfcCreateResult mfc = (MfcCreateResult)openResult.getData();
                     server.setMfcCloudId(mfc.getInstanceId());
                     server.setPublicIp(mfc.getMainip() != null ? mfc.getMainip() : "");
                     server.setPublicGateway(mfc.getGateway() != null ? mfc.getGateway() : "");
                     server.setPublicNetmask(mfc.getSubnetMask() != null ? mfc.getSubnetMask() : "");
                     this.serverService.updateById(server);
                     if (mfc.getIpList() != null && !mfc.getIpList().isEmpty()) {
                        for (int i = 0; i < mfc.getIpList().size(); i++) {
                           MfcIpItem item = (MfcIpItem)mfc.getIpList().get(i);
                           if (item != null && item.getIpaddress() != null) {
                              ServerIpAddParam ipParam = new ServerIpAddParam();
                              ipParam.setServerId(server.getId());
                              ipParam.setIp(item.getIpaddress());
                              ipParam.setGateway(item.getGateway() != null ? item.getGateway() : "");
                              ipParam.setMask(item.getSubnetMask() != null ? item.getSubnetMask() : "");
                              ipParam.setType("PUBLIC");
                              ipParam.setIsMain(i == 0 ? 1 : 0);
                              ipParam.setVlanId(0);
                              this.serverIpService.add(ipParam);
                           }
                        }
                     }

                     String privateIp = "192.168.1.2";
                     String privateGateway = "192.168.1.1";
                     String privateMask = "255.255.255.0";
                     ServerIpAddParam privateIpParam = new ServerIpAddParam();
                     privateIpParam.setServerId(server.getId());
                     privateIpParam.setIp(privateIp);
                     privateIpParam.setGateway(privateGateway);
                     privateIpParam.setMask(privateMask);
                     privateIpParam.setType("PRIVATE");
                     privateIpParam.setIsMain(0);
                     privateIpParam.setVlanId(0);
                     this.serverIpService.add(privateIpParam);
                     server.setPrivateIp(privateIp);
                     server.setPrivateNetmask(privateMask);
                     server.setServerStatus(ServerStatusEnum.RUNNING.getCode());
                     this.serverService.updateById(server);
                  }

                  task.setMasterId(server.getMasterId());
                  task.setExecuteResult("服务器创建操作申请成功");
                  log.info("服务器创建成功[ID:{}], 服务器名称:{}", task.getId(), hostParam.getHostName());
                  if ("3".equals(memberMaster.getGuardVersion())) {
                     this.rememberRkQueueId(task, openResult);
                     task.setExecuteResult(this.buildRkWaitingMessage("创建", task));
                     log.info("RkHyper-v创建命令已发送，等待回调后更新云服务器状态，serverId={}", server.getId());
                  } else if ("2".equals(memberMaster.getGuardVersion())) {
                     Map<String, Object> templateParams = new HashMap<>();
                     templateParams.put("serverName", server.getHostName());
                     this.toolsNotificationApi
                        .sendMemberNotificationBoth(
                           NotificationTypeEnum.MEMBER_CLOUD_SERVER_ACTIVE.getCode(), Long.valueOf(server.getMemberNo()), templateParams
                        );
                     this.hostStatsRefreshService.refreshByMasterId(server.getMasterId());
                  } else {
                     log.info("轻舟创建命令已发送，等待回调后发送开通成功通知，serverId={}", server.getId());
                  }
               } else {
                  throw new RuntimeException(openResult != null && openResult.getMsg() != null ? openResult.getMsg() : "受控创建失败");
               }
            }
         }
      } catch (Exception var22) {
         log.error("创建服务器失败[ID:{}], 错误详情:", task.getId(), var22);
         this.rollbackNetworkAllocation(allocatedPublicNetworkIds, allocatedNatNetworkId, allocatedPrivateNetworkId);
         if (serverId != null) {
            try {
               this.serverIpService.deleteByServerId(serverId);
            } catch (Exception var20) {
               log.error("删除服务器IP记录失败: ", var20);
            }
         }

         throw new RuntimeException("创建服务器失败: " + var22.getMessage());
      }
   }

   private List<String> readAllocatedPublicNetworkIds(JSONObject taskParams) {
      if (taskParams != null && taskParams.containsKey("allocatedPublicNetworkIds")) {
         List<String> ids = new ArrayList<>();
         Object raw = taskParams.get("allocatedPublicNetworkIds");
         if (raw instanceof List) {
            for (Object item : (List)raw) {
               if (item != null && StrUtil.isNotBlank(String.valueOf(item))) {
                  ids.add(String.valueOf(item));
               }
            }
         }

         return ids;
      } else {
         return new ArrayList<>();
      }
   }

   private void rememberAllocatedPublicNetworkIds(AutoTask task, List<String> allocatedIds) {
      if (task != null && allocatedIds != null && !allocatedIds.isEmpty()) {
         try {
            JSONObject params = task.getParams() != null ? JSON.parseObject(task.getParams()) : new JSONObject();
            params.put("allocatedPublicNetworkIds", allocatedIds);
            task.setParams(params.toJSONString());
            this.orderAsyncTaskService.updateById(task);
         } catch (Exception var4) {
            log.warn("记录已分配公网IP失败，taskId={}, ids={}", new Object[]{task.getId(), allocatedIds, var4});
         }
      }
   }

   private void clearAllocatedPublicNetworkIds(AutoTask task) {
      if (task != null && !StrUtil.isBlank(task.getParams())) {
         try {
            JSONObject params = JSON.parseObject(task.getParams());
            if (params == null || !params.containsKey("allocatedPublicNetworkIds")) {
               return;
            }

            params.remove("allocatedPublicNetworkIds");
            task.setParams(params.toJSONString());
            this.orderAsyncTaskService.updateById(task);
         } catch (Exception var3) {
            log.warn("清理已分配公网IP记录失败，taskId={}", task != null ? task.getId() : null, var3);
         }
      }
   }

   private void rollbackNetworkAllocation(List<String> allocatedPublicNetworkIds, String allocatedNatNetworkId, String allocatedPrivateNetworkId) {
      try {
         if (allocatedPublicNetworkIds != null) {
            for (String allocatedPublicNetworkId : allocatedPublicNetworkIds) {
               if (allocatedPublicNetworkId != null) {
                  try {
                     this.memberPublicNetworkApi.rollbackStatus(allocatedPublicNetworkId);
                     log.info("已回滚公网IP地址 ID: {}", allocatedPublicNetworkId);
                  } catch (Exception var9) {
                     log.error("回滚公网IP失败 ID: {}", allocatedPublicNetworkId, var9);
                  }
               }
            }
         }

         if (allocatedNatNetworkId != null) {
            try {
               this.memberNatNetworkApi.rollbackStatus(allocatedNatNetworkId);
               log.info("已回滚Nat网络地址 ID: {}", allocatedNatNetworkId);
            } catch (Exception var8) {
               log.error("回滚Nat网络失败 ID: {}", allocatedNatNetworkId, var8);
            }
         }

         if (allocatedPrivateNetworkId != null) {
            try {
               this.memberPrivateNetworkApi.rollbackStatus(allocatedPrivateNetworkId);
               log.info("已回滚私网IP地址 ID: {}", allocatedPrivateNetworkId);
            } catch (Exception var7) {
               log.error("回滚私网IP失败 ID: {}", allocatedPrivateNetworkId, var7);
            }
         }
      } catch (Exception var10) {
         log.error("回滚网络分配时发生错误: ", var10);
      }
   }

   private List<String> setPublicNetworkInformation(HostParam hostParam, Server server, int ipNum, List<String> allocatedIds, AutoTask task) {
      int totalIpNum = ipNum > 0 ? ipNum : 1;
      List<BaseMemberPublicNetwork> publicNetworks = new ArrayList<>();

      for (int i = 0; i < totalIpNum; i++) {
         BaseMemberPublicNetwork publicNetwork = this.memberPublicNetworkApi.selectByMaster(server.getMasterId(), server.getLineId());
         log.info("创建云服务器，公网网络信息[{}]:{}", i, JSON.toJSONString(publicNetwork));
         if (ObjectUtil.isNull(publicNetwork)) {
            throw new CommonException("创建云服务器失败，公网网络信息不存在", new Object[0]);
         }

         publicNetworks.add(publicNetwork);
         allocatedIds.add(publicNetwork.getId());
         this.rememberAllocatedPublicNetworkIds(task, allocatedIds);
      }

      int mainIndex = publicNetworks.size() == 1 ? 0 : new Random().nextInt(publicNetworks.size());
      BaseMemberPublicNetwork mainNetwork = publicNetworks.get(mainIndex);
      server.setPublicIp(mainNetwork.getIp());
      server.setPublicNetmask(mainNetwork.getMask());
      String publicMac = this.requireServerPublicMac(server);
      server.setPublicGateway(mainNetwork.getGateway());
      hostParam.setPublicIp(mainNetwork.getIp());
      hostParam.setPublicGateway(mainNetwork.getGateway());
      hostParam.setPublicNetmask(mainNetwork.getMask());
      hostParam.setPublicMac(publicMac);
      hostParam.setPublicVlanId(mainNetwork.getVlanId());
      List<HostIp> attachIps = new ArrayList<>();
      StringJoiner ipStrJoiner = new StringJoiner(",");

      for (int i = 0; i < publicNetworks.size(); i++) {
         BaseMemberPublicNetwork publicNetwork = publicNetworks.get(i);
         boolean isMain = i == mainIndex;
         ServerIpAddParam publicIpParam = new ServerIpAddParam();
         publicIpParam.setServerId(server.getId());
         publicIpParam.setIp(publicNetwork.getIp());
         publicIpParam.setGateway(publicNetwork.getGateway());
         publicIpParam.setMask(publicNetwork.getMask());
         publicIpParam.setIsMain(isMain ? 1 : 0);
         publicIpParam.setVlanId(publicNetwork.getVlanId());
         publicIpParam.setType("PUBLIC");
         log.info("创建云服务器，公网IP信息:{}", JSON.toJSONString(publicIpParam));
         this.serverIpService.add(publicIpParam);
         if (!isMain) {
            HostIp hostIp = new HostIp();
            hostIp.setIp(publicNetwork.getIp());
            hostIp.setMask(publicNetwork.getMask());
            attachIps.add(hostIp);
            ipStrJoiner.add(publicNetwork.getIp());
         }
      }

      hostParam.setAttachIps(attachIps);
      hostParam.setIpStr(attachIps.isEmpty() ? "" : ipStrJoiner.toString());
      return allocatedIds;
   }

   private BaseMemberNatNetwork setNatNetworkInformation(HostParam hostParam, Server server) {
      ServerIpAddParam publicIpParam = new ServerIpAddParam();
      BaseMemberNatNetwork natNetwork = this.memberNatNetworkApi.selectByMaster(server.getMasterId());
      log.info("创建云服务器，Nat网络信息:{}", JSON.toJSONString(natNetwork));
      if (ObjectUtil.isNull(natNetwork)) {
         throw new CommonException("创建云服务器失败，公网网络信息不存在", new Object[0]);
      } else {
         server.setPublicIp(natNetwork.getIp());
         server.setPublicNetmask(natNetwork.getMask());
         String publicMac = this.requireServerNatMac(server);
         server.setPublicGateway(natNetwork.getGateway());
         hostParam.setNatIp(natNetwork.getIp());
         hostParam.setNatGateway(natNetwork.getGateway());
         hostParam.setNatNetmask(natNetwork.getMask());
         hostParam.setNatMac(publicMac);
         hostParam.setNatVlanId(natNetwork.getVlanId());
         publicIpParam.setServerId(server.getId());
         publicIpParam.setIp(natNetwork.getIp());
         publicIpParam.setGateway(natNetwork.getGateway());
         publicIpParam.setMask(natNetwork.getMask());
         publicIpParam.setIsMain(1);
         publicIpParam.setVlanId(natNetwork.getVlanId());
         publicIpParam.setType("NAT");
         log.info("创建云服务器，NatIP信息:{}", JSON.toJSONString(publicIpParam));
         this.serverIpService.add(publicIpParam);
         return natNetwork;
      }
   }

   private BaseMemberPrivateNetwork setPrivateNetworkInformation(HostParam hostParam, Server server) {
      ServerIpAddParam privateIpParam = new ServerIpAddParam();
      BaseMemberPrivateNetwork privateNetwork = this.memberPrivateNetworkApi.selectByMaster(server.getMasterId());
      log.info("创建云服务器，私有网络信息:{}", JSON.toJSONString(privateNetwork));
      if (ObjectUtil.isNull(privateNetwork)) {
         throw new CommonException("创建云服务器失败，私有网络信息不存在", new Object[0]);
      } else {
         hostParam.setPrivateIp(privateNetwork.getIp());
         hostParam.setPrivateGateway(privateNetwork.getGateway());
         hostParam.setPrivateNetmask(privateNetwork.getMask());
         String privateMac = this.requireServerPrivateMac(server);
         hostParam.setPrivateMac(privateMac);
         hostParam.setPrivateVlanId(privateNetwork.getVlanId());
         server.setPrivateIp(privateNetwork.getIp());
         server.setPrivateGateway(privateNetwork.getGateway());
         server.setPrivateNetmask(privateNetwork.getMask());
         server.setPrivateVlanId(privateNetwork.getVlanId());
         privateIpParam.setServerId(server.getId());
         privateIpParam.setIp(privateNetwork.getIp());
         privateIpParam.setGateway(privateNetwork.getGateway());
         privateIpParam.setMask(privateNetwork.getMask());
         privateIpParam.setVlanId(privateNetwork.getVlanId());
         privateIpParam.setType("PRIVATE");
         log.info("创建云服务器，私网IP信息:{}", JSON.toJSONString(privateIpParam));
         this.serverIpService.add(privateIpParam);
         return privateNetwork;
      }
   }

   private String requireServerPublicMac(Server server) {
      if (server != null && !StrUtil.isBlank(server.getPublicMac())) {
         return server.getPublicMac();
      } else {
         throw new CommonException("创建云服务器失败，云服务器外网MAC为空", new Object[0]);
      }
   }

   private void prepareServerMacForCreate(Server server, boolean sharedIp) {
      if (server != null) {
         String currentServerId = server.getId();
         if (sharedIp) {
            if (StrUtil.isBlank(server.getPublicMac()) || !server.getPublicMac().startsWith("00:50:56")) {
               server.setPublicMac(this.generateUniqueServerMac("NAT", "PUBLIC", currentServerId));
            }
         } else if (StrUtil.isBlank(server.getPublicMac())) {
            server.setPublicMac(this.generateUniqueServerMac("PUBLIC", "PUBLIC", currentServerId));
         }

         if (StrUtil.isBlank(server.getPrivateMac())) {
            server.setPrivateMac(this.generateUniqueServerMac("PRIVATE", "PRIVATE", currentServerId));
         }
      }
   }

   private String requireServerNatMac(Server server) {
      if (server == null) {
         throw new CommonException("创建云服务器失败，云服务器外网MAC为空", new Object[0]);
      } else {
         if (StrUtil.isBlank(server.getPublicMac()) || !server.getPublicMac().startsWith("00:50:56")) {
            server.setPublicMac(this.generateUniqueServerMac("NAT", "PUBLIC", server.getId()));
         }

         return server.getPublicMac();
      }
   }

   private String requireServerPrivateMac(Server server) {
      if (server != null && !StrUtil.isBlank(server.getPrivateMac())) {
         return server.getPrivateMac();
      } else {
         throw new CommonException("创建云服务器失败，云服务器内网MAC为空", new Object[0]);
      }
   }

   private String generateUniqueServerMac(String generatorType, String columnType, String currentServerId) {
      for (int i = 0; i < 20; i++) {
         String mac = MacAddressGenerator.generateMacAddress(generatorType);
         if (!this.serverMacExists(columnType, mac, currentServerId)) {
            return mac;
         }
      }

      throw new CommonException("生成唯一MAC地址失败，请重试", new Object[0]);
   }

   private boolean serverMacExists(String columnType, String mac, String currentServerId) {
      if (StrUtil.isBlank(mac)) {
         return false;
      } else {
         LambdaQueryWrapper<Server> wrapper = new LambdaQueryWrapper();
         if ("PUBLIC".equals(columnType)) {
            wrapper.eq(Server::getPublicMac, mac);
         } else {
            if (!"PRIVATE".equals(columnType)) {
               throw new CommonException("该MAC地址类型不存在: " + columnType, new Object[0]);
            }

            wrapper.eq(Server::getPrivateMac, mac);
         }

         if (StrUtil.isNotBlank(currentServerId)) {
            wrapper.ne(Server::getId, currentServerId);
         }

         wrapper.ne(Server::getBusinessStatus, BusinessStatusEnum.DELETE.getCode());
         return this.serverService.count(wrapper) > 0L;
      }
   }

   private void processUpgradeHardDisksTask(AutoTask task) {
      log.info("处理服务器升级磁盘任务[ID:{}]", task.getId());

      try {
         HostParam hostParam = (HostParam)JSON.parseObject(task.getParams(), HostParam.class);
         this.enrichHostParamForMfc(hostParam);
         CloudControllerAdapter adapter = this.resolveAdapterByServerId(hostParam.getServerId());
         CommonResult result = adapter.updateHardDiskHost(hostParam);
         if (!result.getCode().equals(200)) {
            throw new RuntimeException(result.getMsg());
         } else if ("3".equals(adapter.getGuardVersion())) {
            this.rememberRkQueueId(task, result);
            task.setExecuteResult(this.buildRkWaitingMessage("磁盘扩容", task));
         } else {
            task.setExecuteResult("服务器升级磁盘成功");
            log.info("服务器升级磁盘成功[ID:{}]", task.getId());
            Server s = (Server)this.serverService.getById(hostParam.getServerId());
            if (s != null) {
               this.hostStatsRefreshService.refreshByMasterId(s.getMasterId());
               if ("2".equals(adapter.getGuardVersion())) {
                  this.serverService.updateServerStatusAndLog(s.getId(), ServerStatusEnum.RUNNING.getCode());
               }
            }
         }
      } catch (Exception var6) {
         log.error("升级服务器磁盘失败[ID:{}]", task.getId(), var6);
         throw new RuntimeException("升级服务器磁盘失败: " + var6.getMessage());
      }
   }

   private void processUpgradeBandwidthTask(AutoTask task) {
      log.info("处理服务器升级带宽任务[ID:{}]", task.getId());

      try {
         HostParam hostParam = (HostParam)JSON.parseObject(task.getParams(), HostParam.class);
         this.enrichHostParamForMfc(hostParam);
         CloudControllerAdapter adapter = this.resolveAdapterByServerId(hostParam.getServerId());
         CommonResult result = adapter.updateBandwidthHost(hostParam);
         if (!result.getCode().equals(200)) {
            throw new RuntimeException(result.getMsg());
         } else if ("3".equals(adapter.getGuardVersion())) {
            this.rememberRkQueueId(task, result);
            task.setExecuteResult(this.buildRkWaitingMessage("设置带宽", task));
         } else {
            task.setExecuteResult("服务器升级带宽成功");
            log.info("服务器升级带宽成功[ID:{}]", task.getId());
            Server s = (Server)this.serverService.getById(hostParam.getServerId());
            if (s != null) {
               this.hostStatsRefreshService.refreshByMasterId(s.getMasterId());
               if ("2".equals(adapter.getGuardVersion())) {
                  this.serverService.updateServerStatusAndLog(s.getId(), ServerStatusEnum.RUNNING.getCode());
               }
            }
         }
      } catch (Exception var6) {
         log.error("升级服务器带宽失败[ID:{}]", task.getId(), var6);
         throw new RuntimeException("升级服务器带宽失败: " + var6.getMessage());
      }
   }

   private void processUpgradeCpuRamTask(AutoTask task) {
      log.info("处理服务器升级核心任务[ID:{}]", task.getId());

      try {
         HostParam hostParam = (HostParam)JSON.parseObject(task.getParams(), HostParam.class);
         this.enrichHostParamForMfc(hostParam);
         CloudControllerAdapter adapter = this.resolveAdapterByServerId(hostParam.getServerId());
         CommonResult result = adapter.updateCpuRamHost(hostParam);
         if (!result.getCode().equals(200)) {
            throw new RuntimeException(result.getMsg());
         } else if ("3".equals(adapter.getGuardVersion())) {
            this.rememberRkQueueId(task, result);
            task.setExecuteResult(this.buildRkWaitingMessage("升级配置", task));
         } else {
            task.setExecuteResult("服务器升级核心成功");
            log.info("服务器升级核心成功[ID:{}]", task.getId());
            Server s = (Server)this.serverService.getById(hostParam.getServerId());
            if (s != null) {
               this.hostStatsRefreshService.refreshByMasterId(s.getMasterId());
               if ("2".equals(adapter.getGuardVersion())) {
                  this.serverService.updateServerStatusAndLog(s.getId(), ServerStatusEnum.RUNNING.getCode());
               }
            }
         }
      } catch (Exception var6) {
         log.error("升级服务器核心失败[ID:{}]", task.getId(), var6);
         throw new RuntimeException("升级服务器核心失败: " + var6.getMessage());
      }
   }

   private void processRenewTask(AutoTask task) {
      log.info("处理续费服务器任务[ID:{}]", task.getId());

      try {
         Server server = (Server)this.serverService.getById(task.getServerId());
         if (server == null) {
            log.error("服务器不存在[ID:{}]", task.getServerId());
            throw new RuntimeException("服务器不存在: " + task.getServerId());
         } else {
            if (ServerStatusEnum.STOP.getCode().equals(server.getServerStatus())) {
               HostParam hostParam = (HostParam)JSON.parseObject(task.getParams(), HostParam.class);
               task.setParams(JSON.toJSONString(hostParam));
               log.info("开机请求:{}", JSON.toJSONString(hostParam));
               CloudControllerAdapter adapter = this.resolveAdapterByServerId(hostParam.getServerId());
               CommonResult result = adapter.startHost(hostParam);
               log.info("开机结果:{}", JSON.toJSONString(result));
               if (!result.getCode().equals(200)) {
                  throw new RuntimeException(result.getMsg());
               }

               ((UpdateChainWrapper)((UpdateChainWrapper)((UpdateChainWrapper)this.serverService.update().eq("id", task.getServerId()))
                        .set("server_status", ServerStatusEnum.RUNNING.getCode()))
                     .set("business_status", BusinessStatusEnum.NORMAL.getCode()))
                  .update();
            }

            task.setExecuteResult("服务器续费成功");
            log.info("服务器续费成功[ID:{}]", task.getId());
         }
      } catch (Exception var6) {
         log.error("续费服务器失败[ID:{}]: {}", task.getId(), var6);
         throw new RuntimeException("续费服务器失败:" + var6.getMessage());
      }
   }

   private String getExceptionMessage(Throwable e) {
      if (e == null) {
         return "未知错误";
      } else {
         for (Throwable t = e; t != null; t = t.getCause()) {
            String msg = t.getMessage();
            if (msg != null && !msg.trim().isEmpty()) {
               return msg;
            }
         }

         return e.getClass().getSimpleName() + "（无详细消息）";
      }
   }

   private void handleTaskFailure(AutoTask task, Throwable e) {
      log.info("进入handleTaskFailure方法，任务ID:{}，异常类型:{}", task.getId(), e.getClass().getName());
      int retryCount = task.getRetryCount() != null ? task.getRetryCount() : 0;
      int maxRetry = task.getMaxRetry() != null ? task.getMaxRetry() : 3;
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      e.printStackTrace(pw);
      String fullExceptionMessage = sw.toString();
      String shortExceptionMessage = this.getExceptionMessage(e);
      if (retryCount >= maxRetry) {
         task.setTaskStatus(-1);
         task.setExecuteResult(String.format("任务执行失败，已达到最大重试次数。原因: %s", shortExceptionMessage));
         log.warn("任务[ID:{}]已达到最大重试次数: {}/{}, 失败原因: {}", new Object[]{task.getId(), retryCount, maxRetry, fullExceptionMessage});
      } else {
         task.setRetryCount(retryCount + 1);
         task.setNextRetryTime(this.calculateNextRetryTime(retryCount + 1));
         task.setTaskStatus(1);
         task.setExecuteResult(String.format("任务执行失败，将重试。原因: %s", shortExceptionMessage));
         log.warn("任务[ID:{}]将在下次重试，重试次数: {}/{}, 失败原因: {}", new Object[]{task.getId(), task.getRetryCount(), maxRetry, fullExceptionMessage});
      }

      boolean updated = this.orderAsyncTaskService.updateById(task);
      log.info("任务状态更新结果: {}", updated);
   }

   private Date calculateNextRetryTime(int retryCount) {
      long delay = 60000L;
      return new Date(System.currentTimeMillis() + delay);
   }

   private void processStartTask(AutoTask task) {
      log.info("处理开机服务器任务[ID:{}]", task.getId());

      try {
         HostParam hostParam = (HostParam)JSON.parseObject(task.getParams(), HostParam.class);
         this.enrichHostParamForMfc(hostParam);
         log.info("开机请求:{}", JSON.toJSONString(hostParam));
         Server server = (Server)this.serverService.getById(hostParam.getServerId());
         BaseMemberMaster memberMaster = this.memberMasterApi.selectByMasterId(server.getMasterId());
         log.info(
            "版本路由: serverId={}, masterId={}, guardVersion={}",
            new Object[]{hostParam.getServerId(), server.getMasterId(), memberMaster != null ? memberMaster.getGuardVersion() : null}
         );
         CloudControllerAdapter adapter = this.cloudControllerRouter.getAdapterByGuardVersion(memberMaster != null ? memberMaster.getGuardVersion() : null);
         CommonResult result = adapter.startHost(hostParam);
         log.info("开机结果:{}", JSON.toJSONString(result));
         if (!result.getCode().equals(200)) {
            throw new RuntimeException(result.getMsg());
         } else if (memberMaster != null && "3".equals(memberMaster.getGuardVersion())) {
            this.rememberRkQueueId(task, result);
            task.setExecuteResult(this.buildRkWaitingMessage("开机", task));
         } else {
            this.serverService.updateServerStatusAndLog(hostParam.getServerId(), ServerStatusEnum.RUNNING.getCode());
            task.setExecuteResult("服务器开机成功");
         }
      } catch (Exception var7) {
         log.error("开机服务器失败[ID:{}], 错误详情:", task.getId(), var7);
         throw new RuntimeException("开机服务器失败: " + var7.getMessage());
      }
   }

   private void processStopTask(AutoTask task) {
      log.info("处理关机服务器任务[ID:{}]", task.getId());

      try {
         HostParam hostParam = (HostParam)JSON.parseObject(task.getParams(), HostParam.class);
         this.enrichHostParamForMfc(hostParam);
         log.info("关机请求:{}", JSON.toJSONString(hostParam));
         Server server = (Server)this.serverService.getById(hostParam.getServerId());
         BaseMemberMaster memberMaster = this.memberMasterApi.selectByMasterId(server.getMasterId());
         log.info(
            "版本路由: serverId={}, masterId={}, guardVersion={}",
            new Object[]{hostParam.getServerId(), server.getMasterId(), memberMaster != null ? memberMaster.getGuardVersion() : null}
         );
         CloudControllerAdapter adapter = this.cloudControllerRouter.getAdapterByGuardVersion(memberMaster != null ? memberMaster.getGuardVersion() : null);
         CommonResult result = adapter.closeHost(hostParam);
         log.info("关机结果:{}", JSON.toJSONString(result));
         if (!result.getCode().equals(200)) {
            throw new RuntimeException(result.getMsg());
         } else if (memberMaster != null && "3".equals(memberMaster.getGuardVersion())) {
            this.rememberRkQueueId(task, result);
            task.setExecuteResult(this.buildRkWaitingMessage("关机", task));
         } else {
            this.serverService.updateServerStatusAndLog(hostParam.getServerId(), ServerStatusEnum.STOP.getCode());
            task.setExecuteResult("云服务器已关机");
         }
      } catch (Exception var7) {
         log.error("关机服务器失败[ID:{}], 错误详情:", task.getId(), var7);
         throw new RuntimeException("关机服务器失败: " + var7.getMessage());
      }
   }

   private void processDeleteTask(AutoTask task) {
      log.info("处理删除服务器任务[ID:{}]", task.getId());

      try {
         HostParam hostParam = (HostParam)JSON.parseObject(task.getParams(), HostParam.class);
         this.enrichHostParamForMfc(hostParam);
         log.info("删除服务器请求:{}", JSON.toJSONString(hostParam));
         Server server = (Server)this.serverService.getById(hostParam.getServerId());
         BaseMemberMaster memberMaster = this.memberMasterApi.selectByMasterId(server.getMasterId());
         log.info(
            "版本路由: serverId={}, masterId={}, guardVersion={}",
            new Object[]{hostParam.getServerId(), server.getMasterId(), memberMaster != null ? memberMaster.getGuardVersion() : null}
         );
         CloudControllerAdapter adapter = this.cloudControllerRouter.getAdapterByGuardVersion(memberMaster != null ? memberMaster.getGuardVersion() : null);
         CommonResult result = adapter.deleteHost(hostParam);
         log.info("删除服务器结果:{}", JSON.toJSONString(result));
         if (!result.getCode().equals(200)) {
            throw new RuntimeException(result.getMsg());
         } else if (memberMaster != null && "3".equals(memberMaster.getGuardVersion())) {
            this.rememberRkQueueId(task, result);
            task.setExecuteResult(this.buildRkWaitingMessage("删除", task));
         } else {
            ((UpdateChainWrapper)((UpdateChainWrapper)((UpdateChainWrapper)this.serverService.update().eq("id", hostParam.getServerId()))
                     .set("server_status", ServerStatusEnum.STOP.getCode()))
                  .set("business_status", BusinessStatusEnum.DELETE.getCode()))
               .update();
            if (memberMaster != null && "2".equals(memberMaster.getGuardVersion())) {
               this.serverService.releaseMacAfterDelete(hostParam.getServerId());
               this.serverIpService.deleteByServerIdWithServerSnapshotFallback(hostParam.getServerId());
            }

            task.setExecuteResult("删除服务器请求已提交");
            log.info("删除服务器请求已提交[ID:{}]，已更新服务器状态为已删除", task.getId());
            this.serverService.saveResourceChangeLogAfterDeleteSuccess(hostParam.getServerId());
         }
      } catch (Exception var7) {
         log.error("删除服务器失败[ID:{}], 错误详情:", task.getId(), var7);
         throw new RuntimeException("删除服务器失败: " + var7.getMessage());
      }
   }

   private void processReinstallTask(AutoTask task) {
      log.info("处理重新安装服务器任务[ID:{}]", task.getId());

      try {
         HostParam hostParam = (HostParam)JSON.parseObject(task.getParams(), HostParam.class);
         this.enrichHostParamForMfc(hostParam);
         CloudControllerAdapter adapter = this.resolveAdapterByServerId(hostParam.getServerId());
         CommonResult result = adapter.reinstallHost(hostParam);
         if (!result.getCode().equals(200)) {
            throw new RuntimeException(result.getMsg());
         } else {
            if ("2".equals(adapter.getGuardVersion())) {
               this.serverService.updateServerStatusAndLog(hostParam.getServerId(), ServerStatusEnum.RUNNING.getCode());
            } else {
               if ("3".equals(adapter.getGuardVersion())) {
                  this.serverService.updateServerStatusAndLog(hostParam.getServerId(), ServerStatusEnum.REINSTALLING.getCode());
                  this.rememberRkQueueId(task, result);
                  task.setExecuteResult(this.buildRkWaitingMessage("重装系统", task));
                  return;
               }

               this.serverService.updateServerStatusAndLog(hostParam.getServerId(), ServerStatusEnum.REINSTALLING.getCode());
            }

            task.setExecuteResult("服务器重新安装成功");
         }
      } catch (Exception var5) {
         log.error("重新安装服务器失败[ID:{}], 错误详情:", task.getId(), var5);
         throw new RuntimeException("重新安装服务器失败: " + var5.getMessage());
      }
   }

   private void processRebuildTask(AutoTask task) {
      log.info("处理重建服务器任务[ID:{}]", task.getId());

      try {
         HostParam hostParam = (HostParam)JSON.parseObject(task.getParams(), HostParam.class);
         log.info("重建服务器请求参数：{}", JSON.toJSONString(hostParam));
         CloudControllerAdapter adapter = this.resolveAdapterByServerId(hostParam.getServerId());
         if ("3".equals(adapter.getGuardVersion())) {
            CommonResult result = adapter.rebuildHost(hostParam);
            log.info("重建服务器结果：{}", JSON.toJSONString(result));
            if (!result.getCode().equals(200)) {
               throw new RuntimeException(result.getMsg());
            } else {
               this.serverService.updateServerStatusAndLog(hostParam.getServerId(), ServerStatusEnum.REBUILDING.getCode());
               this.rememberRkQueueId(task, result);
               task.setExecuteResult(this.buildRkWaitingMessage("重建", task));
            }
         } else {
            CommonResult result = adapter.openHost(hostParam);
            log.info("重建服务器结果：{}", JSON.toJSONString(result));
            if (!result.getCode().equals(200)) {
               throw new RuntimeException(result.getMsg());
            } else {
               this.serverService.updateServerStatusAndLog(hostParam.getServerId(), ServerStatusEnum.REBUILDING.getCode());
               task.setExecuteResult("服务器重建成功");
            }
         }
      } catch (Exception var5) {
         log.error("重建服务器失败[ID:{}], 错误详情:", task.getId(), var5);
         throw new RuntimeException("重建服务器失败: " + var5.getMessage());
      }
   }

   private void processRestartTask(AutoTask task) {
      log.info("处理重启服务器任务[ID:{}]", task.getId());

      try {
         HostParam hostParam = (HostParam)JSON.parseObject(task.getParams(), HostParam.class);
         this.enrichHostParamForMfc(hostParam);
         Server server = (Server)this.serverService.getById(hostParam.getServerId());
         BaseMemberMaster memberMaster = this.memberMasterApi.selectByMasterId(server.getMasterId());
         log.info(
            "版本路由: serverId={}, masterId={}, guardVersion={}",
            new Object[]{hostParam.getServerId(), server.getMasterId(), memberMaster != null ? memberMaster.getGuardVersion() : null}
         );
         CloudControllerAdapter adapter = this.cloudControllerRouter.getAdapterByGuardVersion(memberMaster != null ? memberMaster.getGuardVersion() : null);
         CommonResult result = adapter.restartHost(hostParam);
         log.info("重启结果：{}", JSON.toJSONString(result));
         if (!result.getCode().equals(200)) {
            throw new RuntimeException(result.getMsg());
         } else if (memberMaster != null && "3".equals(memberMaster.getGuardVersion())) {
            this.rememberRkQueueId(task, result);
            task.setExecuteResult(this.buildRkWaitingMessage("重启", task));
         } else {
            log.info("重启结果：{}", JSON.toJSONString(result));
            this.serverService.updateServerStatusAndLog(hostParam.getServerId(), ServerStatusEnum.RUNNING.getCode());
            task.setExecuteResult("服务器重启成功");
         }
      } catch (Exception var7) {
         log.error("重启服务器失败[ID:{}], 错误详情:", task.getId(), var7);
         throw new RuntimeException("重启服务器失败: " + var7.getMessage());
      }
   }

   private void processUpdatePasswordTask(AutoTask task) {
      log.info("处理修改密码服务器任务[ID:{}]", task.getId());

      try {
         HostParam hostParam = (HostParam)JSON.parseObject(task.getParams(), HostParam.class);
         this.enrichHostParamForMfc(hostParam);
         Server server = (Server)this.serverService.getById(hostParam.getServerId());
         BaseMemberMaster memberMaster = this.memberMasterApi.selectByMasterId(server.getMasterId());
         log.info(
            "版本路由: serverId={}, masterId={}, guardVersion={}",
            new Object[]{hostParam.getServerId(), server.getMasterId(), memberMaster != null ? memberMaster.getGuardVersion() : null}
         );
         CloudControllerAdapter adapter = this.cloudControllerRouter.getAdapterByGuardVersion(memberMaster != null ? memberMaster.getGuardVersion() : null);
         CommonResult result = adapter.updatePasswordHost(hostParam);
         log.info("修改密码结果：{}", JSON.toJSONString(result));
         if (!result.getCode().equals(200)) {
            throw new RuntimeException(result.getMsg());
         } else if (memberMaster != null && "3".equals(memberMaster.getGuardVersion())) {
            this.rememberRkQueueId(task, result);
            task.setExecuteResult(this.buildRkWaitingMessage("修改密码", task));
         } else {
            log.info("修改密码结果：{}", JSON.toJSONString(result));
            this.serverService.updateServerStatusAndLog(hostParam.getServerId(), ServerStatusEnum.RUNNING.getCode());
            task.setExecuteResult("服务器密码修改成功");
         }
      } catch (Exception var7) {
         log.error("修改服务器密码失败[ID:{}], 错误详情:", task.getId(), var7);
         throw new RuntimeException("修改服务器密码失败: " + var7.getMessage());
      }
   }

   private void processLockTask(AutoTask task) {
      log.info("处理锁定服务器任务[ID:{}]", task.getId());

      try {
         HostParam hostParam = (HostParam)JSON.parseObject(task.getParams(), HostParam.class);
         this.enrichHostParamForMfc(hostParam);
         CloudControllerAdapter adapter = this.resolveAdapterByServerId(hostParam.getServerId());
         CommonResult result = adapter.closeHost(hostParam);
         if (!result.getCode().equals(200)) {
            throw new RuntimeException(result.getMsg());
         } else if ("3".equals(adapter.getGuardVersion())) {
            this.rememberRkQueueId(task, result);
            task.setExecuteResult(this.buildRkWaitingMessage("锁定关机", task));
         } else {
            this.serverService.updateServerStatusAndLog(hostParam.getServerId(), ServerStatusEnum.LOCKED.getCode());
            task.setExecuteResult("服务器锁定成功");
         }
      } catch (Exception var5) {
         log.error("锁定服务器失败[ID:{}], 错误详情:", task.getId(), var5);
         throw new RuntimeException("锁定服务器失败: " + var5.getMessage());
      }
   }

   private void processUnlockTask(AutoTask task) {
      log.info("处理解锁服务器任务[ID:{}]", task.getId());

      try {
         HostParam hostParam = (HostParam)JSON.parseObject(task.getParams(), HostParam.class);
         this.enrichHostParamForMfc(hostParam);
         CloudControllerAdapter adapter = this.resolveAdapterByServerId(hostParam.getServerId());
         CommonResult result = adapter.startHost(hostParam);
         if (!result.getCode().equals(200)) {
            throw new RuntimeException(result.getMsg());
         } else if ("3".equals(adapter.getGuardVersion())) {
            this.rememberRkQueueId(task, result);
            task.setExecuteResult(this.buildRkWaitingMessage("解锁开机", task));
         } else {
            this.serverService.updateServerStatusAndLog(hostParam.getServerId(), ServerStatusEnum.RUNNING.getCode());
            task.setExecuteResult("服务器解锁成功");
         }
      } catch (Exception var5) {
         log.error("解锁服务器失败[ID:{}], 错误详情:", task.getId(), var5);
         throw new RuntimeException("解锁服务器失败: " + var5.getMessage());
      }
   }

   private void processDisconnectTask(AutoTask task) {
      log.info("处理服务器断网任务[ID:{}]", task.getId());

      try {
         HostParam hostParam = (HostParam)JSON.parseObject(task.getParams(), HostParam.class);
         this.enrichHostParamForMfc(hostParam);
         CloudControllerAdapter adapter = this.resolveAdapterByServerId(hostParam.getServerId());
         CommonResult result = adapter.setNetworkStateHost(hostParam);
         log.info("断网结果：{}", JSON.toJSONString(result));
         if (!result.getCode().equals(200)) {
            throw new RuntimeException(result.getMsg());
         } else {
            log.info("断网结果：{}", JSON.toJSONString(result));
            this.serverService.updateServerStatusAndLog(hostParam.getServerId(), ServerStatusEnum.DISCONNECT.getCode());
            task.setExecuteResult("服务器断网成功");
         }
      } catch (Exception var5) {
         log.error("服务器断网失败[ID:{}], 错误详情:", task.getId(), var5);
         throw new RuntimeException("服务器断网失败: " + var5.getMessage());
      }
   }

   private void processReconnectTask(AutoTask task) {
      log.info("处理服务器恢复网络任务[ID:{}]", task.getId());

      try {
         HostParam hostParam = (HostParam)JSON.parseObject(task.getParams(), HostParam.class);
         this.enrichHostParamForMfc(hostParam);
         CloudControllerAdapter adapter = this.resolveAdapterByServerId(hostParam.getServerId());
         CommonResult result = adapter.setNetworkStateHost(hostParam);
         log.info("恢复网络结果：{}", JSON.toJSONString(result));
         if (!result.getCode().equals(200)) {
            throw new RuntimeException(result.getMsg());
         } else {
            log.info("恢复网络结果：{}", JSON.toJSONString(result));
            this.serverService.updateServerStatusAndLog(hostParam.getServerId(), ServerStatusEnum.RUNNING.getCode());
            task.setExecuteResult("服务器恢复网络成功");
         }
      } catch (Exception var5) {
         log.error("服务器恢复网络失败[ID:{}], 错误详情:", task.getId(), var5);
         throw new RuntimeException("服务器恢复网络失败: " + var5.getMessage());
      }
   }

   private void processPowerTask(AutoTask task) {
      log.info("处理关闭服务器电源任务[ID:{}]", task.getId());

      try {
         HostParam hostParam = (HostParam)JSON.parseObject(task.getParams(), HostParam.class);
         this.enrichHostParamForMfc(hostParam);
         Server server = (Server)this.serverService.getById(hostParam.getServerId());
         BaseMemberMaster memberMaster = this.memberMasterApi.selectByMasterId(server.getMasterId());
         log.info(
            "版本路由: serverId={}, masterId={}, guardVersion={}",
            new Object[]{hostParam.getServerId(), server.getMasterId(), memberMaster != null ? memberMaster.getGuardVersion() : null}
         );
         CloudControllerAdapter adapter = this.cloudControllerRouter.getAdapterByGuardVersion(memberMaster != null ? memberMaster.getGuardVersion() : null);
         CommonResult result = adapter.powerHost(hostParam);
         log.info("断开电源结果：{}", JSON.toJSONString(result));
         if (!result.getCode().equals(200)) {
            throw new RuntimeException(result.getMsg());
         } else if (memberMaster != null && "3".equals(memberMaster.getGuardVersion())) {
            this.rememberRkQueueId(task, result);
            task.setExecuteResult(this.buildRkWaitingMessage("断电", task));
         } else {
            log.info("断开电源结果：{}", JSON.toJSONString(result));
            this.serverService.updateServerStatusAndLog(hostParam.getServerId(), ServerStatusEnum.POWER.getCode());
            task.setExecuteResult("服务器电源关闭成功");
         }
      } catch (Exception var7) {
         log.error("关闭服务器电源失败[ID:{}], 错误详情:", task.getId(), var7);
         throw new RuntimeException("关闭服务器电源失败: " + var7.getMessage());
      }
   }

   private void processAddIpTask(AutoTask task) {
      log.info("处理批量设置IP任务[ID:{}]", task.getId());

      try {
         HostParam hostParam = (HostParam)JSON.parseObject(task.getParams(), HostParam.class);
         CloudControllerAdapter adapter = this.resolveAdapterByServerId(hostParam.getServerId());
         Server server = (Server)this.serverService.getById(hostParam.getServerId());
         BaseMemberMaster memberMaster = this.memberMasterApi.selectByMasterId(server.getMasterId());
         List<ServerIp> serverIps = this.serverIpService.queryByServerId(server.getId());
         ServerIp mainPublicOrNatIp = this.findPrimaryServerIp(serverIps, "PUBLIC", "NAT");
         ServerIp privateIp = this.findPrimaryServerIp(serverIps, "PRIVATE");
         List<HostIp> hostIps = new ArrayList<>();

         for (ServerIp serverIp : serverIps) {
            if (serverIp.getIsMain() == 0 && "PUBLIC".equals(serverIp.getType())) {
               HostIp hi = new HostIp();
               hi.setIp(serverIp.getIp());
               hi.setMask(serverIp.getMask());
               hostIps.add(hi);
            }
         }

         HostParam resetParam = new HostParam();
         resetParam.setHostName(server.getHostName());
         resetParam.setServerId(server.getId());
         resetParam.setQueueId(hostParam.getQueueId());
         resetParam.setMasterIp(memberMaster.getIp());
         resetParam.setApiKey(memberMaster.getApiPassword());
         resetParam.setApiPort(memberMaster.getApiPort());
         resetParam.setBandwidth(server.getBandwidth());
         resetParam.setBandwidthIn(server.getBandwidthIn());
         resetParam.setPublicSwitchName(memberMaster.getPublicSwitchName());
         resetParam.setNatSwitchName(memberMaster.getNatSwitchName());
         resetParam.setPrivateSwitchName(memberMaster.getPrivateSwitchName());
         if (memberMaster.getShareIp() != 1) {
            resetParam.setPublicIp(this.valueOrFallback(mainPublicOrNatIp != null ? mainPublicOrNatIp.getIp() : null, server.getPublicIp()));
            resetParam.setPublicGateway(this.valueOrFallback(mainPublicOrNatIp != null ? mainPublicOrNatIp.getGateway() : null, server.getPublicGateway()));
            resetParam.setPublicNetmask(this.valueOrFallback(mainPublicOrNatIp != null ? mainPublicOrNatIp.getMask() : null, server.getPublicNetmask()));
            resetParam.setPublicMac(server.getPublicMac());
            if (mainPublicOrNatIp != null) {
               resetParam.setPublicVlanId(mainPublicOrNatIp.getVlanId());
            }
         } else {
            resetParam.setNatIp(this.valueOrFallback(mainPublicOrNatIp != null ? mainPublicOrNatIp.getIp() : null, this.resolveSharedNatIp(server)));
            resetParam.setNatGateway(this.valueOrFallback(mainPublicOrNatIp != null ? mainPublicOrNatIp.getGateway() : null, server.getPublicGateway()));
            resetParam.setNatNetmask(this.valueOrFallback(mainPublicOrNatIp != null ? mainPublicOrNatIp.getMask() : null, server.getPublicNetmask()));
            resetParam.setNatMac(server.getPublicMac());
            if (mainPublicOrNatIp != null) {
               resetParam.setNatVlanId(mainPublicOrNatIp.getVlanId());
            }
         }

         resetParam.setPrivateIp(this.valueOrFallback(privateIp != null ? privateIp.getIp() : null, server.getPrivateIp()));
         resetParam.setPrivateGateway(this.valueOrFallback(privateIp != null ? privateIp.getGateway() : null, server.getPrivateGateway()));
         resetParam.setPrivateNetmask(this.valueOrFallback(privateIp != null ? privateIp.getMask() : null, server.getPrivateNetmask()));
         resetParam.setPrivateMac(server.getPrivateMac());
         resetParam.setPrivateVlanId(privateIp != null ? privateIp.getVlanId() : server.getPrivateVlanId());
         resetParam.setShareIp(server.getShareIp());
         resetParam.setAttachIps(hostIps);
         resetParam.setIpStr(hostParam.getIpStr());
         BaseMemberLocalLine localLine = this.memberLocalLineApi.selectByLineId(server.getLineId());
         if (localLine != null) {
            resetParam.setAutoDns(localLine.getAutoDns());
         }

         log.info(
            "批量设置IP任务准备调用适配器重置IP, taskId={}, serverId={}, hostName={}, mainIp={}, attachIpCount={}, privateIp={}, ipStr={}",
            new Object[]{
               task.getId(),
               resetParam.getServerId(),
               resetParam.getHostName(),
               resetParam.getShareIp() != 1 ? resetParam.getPublicIp() : resetParam.getNatIp(),
               resetParam.getAttachIps() != null ? resetParam.getAttachIps().size() : 0,
               resetParam.getPrivateIp(),
               resetParam.getIpStr()
            }
         );
         CommonResult result = memberMaster != null && "3".equals(memberMaster.getGuardVersion())
            ? adapter.batchSetIpHost(resetParam)
            : adapter.resetIpHost(resetParam);
         if (!result.getCode().equals(200)) {
            throw new RuntimeException(result.getMsg());
         } else if (memberMaster != null && "3".equals(memberMaster.getGuardVersion())) {
            this.rememberRkQueueId(task, result);
            task.setExecuteResult(this.buildRkWaitingMessage("设置IP", task));
         } else {
            this.serverService.updateServerStatusAndLog(server.getId(), ServerStatusEnum.RUNNING.getCode());
            task.setExecuteResult("适配器重置云服务器IP成功");
         }
      } catch (Exception var14) {
         log.error("批量设置IP失败[ID:{}], 错误详情:", task.getId(), var14);
         throw new RuntimeException("批量设置IP失败: " + var14.getMessage());
      }
   }

   private void processResetIpTask(AutoTask task) {
      log.info("处理重置云服务器IP任务[ID:{}]", task.getId());

      try {
         HostParam hostParam = (HostParam)JSON.parseObject(task.getParams(), HostParam.class);
         this.enrichHostParamForMfc(hostParam);
         Server server = (Server)this.serverService.getById(hostParam.getServerId());
         BaseMemberMaster memberMaster = this.memberMasterApi.selectByMasterId(server.getMasterId());
         log.info(
            "版本路由: serverId={}, masterId={}, guardVersion={}",
            new Object[]{hostParam.getServerId(), server.getMasterId(), memberMaster != null ? memberMaster.getGuardVersion() : null}
         );
         CloudControllerAdapter adapter = this.cloudControllerRouter.getAdapterByGuardVersion(memberMaster != null ? memberMaster.getGuardVersion() : null);
         CommonResult result = adapter.resetIpHost(hostParam);
         if (!result.getCode().equals(200)) {
            throw new RuntimeException(result.getMsg());
         } else if (memberMaster != null && "3".equals(memberMaster.getGuardVersion())) {
            this.rememberRkQueueId(task, result);
            task.setExecuteResult(this.buildRkWaitingMessage("重置IP", task));
         } else {
            this.serverService.updateServerStatusAndLog(hostParam.getServerId(), ServerStatusEnum.RUNNING.getCode());
            task.setExecuteResult("适配器重置云服务器IP成功");
         }
      } catch (Exception var7) {
         log.error("重置云服务器IP失败[ID:{}], 错误详情:", task.getId(), var7);
         throw new RuntimeException("重置云服务器IP失败: " + var7.getMessage());
      }
   }

   private void releaseDistributedLock(AutoTask task) {
      try {
         Object currentValue = this.redisTemplate.opsForValue().get("server:operation_lock:" + task.getServerId());
         if (currentValue != null) {
            this.redisTemplate.delete("server:operation_lock:" + task.getServerId());
         }
      } catch (Exception var3) {
         log.error("释放分布式锁时发生异常", var3);
      }
   }

   private void enrichHostParamForMfc(HostParam hostParam) {
      if (hostParam != null && hostParam.getServerId() != null) {
         Server server = (Server)this.serverService.getById(hostParam.getServerId());
         if (server != null) {
            BaseMemberMaster memberMaster = this.memberMasterApi.selectByMasterId(server.getMasterId());
            if (memberMaster != null) {
               if (memberMaster.getIp() != null && !memberMaster.getIp().isBlank()) {
                  hostParam.setMasterIp(memberMaster.getIp());
               }

               if (memberMaster.getApiPassword() != null) {
                  hostParam.setApiKey(memberMaster.getApiPassword());
               }

               if (memberMaster.getApiPort() != null) {
                  hostParam.setApiPort(memberMaster.getApiPort());
               }

               if ("2".equals(memberMaster.getGuardVersion())) {
                  hostParam.setMfcCloudId(server.getMfcCloudId());
                  hostParam.setMfcLoginAccount(memberMaster.getMfcLoginAccount());
                  hostParam.setMfcLoginPassword(memberMaster.getMfcLoginPassword());
               }
            }
         }
      }
   }

   private void validateOsVersionControlledType(BaseMemberSystemVersion osVersion, String guardVersion, String osVersionId) {
      if (osVersion == null) {
         throw new CommonException("操作系统版本信息不存在", new Object[0]);
      } else if (guardVersion != null && !guardVersion.isBlank()) {
         List<String> controlledTypes = osVersion.getControlledType();
         if (controlledTypes == null || !controlledTypes.contains(guardVersion)) {
            throw new CommonException("操作系统版本不支持当前宿主机受控类型，osVersionId={}，guardVersion={}", new Object[]{osVersionId, guardVersion});
         }
      } else {
         throw new CommonException("宿主机受控类型为空", new Object[0]);
      }
   }

   private String resolveSharedNatIp(Server server) {
      if (server != null && Integer.valueOf(1).equals(server.getShareIp())) {
         for (ServerIp serverIp : this.serverIpService.queryByServerId(server.getId())) {
            if (Integer.valueOf(1).equals(serverIp.getIsMain())) {
               String type = serverIp.getType();
               if (("NAT".equals(type) || "PUBLIC".equals(type)) && serverIp.getIp() != null && !serverIp.getIp().isBlank()) {
                  return serverIp.getIp();
               }
            }
         }

         return server.getPublicIp();
      } else {
         return server != null ? server.getPublicIp() : null;
      }
   }

   private ServerIp findPrimaryServerIp(List<ServerIp> serverIps, String... types) {
      if (serverIps != null && !serverIps.isEmpty() && types != null && types.length != 0) {
         Set<String> targetTypes = new HashSet<>(Arrays.asList(types));
         ServerIp fallback = null;

         for (ServerIp serverIp : serverIps) {
            if (serverIp != null && targetTypes.contains(serverIp.getType())) {
               if (Integer.valueOf(1).equals(serverIp.getIsMain())) {
                  return serverIp;
               }

               if (fallback == null) {
                  fallback = serverIp;
               }
            }
         }

         return fallback;
      } else {
         return null;
      }
   }

   private String valueOrFallback(String preferred, String fallback) {
      return preferred != null && !preferred.isBlank() ? preferred : fallback;
   }

   private CloudControllerAdapter resolveAdapterByServerId(String serverId) {
      Server server = (Server)this.serverService.getById(serverId);
      BaseMemberMaster memberMaster = this.memberMasterApi.selectByMasterId(server.getMasterId());
      log.info(
         "版本路由: serverId={}, masterId={}, guardVersion={}",
         new Object[]{serverId, server.getMasterId(), memberMaster != null ? memberMaster.getGuardVersion() : null}
      );
      String gv = memberMaster != null ? memberMaster.getGuardVersion() : null;
      CloudControllerAdapter adapter = this.cloudControllerRouter.getAdapterByGuardVersion(gv);
      log.info("选用适配器: {}", adapter.getClass().getName());
      return adapter;
   }
}
