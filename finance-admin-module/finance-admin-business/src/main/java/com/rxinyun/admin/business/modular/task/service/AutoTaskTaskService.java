package com.rxinyun.admin.business.modular.task.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rxinyun.admin.business.modular.task.entity.AutoTask;
import com.rxinyun.admin.business.modular.task.param.AutoTaskAddParam;
import com.rxinyun.admin.business.modular.task.param.AutoTaskIdParam;
import com.rxinyun.admin.business.modular.task.param.AutoTaskPageParam;
import com.rxinyun.common.pojo.CommonResult;
import java.util.List;

public interface AutoTaskTaskService extends IService<AutoTask> {
   Page<AutoTask> page(AutoTaskPageParam pageParam);

   List<AutoTask> queryPendingTaskList();

   void recoverStuckTasks();

   void add(AutoTaskAddParam autoTaskAddParam);

   void addTask(String taskType, String serverId, String params);

   CommonResult<String> retryFailedTask(AutoTaskIdParam autoTaskIdParam);
}
