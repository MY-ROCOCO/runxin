package com.rxinyun.order.api;

import com.rxinyun.order.core.pojo.BaseOrderAsyncTask;
import java.util.List;

public interface OrderAsyncTaskApi {
   List<BaseOrderAsyncTask> queryPendingTaskList();
}
