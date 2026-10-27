package com.rxinyun.dev.api;

import com.rxinyun.dev.core.pojo.BaseResourcePool;
import com.rxinyun.dev.core.pojo.BaseResourcePoolRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface ResourcePoolApi {
   BaseResourcePool getByPoolType(String poolType);

   BaseResourcePool getById(String resourcePoolId);

   List<Map<String, Object>> queryPendingChangeLogs(LocalDateTime lastSyncTime, Integer limit);

   void markChangeLogAsProcessed(String logId);

   void logResourceChange(String orderNo, Long memberNo, String changeType, String upstreamServerId, String poolSyncTarget);

   void createRequest(BaseResourcePoolRequest request);
}
