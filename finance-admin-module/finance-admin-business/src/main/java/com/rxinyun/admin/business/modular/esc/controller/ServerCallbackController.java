package com.rxinyun.admin.business.modular.esc.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.UpdateChainWrapper;
import com.rxinyun.admin.business.modular.esc.entity.Server;
import com.rxinyun.admin.business.modular.esc.param.ServerPortAddParam;
import com.rxinyun.admin.business.modular.esc.service.CallbackLogService;
import com.rxinyun.admin.business.modular.esc.service.HostStatsRefreshService;
import com.rxinyun.admin.business.modular.esc.service.ServerIpService;
import com.rxinyun.admin.business.modular.esc.service.ServerPortService;
import com.rxinyun.admin.business.modular.esc.service.ServerService;
import com.rxinyun.admin.business.modular.task.entity.AutoTask;
import com.rxinyun.admin.business.modular.task.service.AutoTaskTaskService;
import com.rxinyun.common.enums.NotificationTypeEnum;
import com.rxinyun.common.enums.business.BusinessStatusEnum;
import com.rxinyun.common.enums.business.ServerStatusEnum;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.dev.api.ToolsNotificationApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "云服务器回调控制器"
)
@RestController
@RequestMapping({"/api"})
@Validated
public class ServerCallbackController {
   private static final Logger log = LoggerFactory.getLogger(ServerCallbackController.class);
   private static final String PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA39KGJloywY5qmv9Dd27hjyrHKfonzb01sWbnbC7l6HImgHGGSVyw+VMI8SY34K2YgIBX+07qeQWFXOLgY4YByPqazXJWym4kI7gc/bSXy8Anda/kQcEq6Z8LA5vxB6ho98yeFBSh++Kjp7G/lINLoAsQIxn10I7rvSOhFNWQvNFM+EXAN+3y+Q8nkxwLadli27mVJJCO1EN8GSTlgOevC2pvua2tYRB8OPumEQgvch8+BgxhnTqUcSwRiQ20Bh8iZMpA4L7/WaMp1KcwElqCQHZVLDPMUhfzDnV8bvP356vKhOhJq8iLvKwR3R3s70u/zrRmJp8GmxyYPcamJUEDhQIDAQAB";
   @Resource
   private ServerService serverService;
   @Resource
   private HostStatsRefreshService hostStatsRefreshService;
   @Resource
   private ServerIpService serverIpService;
   @Resource
   private ServerPortService serverPortService;
   @Resource
   private CallbackLogService callbackLogService;
   @Resource
   private AutoTaskTaskService autoTaskTaskService;
   @Resource
   private ToolsNotificationApi toolsNotificationApi;

   public static boolean verifySignature(String timestamp, byte[] signature) throws Exception {
      PublicKey publicKey = loadPublicKey(
         "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA39KGJloywY5qmv9Dd27hjyrHKfonzb01sWbnbC7l6HImgHGGSVyw+VMI8SY34K2YgIBX+07qeQWFXOLgY4YByPqazXJWym4kI7gc/bSXy8Anda/kQcEq6Z8LA5vxB6ho98yeFBSh++Kjp7G/lINLoAsQIxn10I7rvSOhFNWQvNFM+EXAN+3y+Q8nkxwLadli27mVJJCO1EN8GSTlgOevC2pvua2tYRB8OPumEQgvch8+BgxhnTqUcSwRiQ20Bh8iZMpA4L7/WaMp1KcwElqCQHZVLDPMUhfzDnV8bvP356vKhOhJq8iLvKwR3R3s70u/zrRmJp8GmxyYPcamJUEDhQIDAQAB"
      );
      Signature sig = Signature.getInstance("SHA1withRSA");
      sig.initVerify(publicKey);
      sig.update(timestamp.getBytes());
      return sig.verify(signature);
   }

   @PostMapping({"/callback"})
   @Operation(
      summary = "处理云服务器受控回调"
   )
   public Object handleCallback(HttpServletRequest request) {
      String requestBody = null;
      JSONObject requestJson = null;

      try {
         requestBody = this.readRequestBody(request);
         log.info("Received Callback: {}", requestBody);
         requestJson = this.parseCallbackRequest(requestBody, request);
         String sign = requestJson.getString("sign");
         String timestamp = requestJson.getString("timestamp");
         String callbackParam = this.resolveCallbackParam(requestJson);
         String action = this.resolveCallbackAction(requestJson);
         log.info("\ud83d\udd14 回调参数解析 - callback_param: {}, action: {}, timestamp: {}", new Object[]{callbackParam, action, timestamp});
         if (!this.isBlank(sign) && !this.isBlank(timestamp)) {
            boolean isSignatureValid = verifySignature(timestamp, Base64.getDecoder().decode(sign));
            if (!isSignatureValid) {
               this.callbackLogService.logCallback("SERVER_CALLBACK", action, JSON.toJSONString(requestJson), "验签失败", callbackParam, 0, "签名验证失败");
               return CommonResult.error("验签失败");
            } else {
               CommonResult var17 = switch (action) {
                  case "create_vps" -> {
                     log.info("create_vps 创建云服务器回调请求");
                     this.createVps(callbackParam);
                     yield CommonResult.ok();
                  }
                  case "recreate_vps" -> CommonResult.ok();
                  case "update_vps" -> {
                     log.info("update_vps 升级云服务器回调请求");
                     this.updateVps(callbackParam);
                     yield CommonResult.ok();
                  }
                  case "remove_vps" -> {
                     this.removeVps(callbackParam);
                     yield CommonResult.ok();
                  }
                  case "reinstall" -> {
                     this.reinstallVps(callbackParam);
                     yield CommonResult.ok();
                  }
                  case "create_snapshot" -> CommonResult.ok();
                  case "create_backup" -> CommonResult.ok();
                  case "restore_snapshot" -> CommonResult.ok();
                  case "restore_backup" -> CommonResult.ok();
                  case "remove_snapshot" -> CommonResult.ok();
                  case "remove_backup" -> CommonResult.ok();
                  case "downISOFile", "importISO" -> CommonResult.ok();
                  case "installOS" -> CommonResult.ok();
                  case "rescue", "changePasswd" -> CommonResult.ok();
                  default -> CommonResult.error("未知操作类型");
               };
               this.callbackLogService.logCallback("SERVER_CALLBACK", action, requestJson.toJSONString(), var17.toString(), callbackParam, 1, null);
               return var17;
            }
         } else {
            CommonResult result = CommonResult.ok("回调已接收");
            AutoTask rkTask = null;
            boolean rkSuccess = this.isRkCallbackSuccess(requestJson);
            boolean callbackSuccess = rkSuccess;
            String callbackErrorMsg = this.buildRkCallbackErrorMsg(requestJson);
            if (rkSuccess) {
               rkTask = this.handleRkHyperVCallback(requestJson, callbackParam);
            } else {
               rkTask = this.handleRkHyperVDeleteNotFoundCallback(requestJson, callbackParam);
               if (rkTask != null) {
                  callbackSuccess = true;
                  callbackErrorMsg = null;
                  result = CommonResult.ok("删除目标不存在，已按删除成功处理");
               } else {
                  String queueId = this.resolveQueueId(requestJson);
                  log.warn("RkHyper-v业务回调失败，不更新云服务器状态，queueId={}, callbackParam={}, error={}", new Object[]{queueId, callbackParam, callbackErrorMsg});
               }
            }

            this.callbackLogService
               .logCallback(
                  "SERVER_CALLBACK",
                  action,
                  requestJson.toJSONString(),
                  result.toString(),
                  this.resolveCallbackLogServerId(rkTask, callbackParam, requestJson),
                  callbackSuccess ? 1 : 0,
                  callbackErrorMsg
               );
            return this.rkCallbackOk();
         }
      } catch (Exception var14) {
         log.error("Error processing request: " + var14.getMessage(), var14);
         this.callbackLogService
            .logCallback(
               "SERVER_CALLBACK",
               requestJson != null ? this.resolveCallbackAction(requestJson) : "UNKNOWN",
               requestJson != null ? requestJson.toJSONString() : requestBody,
               "回调失败",
               requestJson != null ? this.resolveCallbackParam(requestJson) : null,
               0,
               var14.getMessage()
            );
         return this.rkCallbackOk();
      }
   }

   private String readRequestBody(HttpServletRequest request) throws Exception {
      if (request == null) {
         return "";
      } else {
         StringBuilder body = new StringBuilder();

         String line;
         try (BufferedReader reader = request.getReader()) {
            while ((line = reader.readLine()) != null) {
               if (body.length() > 0) {
                  body.append('\n');
               }

               body.append(line);
            }
         }

         return body.toString();
      }
   }

   private JSONObject parseCallbackRequest(String requestBody, HttpServletRequest request) {
      String body = requestBody != null ? requestBody.trim() : "";
      if (!this.isBlank(body)) {
         try {
            return body.startsWith("{") ? JSON.parseObject(body) : this.parseParamString(body);
         } catch (Exception var6) {
            JSONObject json = new JSONObject();
            json.put("rawBody", requestBody);
            this.appendRequestParameters(json, request);
            return json;
         }
      } else {
         JSONObject json = new JSONObject();
         this.appendRequestParameters(json, request);
         return json;
      }
   }

   private JSONObject parseParamString(String body) {
      JSONObject json = new JSONObject();
      String[] pairs = body.split("&");

      for (String pair : pairs) {
         if (!this.isBlank(pair)) {
            int idx = pair.indexOf(61);
            String key = idx >= 0 ? pair.substring(0, idx) : pair;
            String value = idx >= 0 ? pair.substring(idx + 1) : "";
            json.put(this.urlDecode(key), this.urlDecode(value));
         }
      }

      return json;
   }

   private void appendRequestParameters(JSONObject json, HttpServletRequest request) {
      if (request != null) {
         for (Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
            String[] values = entry.getValue();
            json.put(entry.getKey(), values != null && values.length > 0 ? values[0] : "");
         }
      }
   }

   private String urlDecode(String value) {
      return URLDecoder.decode(value, StandardCharsets.UTF_8);
   }

   private JSONObject rkCallbackOk() {
      JSONObject json = new JSONObject();
      json.put("state", true);
      json.put("msg", "success");
      json.put("code", 0);
      return json;
   }

   private String resolveCallbackParam(JSONObject requestJson) {
      String callbackParam = this.firstNotBlank(
         requestJson.getString("callback_param"),
         requestJson.getString("callbackParam"),
         requestJson.getString("_BindId"),
         requestJson.getString("BindId"),
         requestJson.getString("bindId"),
         requestJson.getString("serverId")
      );
      return callbackParam != null ? callbackParam : requestJson.getString("VMName");
   }

   private String resolveCallbackAction(JSONObject requestJson) {
      String action = this.firstNotBlank(
         requestJson.getString("action"), requestJson.getString("Action"), requestJson.getString("taskType"), requestJson.getString("TaskType")
      );
      return action != null ? action : "rk_hyperv_callback";
   }

   private String resolveCallbackLogServerId(AutoTask task, String callbackParam, JSONObject requestJson) {
      if (task != null && !this.isBlank(task.getServerId())) {
         return task.getServerId();
      } else {
         String queueId = requestJson != null ? this.resolveQueueId(requestJson) : null;
         if (!this.isBlank(queueId)) {
            AutoTask queueTask = this.resolveRkCallbackTaskByQueueId(queueId, null);
            if (queueTask != null && !this.isBlank(queueTask.getServerId())) {
               return queueTask.getServerId();
            }
         }

         return callbackParam;
      }
   }

   private AutoTask handleRkHyperVCallback(JSONObject requestJson, String callbackParam) {
      AutoTask task = this.resolveRkCallbackTask(requestJson, callbackParam);
      if (task != null && !this.isBlank(task.getServerId())) {
         String targetStatus = this.resolveServerStatusByTaskType(task.getTaskType());
         if (targetStatus == null) {
            log.info("RkHyper-v回调任务类型无需更新云服务器状态，taskId={}, taskType={}", task.getId(), task.getTaskType());
            return task;
         } else {
            if ("CREATE".equals(task.getTaskType())) {
               this.createVps(task.getServerId());
            } else if (ServerStatusEnum.STOP.getCode().equals(targetStatus) && "DELETE".equals(task.getTaskType())) {
               this.removeVps(task.getServerId());
               this.serverService.saveResourceChangeLogAfterDeleteSuccess(task.getServerId());
            } else {
               this.serverService.updateServerStatusAndLog(task.getServerId(), targetStatus);
            }

            log.info(
               "RkHyper-v回调已更新云服务器状态，taskId={}, serverId={}, taskType={}, status={}",
               new Object[]{task.getId(), task.getServerId(), task.getTaskType(), targetStatus}
            );
            return task;
         }
      } else {
         log.warn("RkHyper-v回调未匹配到自动任务，callbackParam={}, request={}", callbackParam, requestJson.toJSONString());
         return null;
      }
   }

   private AutoTask handleRkHyperVDeleteNotFoundCallback(JSONObject requestJson, String callbackParam) {
      if (!this.isRkInstanceNotFound(requestJson)) {
         return null;
      } else {
         AutoTask task = this.resolveRkCallbackTask(requestJson, callbackParam);
         if (task == null || this.isBlank(task.getServerId())) {
            return null;
         } else if (!"DELETE".equals(task.getTaskType())) {
            log.warn("RkHyper-v实例不存在回调不是删除任务，不按删除成功处理，taskId={}, taskType={}", task.getId(), task.getTaskType());
            return null;
         } else {
            this.removeVps(task.getServerId());
            this.serverService.saveResourceChangeLogAfterDeleteSuccess(task.getServerId());
            log.info("RkHyper-v删除回调返回实例不存在，已按删除成功处理，taskId={}, serverId={}", task.getId(), task.getServerId());
            return task;
         }
      }
   }

   private AutoTask resolveRkCallbackTask(JSONObject requestJson, String callbackParam) {
      String queueId = this.resolveQueueId(requestJson);
      if (this.isBlank(queueId)) {
         log.warn("RkHyper-v回调未携带QueueID，callbackParam={}, request={}", callbackParam, requestJson.toJSONString());
         return null;
      } else {
         return this.resolveRkCallbackTaskByQueueId(queueId, requestJson);
      }
   }

   private AutoTask resolveRkCallbackTaskByQueueId(String queueId, JSONObject requestJson) {
      AutoTask task = (AutoTask)this.autoTaskTaskService.getById(queueId);
      if (task != null) {
         return task;
      } else {
         task = (AutoTask)this.autoTaskTaskService
            .getOne(
               (Wrapper<AutoTask>)((LambdaQueryWrapper<AutoTask>)((LambdaQueryWrapper<AutoTask>)new LambdaQueryWrapper<AutoTask>().like(AutoTask::getParams, "\"rkQueueId\":\"" + queueId + "\""))
                     .orderByDesc(AutoTask::getCreateTime))
                  .last("LIMIT 1")
            );
         if (task == null) {
            log.warn("RkHyper-v回调QueueID未匹配到任务，queueId={}, request={}", queueId, requestJson != null ? requestJson.toJSONString() : null);
         }

         return task;
      }
   }

   private String resolveQueueId(JSONObject requestJson) {
      String queueId = this.firstNotBlank(
         requestJson.getString("AnsyQueueId"),
         requestJson.getString("AsyncQueueId"),
         requestJson.getString("QueueID"),
         requestJson.getString("QueueId"),
         requestJson.getString("_QueueId"),
         requestJson.getString("queueId")
      );
      if (!this.isBlank(queueId)) {
         return this.cleanRkCallbackValue(queueId);
      } else {
         String executeResult = requestJson.getString("_ExecuteResult");
         if (!this.isBlank(executeResult)) {
            try {
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
            } catch (Exception var5) {
            }
         }

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

   private String resolveServerStatusByTaskType(String taskType) {
      if ("CREATE".equals(taskType)
         || "START".equals(taskType)
         || "RESTART".equals(taskType)
         || "REINSTALL".equals(taskType)
         || "REBUILD".equals(taskType)
         || "UPGRADE_BANDWIDTH".equals(taskType)
         || "UPGRADE_CPU_RAM".equals(taskType)
         || "UPGRADE_DISK".equals(taskType)
         || "UPGRADE_HARD_DISKS".equals(taskType)
         || "UPDATE_PASSWORD".equals(taskType)
         || "UNLOCK".equals(taskType)
         || "ADD_IP".equals(taskType)
         || "RESET_IP".equals(taskType)) {
         return ServerStatusEnum.RUNNING.getCode();
      } else if ("LOCK".equals(taskType)) {
         return ServerStatusEnum.LOCKED.getCode();
      } else if ("STOP".equals(taskType)) {
         return ServerStatusEnum.STOP.getCode();
      } else if ("POWER".equals(taskType)) {
         return ServerStatusEnum.POWER.getCode();
      } else {
         return "DELETE".equals(taskType) ? ServerStatusEnum.STOP.getCode() : null;
      }
   }

   private boolean isRkCallbackSuccess(JSONObject requestJson) {
      JSONObject resultJson = this.resolveRkExecuteResult(requestJson);
      Integer code = resultJson.getInteger("code");
      Boolean state = resultJson.getBoolean("state");
      return code == null && state == null ? true : (code == null || code == 0) && (state == null || Boolean.TRUE.equals(state));
   }

   private String buildRkCallbackErrorMsg(JSONObject requestJson) {
      if (this.isRkCallbackSuccess(requestJson)) {
         return null;
      } else {
         JSONObject resultJson = this.resolveRkExecuteResult(requestJson);
         String msg = resultJson.getString("msg");
         Integer code = resultJson.getInteger("code");
         JSONObject err = resultJson.getJSONObject("err");
         if (err != null && this.isBlank(msg)) {
            msg = err.getString("msg");
         }

         return this.firstNotBlank(msg, code != null ? "业务码：" + code : null, "RkHyper-v回调状态异常");
      }
   }

   private boolean isRkInstanceNotFound(JSONObject requestJson) {
      JSONObject resultJson = this.resolveRkExecuteResult(requestJson);
      String msg = resultJson.getString("msg");
      JSONObject err = resultJson.getJSONObject("err");
      if (err != null && this.isBlank(msg)) {
         msg = err.getString("msg");
      }

      return !this.isBlank(msg) && msg.contains("实例不存在");
   }

   private JSONObject resolveRkExecuteResult(JSONObject requestJson) {
      String executeResult = requestJson.getString("_ExecuteResult");
      if (!this.isBlank(executeResult)) {
         try {
            return JSON.parseObject(this.cleanRkCallbackValue(executeResult));
         } catch (Exception var4) {
            log.warn("RkHyper-v回调_ExecuteResult解析失败，executeResult={}", executeResult, var4);
         }
      }

      return requestJson;
   }

   private String firstNotBlank(String... values) {
      if (values == null) {
         return null;
      } else {
         for (String value : values) {
            if (!this.isBlank(value)) {
               return value;
            }
         }

         return null;
      }
   }

   private boolean isBlank(String value) {
      return value == null || value.trim().isEmpty();
   }

   private void updateVps(String taskid) {
      this.serverService.updateServerStatusAndLog(taskid, ServerStatusEnum.RUNNING.getCode());
   }

   private void createVps(String taskid) {
      log.info("create_vps 创建云服务器回调，服务器ID（A 系统）：{}", taskid);
      Server server = (Server)this.serverService.getById(taskid);
      if (server == null) {
         log.warn("⚠️ 未找到服务器信息，服务器ID：{}", taskid);
      } else {
         server.setServerStatus(ServerStatusEnum.RUNNING.getCode());
         this.serverService.updateById(server);
         log.info("create_vps 创建云服务器回调请求后 分配端口号，systemClass={}, systemName={}", server.getSystemClass(), server.getSystemName());
         this.createPrimaryPortMapping(server);
         this.serverService.updateServerStatusAndLog(taskid, ServerStatusEnum.RUNNING.getCode());
         if (server.getMasterId() != null) {
            this.hostStatsRefreshService.refreshByMasterId(server.getMasterId());
         }

         this.sendCloudServerActiveNotification(server);
      }
   }

   private void sendCloudServerActiveNotification(Server server) {
      if (server != null && !this.isBlank(server.getMemberNo()) && !this.isBlank(server.getHostName())) {
         try {
            Map<String, Object> templateParams = new HashMap<>();
            templateParams.put("serverName", server.getHostName());
            this.toolsNotificationApi
               .sendMemberNotificationBoth(NotificationTypeEnum.MEMBER_CLOUD_SERVER_ACTIVE.getCode(), Long.valueOf(server.getMemberNo()), templateParams);
         } catch (Exception var3) {
            log.warn("开通成功通知发送失败，serverId={}, memberNo={}, error={}", new Object[]{server.getId(), server.getMemberNo(), var3.getMessage()});
         }
      } else {
         log.warn("开通成功通知缺少会员或服务器名称，跳过发送，serverId={}", server != null ? server.getId() : null);
      }
   }

   private void createPrimaryPortMapping(Server server) {
      boolean windows = this.isWindowsServer(server);
      int privatePort = this.resolvePrimaryPrivatePort(server, windows);
      if (windows) {
         this.addServerPortMapping(server.getId(), "远程桌面", privatePort);
      } else {
         this.addServerPortMapping(server.getId(), "SSH", privatePort);
      }
   }

   private int resolvePrimaryPrivatePort(Server server, boolean windows) {
      Integer remotePort = server.getPort();
      if (remotePort != null && remotePort > 0 && remotePort <= 65535) {
         return remotePort;
      } else {
         return windows ? 3389 : 22;
      }
   }

   private void addServerPortMapping(String serverId, String portName, int privatePort) {
      ServerPortAddParam memberServerPortAddParam = new ServerPortAddParam();
      memberServerPortAddParam.setServerId(serverId);
      memberServerPortAddParam.setPortName(portName);
      memberServerPortAddParam.setPrivatePort(privatePort);
      this.serverPortService.add(memberServerPortAddParam);
   }

   private boolean isWindowsServer(Server server) {
      String os = this.resolveServerOsCategory(server);
      return os.contains("windows");
   }

   private String resolveServerOsCategory(Server server) {
      String systemClass = server.getSystemClass();
      if (systemClass != null) {
         return systemClass.toLowerCase(Locale.ROOT);
      } else {
         String systemName = server.getSystemName();
         return systemName == null ? "" : systemName.toLowerCase(Locale.ROOT);
      }
   }

   private void removeVps(String taskid) {
      Server server = (Server)this.serverService.getById(taskid);
      String masterId = server != null ? server.getMasterId() : null;
      ((UpdateChainWrapper)((UpdateChainWrapper)((UpdateChainWrapper)((UpdateChainWrapper)((UpdateChainWrapper)this.serverService.update().eq("id", taskid))
                     .set("server_status", ServerStatusEnum.STOP.getCode()))
                  .set("business_status", BusinessStatusEnum.DELETE.getCode()))
               .set("public_mac", null))
            .set("private_mac", null))
         .update();
      this.serverIpService.deleteByServerIdWithServerSnapshotFallback(taskid);
      if (server != null) {
         this.serverPortService.deleteVps(taskid);
      }

      if (masterId != null) {
         this.hostStatsRefreshService.refreshByMasterId(masterId);
      }
   }

   private void reinstallVps(String taskid) {
      this.serverService.updateServerStatusAndLog(taskid, ServerStatusEnum.RUNNING.getCode());
   }

   private void createSnapshotVps(String data) {
   }

   private void createBackupVps(String data) {
   }

   private void restoreSnapshotVps(String data) {
   }

   private void restoreBackupVps(String data) {
   }

   private void removeSnapshotVps(String data) {
   }

   private void removeBackupVps(String data) {
   }

   private void importIso(String data) {
   }

   private void installOS(String data) {
   }

   private void rescue(String data) {
   }

   private static PublicKey loadPublicKey(String publicKeyPem) throws Exception {
      String publicKeyStr = publicKeyPem.replace("-----BEGIN PUBLIC KEY-----", "").replace("-----END PUBLIC KEY-----", "").replaceAll("\\s+", "");
      byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyStr);
      X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
      KeyFactory keyFactory = KeyFactory.getInstance("RSA");
      return keyFactory.generatePublic(keySpec);
   }
}
