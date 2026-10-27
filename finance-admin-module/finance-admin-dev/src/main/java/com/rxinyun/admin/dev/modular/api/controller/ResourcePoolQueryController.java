package com.rxinyun.admin.dev.modular.api.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.rxinyun.admin.business.modular.esc.entity.Server;
import com.rxinyun.admin.business.modular.esc.entity.ServerIp;
import com.rxinyun.admin.business.modular.esc.entity.ServerPort;
import com.rxinyun.admin.business.modular.esc.service.ServerIpService;
import com.rxinyun.admin.business.modular.esc.service.ServerPortService;
import com.rxinyun.admin.business.modular.esc.util.ResourcePoolSnapshotWriter;
import com.rxinyun.admin.dev.modular.api.entity.ResourceChangeLog;
import com.rxinyun.admin.dev.modular.api.service.ResourceChangeLogService;
import com.rxinyun.business.api.IndependentApi;
import com.rxinyun.business.api.ServerApi;
import com.rxinyun.business.core.pojo.BaseIndependent;
import com.rxinyun.business.core.pojo.BaseServer;
import com.rxinyun.business.core.util.IndependentResourcePoolSnapshotUtil;
import com.rxinyun.common.context.ApiContext;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.common.util.TimeUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "资源池查询控制器"
)
@RestController
@RequestMapping({"/api/resource-pool"})
public class ResourcePoolQueryController {
   private static final Logger log = LoggerFactory.getLogger(ResourcePoolQueryController.class);
   @Resource
   private ResourceChangeLogService resourceChangeLogService;
   @Resource
   private ServerApi serverApi;
   @Resource
   private IndependentApi independentApi;
   @Resource
   private ServerIpService serverIpService;
   @Resource
   private ServerPortService serverPortService;

   @PostMapping({"/queryPendingChangeLogs"})
   @Operation(
      summary = "查询待处理的变更日志（供下游系统同步）"
   )
   public CommonResult<List<Map<String, Object>>> queryPendingChangeLogs(@RequestBody JSONObject params) {
      try {
         log.info("收到查询变更日志请求，参数：{}", params);
         boolean poolAuth = ApiContext.isResourcePoolAuth();
         Long memberNo = ApiContext.getMemberNo();
         if (!poolAuth && memberNo == null) {
            log.error("API Key 无效：非资源池对接且未解析到会员编号");
            return CommonResult.error(401, "API Key 无效");
         } else {
            LocalDateTime lastSyncTime = null;
            if (params != null && params.containsKey("lastSyncTime")) {
               String lastSyncTimeStr = params.getString("lastSyncTime");
               if (ObjectUtil.isNotEmpty(lastSyncTimeStr)) {
                  try {
                     lastSyncTime = LocalDateTime.parse(lastSyncTimeStr);
                  } catch (Exception var8) {
                     log.warn("解析 lastSyncTime 失败，使用默认值（1 小时前）: {}", lastSyncTimeStr);
                     lastSyncTime = TimeUtil.now().minusHours(1L);
                  }
               }
            }

            Integer limit = params.getInteger("limit");
            if (limit == null || limit <= 0) {
               limit = 100;
            }

            log.info("查询变更日志，资源池对接：{}，会员编号：{}，最后同步时间：{}，限制：{}", new Object[]{poolAuth, memberNo, lastSyncTime, limit});
            List<ResourceChangeLog> logs = poolAuth
               ? this.resourceChangeLogService.queryPendingLogsForPool(lastSyncTime, limit)
               : this.resourceChangeLogService.queryPendingLogs(memberNo, lastSyncTime, limit);
            List<Map<String, Object>> result = logs.stream().map(log -> {
               Map<String, Object> resultMap = new HashMap<>();
               resultMap.put("id", log.getId());
               resultMap.put("orderNo", log.getOrderNo());
               resultMap.put("memberNo", log.getMemberNo());
               resultMap.put("changeType", log.getChangeType());
               resultMap.put("createTime", log.getCreateTime());
               resultMap.put("upstreamServerId", log.getUpstreamServerId());
               resultMap.put("poolSyncTarget", log.getPoolSyncTarget());
               return resultMap;
            }).collect(Collectors.toList());
            log.info("查询变更日志成功，资源池对接：{}，会员编号：{}，找到 {} 条记录", new Object[]{poolAuth, memberNo, result.size()});
            return CommonResult.data(result);
         }
      } catch (Exception var9) {
         log.error("查询变更日志失败：{}", var9.getMessage(), var9);
         return CommonResult.error("查询变更日志失败：" + var9.getMessage());
      }
   }

   @PostMapping({"/queryServerDetail"})
   @Operation(
      summary = "查询当前服务器详情（供下游系统按实例同步）"
   )
   public CommonResult<JSONObject> queryServerDetail(@RequestBody JSONObject params) {
      try {
         log.info("收到查询服务器详情请求，参数：{}", params);
         boolean poolAuth = ApiContext.isResourcePoolAuth();
         Long memberNo = ApiContext.getMemberNo();
         if (!poolAuth && memberNo == null) {
            log.error("API Key 无效：非资源池对接且未解析到会员编号");
            return CommonResult.error(401, "API Key 无效");
         } else {
            String upstreamServerId = StrUtil.trimToNull(params.getString("upstreamServerId"));
            if (StrUtil.isBlank(upstreamServerId)) {
               return CommonResult.error("upstreamServerId 不能为空");
            } else {
               String targetHint = StrUtil.trimToEmpty(params.getString("poolSyncTarget"));
               JSONObject detail = this.queryCurrentServerDetail(upstreamServerId, targetHint);
               if (detail != null && !detail.isEmpty()) {
                  return CommonResult.data(detail);
               } else {
                  log.warn("未找到服务器详情，upstreamServerId={}，poolSyncTarget={}", upstreamServerId, targetHint);
                  return CommonResult.error("未找到服务器详情");
               }
            }
         }
      } catch (Exception var7) {
         log.error("查询服务器详情失败：{}", var7.getMessage(), var7);
         return CommonResult.error("查询服务器详情失败：" + var7.getMessage());
      }
   }

   @PostMapping({"/markChangeLogAsProcessed"})
   @Operation(
      summary = "标记变更日志为已处理（供下游系统调用）"
   )
   public CommonResult<String> markChangeLogAsProcessed(@RequestBody JSONObject params) {
      try {
         log.info("收到标记变更日志为已处理请求，参数：{}", params);
         boolean poolAuth = ApiContext.isResourcePoolAuth();
         Long memberNo = ApiContext.getMemberNo();
         if (!poolAuth && memberNo == null) {
            log.error("API Key 无效：非资源池对接且未解析到会员编号");
            return CommonResult.error(401, "API Key 无效");
         } else {
            String logId = params.getString("logId");
            if (ObjectUtil.isEmpty(logId)) {
               return CommonResult.error("变更日志 ID 不能为空");
            } else {
               this.resourceChangeLogService.markAsProcessed(logId);
               log.info("成功标记变更日志为已处理，会员编号：{}, logId: {}", memberNo, logId);
               return CommonResult.ok("操作成功");
            }
         }
      } catch (Exception var5) {
         log.error("标记变更日志失败：{}", var5.getMessage(), var5);
         return CommonResult.error("标记变更日志失败：" + var5.getMessage());
      }
   }

   private JSONObject queryCurrentServerDetail(String upstreamServerId, String targetHint) {
      if (StrUtil.equalsIgnoreCase(targetHint, "CLOUD") || StrUtil.equalsIgnoreCase(targetHint, "ECS")) {
         return this.queryCloudServerDetail(upstreamServerId);
      } else if (!StrUtil.equalsIgnoreCase(targetHint, "INDEPENDENT") && !StrUtil.equalsIgnoreCase(targetHint, "DEDICATED")) {
         JSONObject cloud = this.queryCloudServerDetail(upstreamServerId);
         return cloud != null && !cloud.isEmpty() ? cloud : this.queryIndependentServerDetail(upstreamServerId);
      } else {
         return this.queryIndependentServerDetail(upstreamServerId);
      }
   }

   private JSONObject queryCloudServerDetail(String upstreamServerId) {
      BaseServer baseServer = this.serverApi.selectByServerId(upstreamServerId);
      if (baseServer != null && !StrUtil.isBlank(baseServer.getId())) {
         Server server = new Server();
         BeanUtils.copyProperties(baseServer, server);
         List<ServerIp> ipList = this.serverIpService.queryByServerId(baseServer.getId());
         List<ServerPort> portList = Integer.valueOf(1).equals(baseServer.getShareIp()) ? this.serverPortService.queryByServerId(baseServer.getId()) : null;
         JSONObject snapshot = ResourcePoolSnapshotWriter.build(server, ipList, portList);
         snapshot.put("poolSyncTarget", "CLOUD");
         return snapshot;
      } else {
         return null;
      }
   }

   private JSONObject queryIndependentServerDetail(String upstreamServerId) {
      BaseIndependent independent = this.independentApi.getById(upstreamServerId);
      if (independent != null && !StrUtil.isBlank(independent.getId())) {
         JSONObject snapshot = IndependentResourcePoolSnapshotUtil.build(independent, this.independentApi.listIpsByIndependentServerId(independent.getId()));
         snapshot.put("poolSyncTarget", "INDEPENDENT");
         return snapshot;
      } else {
         return null;
      }
   }
}
