package com.rxinyun.admin.business.modular.task.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rxinyun.admin.business.modular.task.entity.TaskExecutionLog;
import com.rxinyun.admin.business.modular.task.param.TaskExecutionLogParam;

public interface TaskExecutionLogService extends IService<TaskExecutionLog> {
   Page<TaskExecutionLog> page(TaskExecutionLogParam pageParam);
}
