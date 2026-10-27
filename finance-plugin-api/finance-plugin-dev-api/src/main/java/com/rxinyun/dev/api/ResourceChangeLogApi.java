package com.rxinyun.dev.api;

import com.rxinyun.dev.core.pojo.BaseResourceChangeLog;
import java.util.List;

public interface ResourceChangeLogApi {
   List<BaseResourceChangeLog> listPendingByOrderNoAndUpstreamServerId(String orderNo, String upstreamServerId);

   boolean save(BaseResourceChangeLog changeLog);

   boolean updateById(BaseResourceChangeLog changeLog);
}
