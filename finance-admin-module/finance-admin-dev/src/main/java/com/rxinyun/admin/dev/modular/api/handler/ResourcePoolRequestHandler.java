package com.rxinyun.admin.dev.modular.api.handler;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rxinyun.admin.business.modular.esc.entity.Server;
import com.rxinyun.admin.business.modular.esc.service.ServerService;
import com.rxinyun.admin.business.modular.independent.entity.Independent;
import com.rxinyun.admin.business.modular.independent.service.IndependentService;
import com.rxinyun.admin.dev.modular.api.entity.ResourcePool;
import com.rxinyun.admin.dev.modular.api.entity.ResourcePoolRequest;
import com.rxinyun.admin.dev.modular.api.service.ResourcePoolRequestService;
import com.rxinyun.admin.dev.modular.api.service.ResourcePoolService;
import com.rxinyun.common.enums.ResourcePoolRequestStatusEnum;
import com.rxinyun.common.enums.order.ProductTypeEnum;
import com.rxinyun.common.util.CommonCryptogramUtil;
import com.rxinyun.order.api.OrderServerApi;
import com.rxinyun.order.core.pojo.BaseOrderServer;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ResourcePoolRequestHandler {
   private static final Logger log = LoggerFactory.getLogger(ResourcePoolRequestHandler.class);
   @Resource
   private ResourcePoolRequestService resourcePoolRequestService;
   @Resource
   private ResourcePoolService resourcePoolService;
   @Resource
   private OrderServerApi orderServerApi;
   @Resource
   private ServerService serverService;
   @Resource
   private IndependentService independentService;

   public void scanAndSendPendingRequests() {
      List<ResourcePoolRequest> pendingRequests = this.resourcePoolRequestService.listPendingRequests();
      if (ObjectUtil.isEmpty(pendingRequests)) {
         log.debug("没有待发送的资源池请求");
      } else {
         log.info("开始处理待发送的资源池请求，数量：{}", pendingRequests.size());

         for (ResourcePoolRequest request : pendingRequests) {
            try {
               this.sendRequest(request);
            } catch (Exception var5) {
               log.error("发送资源池请求失败，请求ID：{}，订单号：{}，错误：{}", new Object[]{request.getId(), request.getOrderNo(), var5.getMessage(), var5});
               this.resourcePoolRequestService.updateStatus(request.getId(), ResourcePoolRequestStatusEnum.FAILED.getCode(), "发送失败：" + var5.getMessage());
            }
         }
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   public void sendRequest(ResourcePoolRequest request) {
      log.info("开始发送资源池请求，请求ID：{}，订单号：{}，操作类型：{}", new Object[]{request.getId(), request.getOrderNo(), request.getOperationType()});
      ResourcePool resourcePool = this.resourcePoolService.getByIdForApi(request.getResourcePoolId());
      if (resourcePool == null) {
         throw new RuntimeException("资源池不存在或已禁用，资源池ID：" + request.getResourcePoolId());
      } else {
         String apiSecret = CommonCryptogramUtil.doSm4CbcDecrypt(resourcePool.getApiSecret());
         JSONObject requestParams = JSONUtil.parseObj(request.getRequestParams());
         if (StrUtil.isNotBlank(request.getOrderNo())) {
            if (StrUtil.isBlank(requestParams.getStr("orderNo"))) {
               requestParams.set("orderNo", request.getOrderNo());
            }

            if (StrUtil.isBlank(requestParams.getStr("downstreamOrderNo"))) {
               requestParams.set("downstreamOrderNo", request.getOrderNo());
            }
         }

         String operationType = request.getOperationType();
         String apiUrl = resourcePool.getApiUrl();
         if (!apiUrl.endsWith("/")) {
            apiUrl = apiUrl + "/";
         }

         String requestUrl = this.buildRequestUrl(request, apiUrl, operationType, requestParams);
         log.info("发送给 A 系统的请求 URL：{}，操作类型：{}，productType：{}", new Object[]{requestUrl, operationType, request.getProductType()});
         if ("DELETE".equals(operationType)
            && StrUtil.isBlank(StrUtil.trimToNull(requestParams.getStr("upstreamServerId")))
            && StrUtil.isNotBlank(request.getAServerId())) {
            requestParams.set("upstreamServerId", request.getAServerId());
         }

         if ("CREATE".equals(operationType)) {
            List<BaseOrderServer> baseOrderServers = this.orderServerApi.queryByOrderNo(request.getOrderNo());
            if (CollectionUtil.isNotEmpty(baseOrderServers)) {
               List<String> serverIds = baseOrderServers.stream().<String>map(BaseOrderServer::getServerId).collect(Collectors.toList());
               String serverIdsJson = JSON.toJSONString(serverIds);
               requestParams.set("callback_params", serverIdsJson);
               log.info("✅ 已推送 {} 台服务器给上游，订单号：{}，服务器ID 列表：{}", new Object[]{serverIds.size(), request.getOrderNo(), serverIds});
            } else {
               log.warn("⚠️ 订单下没有 ORDER_SERVER 记录，无法推送 B 侧服务器 ID，订单号：{}", request.getOrderNo());
            }
         }

         JSONObject upstreamParams = JSONUtil.parseObj(requestParams);
         this.enrichIndependentPoolUpstreamIdentityForUpstreamRequest(upstreamParams, request, operationType);
         upstreamParams.remove("payPrice");
         upstreamParams.remove("renewPrice");
         HttpResponse response = ((HttpRequest)((HttpRequest)((HttpRequest)HttpRequest.post(requestUrl).header("Content-Type", "application/json"))
                  .header("X-API-Key", resourcePool.getApiKey()))
               .header("X-API-Secret", apiSecret))
            .body(upstreamParams.toString())
            .timeout(30000)
            .execute();
         String responseBody = response.body();
         if (response.isOk()) {
            log.info("资源池请求发送成功，请求ID：{}，响应：{}", request.getId(), responseBody);

            try {
               JSONObject responseJson = JSONUtil.parseObj(responseBody);
               Integer code = responseJson.getInt("code");
               String msg = responseJson.getStr("msg");
               if (code != null && code == 200) {
                  this.resourcePoolRequestService.updateStatus(request.getId(), ResourcePoolRequestStatusEnum.SENT.getCode(), responseBody);
                  if ("CREATE".equals(operationType)) {
                     this.persistUpstreamIdsAfterResourcePoolCreate(request, responseJson);
                  }

                  this.syncLocalBusinessServerAfterUpstreamSuccess(request, operationType, requestParams);
               } else {
                  String errorMsg = msg != null ? msg : "资源池API返回错误";
                  log.error(
                     "资源池请求业务失败，请求ID：{}，订单号：{}，操作类型：{}，错误码：{}，错误信息：{}", new Object[]{request.getId(), request.getOrderNo(), operationType, code, errorMsg}
                  );
                  this.resourcePoolRequestService.updateStatus(request.getId(), ResourcePoolRequestStatusEnum.FAILED.getCode(), responseBody);
                  throw new RuntimeException("资源池API业务失败：" + errorMsg);
               }
            } catch (Exception var15) {
               log.error("解析资源池响应失败，请求ID：{}，响应：{}，错误：{}", new Object[]{request.getId(), responseBody, var15.getMessage(), var15});
               this.resourcePoolRequestService.updateStatus(request.getId(), ResourcePoolRequestStatusEnum.FAILED.getCode(), responseBody);
               throw new RuntimeException("解析资源池响应失败：" + var15.getMessage());
            }
         } else {
            log.error("资源池请求发送失败，请求ID：{}，状态码：{}，响应：{}", new Object[]{request.getId(), response.getStatus(), responseBody});
            this.resourcePoolRequestService.updateStatus(request.getId(), ResourcePoolRequestStatusEnum.FAILED.getCode(), responseBody);
            throw new RuntimeException("资源池API请求失败，状态码：" + response.getStatus());
         }
      }
   }

   private void syncLocalBusinessServerAfterUpstreamSuccess(ResourcePoolRequest request, String operationType, JSONObject requestParams) {
      if (!StrUtil.isBlank(operationType) && !"CREATE".equals(operationType)) {
         String sid = StrUtil.trimToNull(requestParams.getStr("upstreamServerId"));
         if (StrUtil.isBlank(sid)) {
            sid = StrUtil.trimToNull(request.getAServerId());
         }

         if (!StrUtil.isBlank(sid)) {
            if ("RENEW".equals(operationType) && request.getMonths() != null && requestParams.getInt("months") == null) {
               requestParams.set("months", request.getMonths());
            }

            try {
               Independent independent = this.resolveIndependentForPoolLocalApply(sid);
               if (this.isPoolManagedIndependent(independent)) {
                  this.independentService
                     .applyLocalResourcePoolAfterUpstreamSuccess(operationType, independent.getId(), requestParams.toString(), request.getOrderNo());
                  return;
               }

               Server cloudServer = this.resolveCloudServerForPoolLocalApply(sid);
               if (this.isPoolManagedCloudServer(cloudServer)) {
                  this.serverService
                     .applyLocalResourcePoolAfterUpstreamSuccess(operationType, cloudServer.getId(), requestParams.toString(), request.getOrderNo());
               }
            } catch (Exception var7) {
               log.error("上游已成功，但 B 侧资源池实例同步失败，requestId={}，op={}", new Object[]{request.getId(), operationType, var7});
            }
         }
      }
   }

   private Independent resolveIndependentForPoolLocalApply(String upstreamOrBId) {
      if (StrUtil.isBlank(upstreamOrBId)) {
         return null;
      } else {
         Independent row = (Independent)this.independentService.getById(upstreamOrBId);
         return row != null
            ? row
            : (Independent)this.independentService
               .getOne((Wrapper<Independent>)((LambdaQueryWrapper<Independent>)new LambdaQueryWrapper<Independent>().eq(Independent::getUpstreamServerId, upstreamOrBId)).last("LIMIT 1"));
      }
   }

   private Server resolveCloudServerForPoolLocalApply(String upstreamOrBId) {
      if (StrUtil.isBlank(upstreamOrBId)) {
         return null;
      } else {
         Server row = (Server)this.serverService.getById(upstreamOrBId);
         return row != null
            ? row
            : (Server)this.serverService
               .getOne((Wrapper<Server>)((LambdaQueryWrapper<Server>)new LambdaQueryWrapper<Server>().eq(Server::getUpstreamServerId, upstreamOrBId)).last("LIMIT 1"));
      }
   }

   private boolean isPoolManagedIndependent(Independent independent) {
      return independent != null
         && (
            Integer.valueOf(1).equals(independent.getType())
               || StrUtil.isNotBlank(independent.getResourcePoolId())
               || StrUtil.isNotBlank(independent.getUpstreamServerId())
         );
   }

   private boolean isPoolManagedCloudServer(Server cloudServer) {
      return cloudServer != null
         && (
            Integer.valueOf(1).equals(cloudServer.getType())
               || StrUtil.isNotBlank(cloudServer.getResourcePoolId())
               || StrUtil.isNotBlank(cloudServer.getUpstreamServerId())
         );
   }

   private boolean routeToIndependentOrderApi(ResourcePoolRequest row, JSONObject params, String operationType) {
      Integer pt = row.getProductType();
      if (ProductTypeEnum.INDEPENDENT_SERVER.getCode().equals(pt)) {
         return true;
      } else if (ProductTypeEnum.CLOUD_SERVER.getCode().equals(pt)) {
         return false;
      } else if ("CREATE".equals(operationType) && params != null && StrUtil.isNotBlank(params.getStr("machineId"))) {
         return true;
      } else {
         if (params != null && isRenewOrIndependentUpgradeOperation(operationType)) {
            String sid = StrUtil.trimToNull(params.getStr("upstreamServerId"));
            if (StrUtil.isBlank(sid)) {
               sid = StrUtil.trimToNull(row.getAServerId());
            }

            if (StrUtil.isNotBlank(sid)) {
               Independent indep = (Independent)this.independentService.getById(sid);
               if (indep == null) {
                  indep = (Independent)this.independentService.getOne((Wrapper<Independent>)new LambdaQueryWrapper<Independent>().eq(Independent::getUpstreamServerId, sid));
               }

               if (indep != null) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   private static boolean isRenewOrIndependentUpgradeOperation(String operationType) {
      return "RENEW".equals(operationType)
         || "UPGRADE_BANDWIDTH".equals(operationType)
         || "UPGRADE_DEFENSE".equals(operationType)
         || "UPGRADE_IP".equals(operationType);
   }

   private void enrichIndependentPoolUpstreamIdentityForUpstreamRequest(JSONObject upstreamParams, ResourcePoolRequest request, String operationType) {
      if (isRenewOrIndependentUpgradeOperation(operationType)) {
         if (this.routeToIndependentOrderApi(request, upstreamParams, operationType)) {
            if (!StrUtil.isNotBlank(StrUtil.trimToNull(upstreamParams.getStr("upstreamServerId")))) {
               String fromRow = StrUtil.trimToNull(request.getAServerId());
               if (StrUtil.isNotBlank(fromRow)) {
                  upstreamParams.set("upstreamServerId", fromRow);
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

   private String buildRequestUrl(ResourcePoolRequest row, String baseUrl, String operationType, JSONObject requestParams) {
      switch (operationType) {
         case "RENEW":
            if (this.routeToIndependentOrderApi(row, requestParams, operationType)) {
               return baseUrl + "api/order/independent/renew";
            }

            return baseUrl + "api/order/renew";
         case "UPGRADE_DISK":
            return baseUrl + "api/order/upgrade/disk";
         case "UPGRADE_BANDWIDTH":
            if (this.routeToIndependentOrderApi(row, requestParams, operationType)) {
               return baseUrl + "api/order/independent/upgrade/bandwidth";
            }

            return baseUrl + "api/order/upgrade/bandwidth";
         case "UPGRADE_CPU_RAM":
            return baseUrl + "api/order/upgrade/cpu-ram";
         case "UPGRADE_DEFENSE":
            if (this.routeToIndependentOrderApi(row, requestParams, operationType)) {
               return baseUrl + "api/order/independent/upgrade/defense";
            }

            return baseUrl + "api/order/upgrade/defense";
         case "UPGRADE_IP":
            if (this.routeToIndependentOrderApi(row, requestParams, operationType)) {
               return baseUrl + "api/order/independent/upgrade/ip";
            }

            return baseUrl + "api/order/upgrade/ip";
         case "DELETE":
            if (this.routeToIndependentOrderApi(row, requestParams, operationType)) {
               return baseUrl + "api/order/independent/delete";
            }

            return baseUrl + "api/order/delete";
         case "CREATE":
            if (this.routeToIndependentOrderApi(row, requestParams, operationType)) {
               return baseUrl + "api/order/independent/create";
            }

            return baseUrl + "api/order/create";
         default:
            return baseUrl + "api/order/create";
      }
   }

   private void persistUpstreamIdsAfterResourcePoolCreate(ResourcePoolRequest request, JSONObject responseJson) {
      List<BaseOrderServer> orderServers = this.orderServerApi.queryByOrderNo(request.getOrderNo());
      if (CollectionUtil.isEmpty(orderServers)) {
         log.warn("CREATE 成功但无 ORDER_SERVER，无法回写上游 ID，orderNo={}", request.getOrderNo());
      } else {
         List<String> upstreamIds = parseUpstreamIdsFromPoolCreateResponse(responseJson);
         if (upstreamIds.isEmpty()) {
            log.warn("CREATE 成功但未解析到实例主键（请 A 在 data 中返回 upstreamServerId 或 id / serverIds 数组），orderNo={}", request.getOrderNo());
         } else {
            boolean independent = this.routeToIndependentOrderApi(request, JSONUtil.parseObj(request.getRequestParams()), "CREATE");

            for (int i = 0; i < orderServers.size(); i++) {
               String bSid = orderServers.get(i).getServerId();
               String aSid = upstreamIds.size() == 1 ? upstreamIds.get(0) : (i < upstreamIds.size() ? upstreamIds.get(i) : null);
               if (!StrUtil.isBlank(bSid) && !StrUtil.isBlank(aSid)) {
                  if (independent) {
                     Independent ind = (Independent)this.independentService.getById(bSid);
                     if (ind != null) {
                        ind.setUpstreamServerId(aSid.trim());
                        this.independentService.updateById(ind);
                        log.info("已回写独立服务器上游 ID：B id={} upstream={}", bSid, aSid);
                     }
                  } else {
                     Server s = (Server)this.serverService.getById(bSid);
                     if (s != null) {
                        s.setUpstreamServerId(aSid.trim());
                        this.serverService.updateById(s);
                        log.info("已回写云服务器上游 ID：B id={} upstream={}", bSid, aSid);
                     }
                  }
               }
            }
         }
      }
   }

   private static List<String> parseUpstreamIdsFromPoolCreateResponse(JSONObject responseJson) {
      if (responseJson == null) {
         return Collections.emptyList();
      } else {
         Object raw = responseJson.get("data");
         if (raw == null) {
            return Collections.emptyList();
         } else if (raw instanceof CharSequence) {
            String s = StrUtil.trimToNull(raw.toString());
            return s == null ? Collections.emptyList() : List.of(s);
         } else if (raw instanceof JSONArray arr) {
            return idsFromJsonArray(arr);
         } else {
            JSONObject dataObj = raw instanceof JSONObject ? (JSONObject)raw : JSONUtil.parseObj(raw);
            if (dataObj != null && !dataObj.isEmpty()) {
               JSONArray serverIds = dataObj.getJSONArray("serverIds");
               if (serverIds != null && !serverIds.isEmpty()) {
                  return idsFromJsonArray(serverIds);
               }

               String one = firstNonBlank(dataObj.getStr("upstreamServerId"), dataObj.getStr("serverId"), dataObj.getStr("id"));
               if (StrUtil.isNotBlank(one)) {
                  return List.of(one.trim());
               }
            }

            return Collections.emptyList();
         }
      }
   }

   private static List<String> idsFromJsonArray(JSONArray arr) {
      List<String> out = new ArrayList<>();

      for (int i = 0; i < arr.size(); i++) {
         Object el = arr.get(i);
         if (el instanceof CharSequence) {
            String s = StrUtil.trimToNull(el.toString());
            if (s != null) {
               out.add(s);
            }
         } else if (el instanceof JSONObject jo) {
            String id = firstNonBlank(jo.getStr("upstreamServerId"), jo.getStr("serverId"), jo.getStr("id"));
            if (StrUtil.isNotBlank(id)) {
               out.add(id.trim());
            }
         }
      }

      return out;
   }
}
