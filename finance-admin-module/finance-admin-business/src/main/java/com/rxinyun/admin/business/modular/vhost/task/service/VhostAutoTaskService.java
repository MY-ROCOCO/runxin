package com.rxinyun.admin.business.modular.vhost.task.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.business.modular.vhost.task.entity.VhostAutoTask;
import com.rxinyun.admin.business.modular.vhost.task.param.VhostAutoTaskIdParam;
import com.rxinyun.admin.business.modular.vhost.task.param.VhostAutoTaskPageParam;
import com.rxinyun.common.pojo.CommonResult;
import java.util.List;

public interface VhostAutoTaskService {
   Page<VhostAutoTask> page(VhostAutoTaskPageParam pageParam);

   CommonResult<String> retry(VhostAutoTaskIdParam idParam);

   void recoverStuckTasks();

   List<VhostAutoTask> listPending(int limit);

   boolean claim(String id);

   void bindMaster(String id, String masterId);

   void markSuccess(String id, String masterId, String executeResult);

   void markFailure(VhostAutoTask task, String message, boolean retryable);
}
