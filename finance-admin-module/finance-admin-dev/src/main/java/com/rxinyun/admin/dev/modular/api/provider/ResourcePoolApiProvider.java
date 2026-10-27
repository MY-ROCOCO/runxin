package com.rxinyun.admin.dev.modular.api.provider;

import cn.hutool.core.bean.BeanUtil;
import com.rxinyun.admin.dev.modular.api.entity.ResourceChangeLog;
import com.rxinyun.admin.dev.modular.api.entity.ResourcePool;
import com.rxinyun.admin.dev.modular.api.entity.ResourcePoolRequest;
import com.rxinyun.admin.dev.modular.api.service.ResourceChangeLogService;
import com.rxinyun.admin.dev.modular.api.service.ResourcePoolRequestService;
import com.rxinyun.admin.dev.modular.api.service.ResourcePoolService;
import com.rxinyun.common.context.ApiContext;
import com.rxinyun.dev.api.ResourcePoolApi;
import com.rxinyun.dev.core.pojo.BaseResourcePool;
import com.rxinyun.dev.core.pojo.BaseResourcePoolRequest;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ResourcePoolApiProvider implements ResourcePoolApi {
   private static final Logger log = LoggerFactory.getLogger(ResourcePoolApiProvider.class);
   @Resource
   private ResourcePoolService resourcePoolService;
   @Resource
   private ResourceChangeLogService resourceChangeLogService;
   @Resource
   private ResourcePoolRequestService resourcePoolRequestService;

   public BaseResourcePool getByPoolType(String poolType) {
      ResourcePool entity = this.resourcePoolService.getByPoolType(poolType);
      return entity == null ? null : (BaseResourcePool)BeanUtil.toBean(entity, BaseResourcePool.class);
   }

   public BaseResourcePool getById(String resourcePoolId) {
      ResourcePool entity = this.resourcePoolService.getByIdForApi(resourcePoolId);
      return (BaseResourcePool)BeanUtil.toBean(entity, BaseResourcePool.class);
   }

   public List<Map<String, Object>> queryPendingChangeLogs(LocalDateTime lastSyncTime, Integer limit) {
      boolean poolAuth = ApiContext.isResourcePoolAuth();
      Long memberNo = ApiContext.getMemberNo();
      if (!poolAuth && memberNo == null) {
         log.error("API Key 无效：非资源池对接且未解析到会员编号");
         throw new RuntimeException("API Key 无效");
      } else {
         log.info("查询变更日志，资源池对接：{}，会员编号：{}，最后同步时间：{}，限制：{}", new Object[]{poolAuth, memberNo, lastSyncTime, limit});
         List<ResourceChangeLog> logs = poolAuth
            ? this.resourceChangeLogService.queryPendingLogsForPool(lastSyncTime, limit)
            : this.resourceChangeLogService.queryPendingLogs(memberNo, lastSyncTime, limit);
         return logs.stream().map(log -> {
            Map<String, Object> result = new HashMap<>();
            result.put("id", log.getId());
            result.put("orderNo", log.getOrderNo());
            result.put("memberNo", log.getMemberNo());
            result.put("changeType", log.getChangeType());
            result.put("createTime", log.getCreateTime());
            result.put("upstreamServerId", log.getUpstreamServerId());
            result.put("poolSyncTarget", log.getPoolSyncTarget());
            return result;
         }).collect(Collectors.toList());
      }
   }

   public void markChangeLogAsProcessed(String logId) {
      log.debug("标记变更日志为已处理，ID: {}", logId);
      this.resourceChangeLogService.markAsProcessed(logId);
   }

   public void logResourceChange(String orderNo, Long memberNo, String changeType, String upstreamServerId, String poolSyncTarget) {
      log.info(
         "记录资源变更日志，订单号：{}，会员编号：{}，变更类型：{}，upstreamServerId：{}，poolSyncTarget：{}", new Object[]{orderNo, memberNo, changeType, upstreamServerId, poolSyncTarget}
      );
      this.resourceChangeLogService.logChange(orderNo, memberNo, changeType, upstreamServerId, poolSyncTarget);
   }

   public void createRequest(BaseResourcePoolRequest request) {
      log.info("创建资源池发单请求，订单号：{}，资源池ID：{}，操作类型：{}", new Object[]{request.getOrderNo(), request.getResourcePoolId(), request.getOperationType()});
      ResourcePoolRequest entity = new ResourcePoolRequest();
      entity.setOrderNo(request.getOrderNo());
      entity.setMemberNo(request.getMemberNo());
      entity.setResourcePoolId(request.getResourcePoolId());
      entity.setRequestParams(request.getRequestParams());
      entity.setOperationType(request.getOperationType() != null ? request.getOperationType() : "CREATE");
      entity.setProductType(request.getProductType());
      entity.setAServerId(request.getAServerId());
      entity.setMonths(request.getMonths());
      this.resourcePoolRequestService.create(entity);
   }
}
