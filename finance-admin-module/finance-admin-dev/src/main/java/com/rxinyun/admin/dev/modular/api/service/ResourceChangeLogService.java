package com.rxinyun.admin.dev.modular.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rxinyun.admin.dev.modular.api.entity.ResourceChangeLog;
import java.time.LocalDateTime;
import java.util.List;

public interface ResourceChangeLogService extends IService<ResourceChangeLog> {
   void logChange(String orderNo, Long memberNo, String changeType, String upstreamServerId, String poolSyncTarget);

   List<ResourceChangeLog> queryPendingLogs(Long memberNo, LocalDateTime lastSyncTime, Integer limit);

   List<ResourceChangeLog> queryPendingLogsForPool(LocalDateTime lastSyncTime, Integer limit);

   void markAsProcessed(String logId);
}
