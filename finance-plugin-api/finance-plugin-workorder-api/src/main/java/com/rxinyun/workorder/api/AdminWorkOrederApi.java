package com.rxinyun.workorder.api;

import com.rxinyun.workorder.pojo.WorkOrderStatisticsResponse;
import java.time.LocalDateTime;
import java.util.HashMap;

public interface AdminWorkOrederApi {
   WorkOrderStatisticsResponse getInstance(HashMap<String, LocalDateTime> map);

   Long getUserOrderCount(Long userId);

   Long getUserUnprocessedOrderCount(Long userId);
}
