package com.rxinyun.admin.dev.modular.api.task;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rxinyun.admin.business.modular.esc.service.ServerIpService;
import com.rxinyun.admin.business.modular.independent.service.IndependentIpService;
import com.rxinyun.admin.dev.modular.api.entity.ResourcePool;
import com.rxinyun.admin.dev.modular.api.service.ResourcePoolService;
import com.rxinyun.admin.dev.modular.api.service.ResourcePoolSyncTimeService;
import com.rxinyun.business.api.IndependentApi;
import com.rxinyun.business.api.ServerApi;
import com.rxinyun.business.core.pojo.BaseIndependent;
import com.rxinyun.business.core.pojo.BaseServer;
import com.rxinyun.common.enums.business.BusinessStatusEnum;
import com.rxinyun.common.enums.business.ServerStatusEnum;
import com.rxinyun.common.timer.CommonTimerTaskRunner;
import com.rxinyun.common.util.CommonCryptogramUtil;
import com.rxinyun.common.util.TimeUtil;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ResourcePoolSyncTaskRunner implements CommonTimerTaskRunner {
   private static final Logger log = LoggerFactory.getLogger(ResourcePoolSyncTaskRunner.class);
   @Resource
   private ResourcePoolService resourcePoolService;
   @Resource
   private ServerApi serverApi;
   @Resource
   private IndependentApi independentApi;
   @Resource
   private IndependentIpService independentIpService;
   @Resource
   private ServerIpService serverIpService;
   @Resource
   private ResourcePoolSyncTimeService resourcePoolSyncTimeService;

   public void action() {
      log.info("开始执行资源池同步任务");
      List<ResourcePool> pools = this.resourcePoolService.listEnabled();
      if (pools != null && !pools.isEmpty()) {
         log.info("找到 {} 个启用的资源池", pools.size());
         int successCount = 0;

         for (ResourcePool pool : pools) {
            try {
               this.syncFromUpstream(pool);
               successCount++;
            } catch (Exception var6) {
               log.error("同步资源池失败：{}", pool.getName(), var6);
            }
         }

         log.info("资源池同步完成，成功{}/{}", successCount, pools.size());
      } else {
         log.debug("没有启用的资源池，跳过同步");
      }
   }

   private void syncFromUpstream(ResourcePool pool) {
      log.info("开始同步资源池：{}", pool.getName());
      LocalDateTime lastSyncTime = this.resourcePoolSyncTimeService.getLastSyncTime(pool.getId());
      log.info("最后同步时间：{}", lastSyncTime);

      List<Map<String, Object>> changeLogs;
      try {
         changeLogs = this.queryUpstreamChangeLogs(pool, lastSyncTime, 100);
      } catch (Exception var9) {
         log.error("查询上游变更日志失败，资源池：{}", pool.getName(), var9);
         return;
      }

      if (changeLogs != null && !changeLogs.isEmpty()) {
         log.info("找到 {} 条变更日志，资源池：{}", changeLogs.size(), pool.getName());
         int successCount = 0;

         for (Map<String, Object> changeLog : changeLogs) {
            try {
               this.processChangeLog(changeLog, pool);
               successCount++;
            } catch (Exception var8) {
               log.error("处理变更日志失败，资源池：{}, 变更 ID: {}", new Object[]{pool.getName(), changeLog.get("id"), var8});
            }
         }

         log.info("资源池 {} 同步完成，成功{}/{}", new Object[]{pool.getName(), successCount, changeLogs.size()});
         this.resourcePoolSyncTimeService.updateLastSyncTime(pool.getId(), TimeUtil.now());
      } else {
         log.info("资源池 {} 本次上游未返回待处理变更（可能确实无单，或已全部处理）", pool.getName());
         this.resourcePoolSyncTimeService.updateLastSyncTime(pool.getId(), TimeUtil.now());
      }
   }

   private void processChangeLog(Map<String, Object> changeLog, ResourcePool pool) {
      String orderNo = (String)changeLog.get("orderNo");
      String changeType = (String)changeLog.get("changeType");
      String logId = (String)changeLog.get("id");
      log.info("处理变更日志，订单号：{}，变更类型：{}，资源池：{}", new Object[]{orderNo, changeType, pool.getName()});
      String upstreamServerId = this.resolveUpstreamServerId(changeLog);
      if (StrUtil.isBlank(upstreamServerId)) {
         log.warn("变更日志缺少 upstreamServerId，无法回源查询详情，logId: {}", logId);
         this.markChangeLogAsProcessedSafe(pool, logId);
      } else {
         String targetHint = this.resolvePoolSyncTarget(changeLog);
         JSONObject currentDetail = this.queryUpstreamServerDetail(pool, upstreamServerId, targetHint);
         if (currentDetail != null && !currentDetail.isEmpty()) {
            switch (changeType) {
               case "CREATE":
               case "UPDATE":
                  this.syncServerInfo(currentDetail, pool, orderNo);
                  break;
               case "DELETE":
                  this.handleServerDelete(currentDetail, pool, orderNo);
                  break;
               default:
                  log.warn("未知的变更类型：{}，logId: {}", changeType, logId);
            }

            this.markChangeLogAsProcessedSafe(pool, logId);
         } else {
            throw new RuntimeException("查询上游当前服务器详情为空，upstreamServerId=" + upstreamServerId);
         }
      }
   }

   private void syncServerInfo(JSONObject data, ResourcePool pool, String orderNo) {
      String upstreamServerId = ResourcePoolSyncDataApplier.resolveUpstreamServerId(data);
      if (StrUtil.isBlank(upstreamServerId)) {
         log.warn("变更日志缺少 upstreamServerId（A 实例主键），无法定位 B 侧记录，orderNo: {}，data: {}", orderNo, data);
      } else {
         ResourcePoolSyncTaskRunner.PoolSyncRowKind kind = this.resolvePoolSyncRowKind(data, upstreamServerId);
         if (kind == ResourcePoolSyncTaskRunner.PoolSyncRowKind.UNKNOWN) {
            log.warn("无法判断变更归属云服务器或独立服务器，跳过以免写入错误业务表，upstreamServerId={}，orderNo={}", upstreamServerId, orderNo);
         } else if (kind == ResourcePoolSyncTaskRunner.PoolSyncRowKind.INDEPENDENT) {
            this.syncIndependentServerInfo(data, orderNo, upstreamServerId);
         } else {
            log.info("同步云服务器信息（BUSINESS_SERVER），upstreamServerId: {}，订单号: {}", upstreamServerId, orderNo);
            BaseServer existingServer = this.serverApi.selectByServerId(upstreamServerId);
            if (existingServer == null) {
               log.warn("B 系统中未找到云服务器记录（按 UPSTREAM_SERVER_ID/主键），upstreamServerId: {}，orderNo: {}", upstreamServerId, orderNo);
            } else {
               String localId = existingServer.getId();
               ResourcePoolSyncDataApplier.apply(data, existingServer, localId);

               try {
                  this.serverApi.syncFromUpstream(existingServer);
                  if (StrUtil.isNotBlank(existingServer.getId())) {
                     this.serverIpService.replaceFromResourcePoolUpstreamSnapshot(existingServer.getId(), data);
                  }

                  log.info(
                     "云服务器信息同步成功，localServerId: {}，状态: {}，公网IP: {}，订单号: {}",
                     new Object[]{localId, existingServer.getServerStatus(), existingServer.getPublicIp(), orderNo}
                  );
               } catch (Exception var9) {
                  log.error("保存云服务器信息失败，localServerId: {}，订单号: {}，错误: {}", new Object[]{localId, orderNo, var9.getMessage(), var9});
                  throw new RuntimeException("保存云服务器信息失败：" + var9.getMessage(), var9);
               }
            }
         }
      }
   }

   private ResourcePoolSyncTaskRunner.PoolSyncRowKind resolvePoolSyncRowKind(JSONObject data, String upstreamSid) {
      if (data != null && !StrUtil.isBlank(upstreamSid)) {
         String target = StrUtil.trimToEmpty(data.getString("poolSyncTarget"));
         if (StrUtil.equalsIgnoreCase(target, "INDEPENDENT") || StrUtil.equalsIgnoreCase(target, "DEDICATED")) {
            return ResourcePoolSyncTaskRunner.PoolSyncRowKind.INDEPENDENT;
         } else if (StrUtil.equalsIgnoreCase(target, "CLOUD") || StrUtil.equalsIgnoreCase(target, "ECS")) {
            return ResourcePoolSyncTaskRunner.PoolSyncRowKind.CLOUD;
         } else if (this.independentApi.getById(upstreamSid) != null) {
            return ResourcePoolSyncTaskRunner.PoolSyncRowKind.INDEPENDENT;
         } else if (this.serverApi.selectByServerId(upstreamSid) != null) {
            return ResourcePoolSyncTaskRunner.PoolSyncRowKind.CLOUD;
         } else if (StrUtil.isNotBlank(data.getString("machineId")) && StrUtil.isBlank(data.getString("hostName"))) {
            return ResourcePoolSyncTaskRunner.PoolSyncRowKind.INDEPENDENT;
         } else {
            return StrUtil.isNotBlank(data.getString("hostName"))
               ? ResourcePoolSyncTaskRunner.PoolSyncRowKind.CLOUD
               : ResourcePoolSyncTaskRunner.PoolSyncRowKind.UNKNOWN;
         }
      } else {
         return ResourcePoolSyncTaskRunner.PoolSyncRowKind.UNKNOWN;
      }
   }

   private void syncIndependentServerInfo(JSONObject data, String orderNo, String upstreamServerId) {
      log.info("同步独立服务器信息（BUSINESS_INDEPENDENT），upstreamServerId: {}，订单号: {}", upstreamServerId, orderNo);
      BaseIndependent existing = this.independentApi.getById(upstreamServerId);
      if (existing == null) {
         log.warn("B 系统中未找到独立服务器记录（按主键或 UPSTREAM_SERVER_ID），upstreamServerId: {}，orderNo: {}", upstreamServerId, orderNo);
      } else {
         String localId = existing.getId();
         ResourcePoolSyncDataApplier.applyToIndependent(data, existing, localId);

         try {
            this.independentApi.syncFromUpstream(existing);
            if (StrUtil.isNotBlank(existing.getId())) {
               this.independentIpService.replaceFromResourcePoolUpstreamSnapshot(existing.getId(), data);
            }

            log.info(
               "独立服务器信息同步成功，localServerId: {}，状态: {}，serverIp: {}，订单号: {}", new Object[]{localId, existing.getServerStatus(), existing.getServerIp(), orderNo}
            );
         } catch (Exception var7) {
            log.error("保存独立服务器信息失败，localServerId: {}，orderNo: {}，错误: {}", new Object[]{localId, orderNo, var7.getMessage(), var7});
            throw new RuntimeException("保存独立服务器信息失败：" + var7.getMessage(), var7);
         }
      }
   }

   private void handleServerDelete(JSONObject data, ResourcePool pool, String orderNo) {
      String upstreamServerId = ResourcePoolSyncDataApplier.resolveUpstreamServerId(data);
      if (StrUtil.isBlank(upstreamServerId)) {
         log.warn("DELETE 变更日志缺少 upstreamServerId，无法处理，orderNo: {}", orderNo);
      } else {
         log.info("处理删除类变更，upstreamServerId: {}，订单号: {}", upstreamServerId, orderNo);

         try {
            ResourcePoolSyncTaskRunner.PoolSyncRowKind kind = this.resolvePoolSyncRowKind(data, upstreamServerId);
            if (kind == ResourcePoolSyncTaskRunner.PoolSyncRowKind.UNKNOWN) {
               log.warn("DELETE 变更无法路由云机/独立机，跳过，upstreamServerId={}，orderNo={}", upstreamServerId, orderNo);
            } else if (kind == ResourcePoolSyncTaskRunner.PoolSyncRowKind.INDEPENDENT) {
               BaseIndependent independent = this.independentApi.getById(upstreamServerId);
               if (independent == null) {
                  log.warn("DELETE 独立机：B 侧无记录，upstreamServerId: {}", upstreamServerId);
               } else {
                  String localId = independent.getId();
                  ResourcePoolSyncDataApplier.applyToIndependent(data, independent, localId);
                  independent.setServerStatus(ServerStatusEnum.STOP.getCode());
                  independent.setBusinessStatus(BusinessStatusEnum.DELETE.getCode());
                  this.independentApi.syncFromUpstream(independent);
                  log.info("已同步上游 DELETE（独立服务器，已合并快照并强制定终态），localServerId={}", localId);
               }
            } else {
               BaseServer server = this.serverApi.selectByServerId(upstreamServerId);
               if (server == null) {
                  log.warn("B 系统中未找到待删除的云服务器记录，upstreamServerId: {}", upstreamServerId);
               } else {
                  String localId = server.getId();
                  ResourcePoolSyncDataApplier.apply(data, server, localId);
                  server.setServerStatus(ServerStatusEnum.STOP.getCode());
                  server.setBusinessStatus(BusinessStatusEnum.DELETE.getCode());
                  this.serverApi.syncFromUpstream(server);
                  log.info("已同步上游 DELETE（云服务器，已合并快照并强制定终态），localServerId={}", localId);
               }
            }
         } catch (Exception var8) {
            log.error("处理删除变更失败，upstreamServerId: {}，错误: {}", new Object[]{upstreamServerId, var8.getMessage(), var8});
            throw new RuntimeException("处理删除变更失败：" + var8.getMessage(), var8);
         }
      }
   }

   private List<Map<String, Object>> queryUpstreamChangeLogs(ResourcePool pool, LocalDateTime lastSyncTime, Integer limit) {
      String requestUrl = this.buildUrl(pool.getApiUrl(), "api/resource-pool/queryPendingChangeLogs");
      log.info("查询上游变更日志，URL: {}，资源池: {}", requestUrl, pool.getName());
      String apiSecret = CommonCryptogramUtil.doSm4CbcDecrypt(pool.getApiSecret());
      JSONObject params = new JSONObject();
      params.put("lastSyncTime", lastSyncTime != null ? lastSyncTime.toString() : null);
      params.put("limit", limit);

      try {
         HttpResponse response = ((HttpRequest)((HttpRequest)((HttpRequest)HttpRequest.post(requestUrl).header("Content-Type", "application/json"))
                  .header("X-API-Key", pool.getApiKey()))
               .header("X-API-Secret", apiSecret))
            .body(params.toString())
            .timeout(30000)
            .execute();
         this.assertHttpOk(response, "查询上游变更日志");
         JSONObject responseJson = JSON.parseObject(response.body());
         this.assertBizSuccess(responseJson, "查询上游变更日志");
         JSONArray data = responseJson.getJSONArray("data");
         List<Map<String, Object>> result = new ArrayList<>();
         if (data != null) {
            for (Object item : data) {
               result.add((Map<String, Object>)item);
            }
         }
         return result;
      } catch (Exception var10) {
         log.error("HTTP 查询上游变更日志失败，资源池: {}，错误: {}", new Object[]{pool.getName(), var10.getMessage(), var10});
         throw new RuntimeException("查询上游变更日志失败：" + var10.getMessage(), var10);
      }
   }

   private JSONObject queryUpstreamServerDetail(ResourcePool pool, String upstreamServerId, String poolSyncTarget) {
      String requestUrl = this.buildUrl(pool.getApiUrl(), "api/resource-pool/queryServerDetail");
      String apiSecret = CommonCryptogramUtil.doSm4CbcDecrypt(pool.getApiSecret());
      JSONObject params = new JSONObject();
      params.put("upstreamServerId", upstreamServerId);
      if (StrUtil.isNotBlank(poolSyncTarget)) {
         params.put("poolSyncTarget", poolSyncTarget);
      }

      try {
         HttpResponse response = ((HttpRequest)((HttpRequest)((HttpRequest)HttpRequest.post(requestUrl).header("Content-Type", "application/json"))
                  .header("X-API-Key", pool.getApiKey()))
               .header("X-API-Secret", apiSecret))
            .body(params.toString())
            .timeout(30000)
            .execute();
         this.assertHttpOk(response, "查询上游服务器详情");
         JSONObject responseJson = JSON.parseObject(response.body());
         this.assertBizSuccess(responseJson, "查询上游服务器详情");
         JSONObject data = responseJson.getJSONObject("data");
         if (data != null && StrUtil.isBlank(data.getString("upstreamServerId"))) {
            data.put("upstreamServerId", upstreamServerId);
         }

         return data;
      } catch (Exception var10) {
         log.error("HTTP 查询上游服务器详情失败，资源池: {}，upstreamServerId: {}，错误: {}", new Object[]{pool.getName(), upstreamServerId, var10.getMessage(), var10});
         throw new RuntimeException("查询上游服务器详情失败：" + var10.getMessage(), var10);
      }
   }

   private void markChangeLogAsProcessed(ResourcePool pool, String logId) {
      String requestUrl = this.buildUrl(pool.getApiUrl(), "api/resource-pool/markChangeLogAsProcessed");
      log.debug("标记变更日志为已处理，URL: {}，logId: {}", requestUrl, logId);
      String apiSecret = CommonCryptogramUtil.doSm4CbcDecrypt(pool.getApiSecret());
      JSONObject params = new JSONObject();
      params.put("logId", logId);

      try {
         HttpResponse response = ((HttpRequest)((HttpRequest)((HttpRequest)HttpRequest.post(requestUrl).header("Content-Type", "application/json"))
                  .header("X-API-Key", pool.getApiKey()))
               .header("X-API-Secret", apiSecret))
            .body(params.toString())
            .timeout(30000)
            .execute();
         this.assertHttpOk(response, "标记变更日志");
         this.assertBizSuccess(JSON.parseObject(response.body()), "标记变更日志");
         log.debug("成功标记变更日志为已处理，logId: {}", logId);
      } catch (Exception var7) {
         log.error("HTTP 标记变更日志失败，logId: {}，错误: {}", new Object[]{logId, var7.getMessage(), var7});
         throw new RuntimeException("标记变更日志失败：" + var7.getMessage(), var7);
      }
   }

   private void markChangeLogAsProcessedSafe(ResourcePool pool, String logId) {
      try {
         this.markChangeLogAsProcessed(pool, logId);
      } catch (Exception var4) {
         log.error("标记变更日志为已处理失败（非致命），logId: {}，错误: {}", logId, var4.getMessage());
      }
   }

   private String buildUrl(String baseUrl, String path) {
      if (!baseUrl.endsWith("/")) {
         baseUrl = baseUrl + "/";
      }

      return baseUrl + path;
   }

   private void assertHttpOk(HttpResponse response, String action) {
      if (!response.isOk()) {
         throw new RuntimeException(action + " HTTP 请求失败，状态码：" + response.getStatus());
      }
   }

   private void assertBizSuccess(JSONObject responseJson, String action) {
      Integer code = responseJson.getInteger("code");
      if (code == null || code != 200) {
         throw new RuntimeException(action + " 业务失败：" + responseJson.getString("msg"));
      }
   }

   private String resolveUpstreamServerId(Map<String, Object> changeLog) {
      Object upstreamTop = changeLog.get("upstreamServerId");
      if (upstreamTop != null) {
         String u = StrUtil.trimToNull(String.valueOf(upstreamTop));
         if (u != null) {
            return u;
         }
      }

      return null;
   }

   private String resolvePoolSyncTarget(Map<String, Object> changeLog) {
      Object top = changeLog.get("poolSyncTarget");
      if (top != null) {
         String t = StrUtil.trimToNull(String.valueOf(top));
         if (t != null) {
            return t;
         }
      }

      return null;
   }

   private static enum PoolSyncRowKind {
      CLOUD,
      INDEPENDENT,
      UNKNOWN;
   }
}
