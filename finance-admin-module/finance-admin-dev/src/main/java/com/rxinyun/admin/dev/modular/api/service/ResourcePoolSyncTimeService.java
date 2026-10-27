package com.rxinyun.admin.dev.modular.api.service;

import java.time.LocalDateTime;

public interface ResourcePoolSyncTimeService {
   LocalDateTime getLastSyncTime(String resourcePoolId);

   void updateLastSyncTime(String resourcePoolId, LocalDateTime syncTime);
}
