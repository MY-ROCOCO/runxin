package com.rxinyun.admin.business.modular.task.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.admin.business.modular.task.entity.TaskExecutionLog;
import com.rxinyun.admin.business.modular.task.mapper.TaskExecutionLogMapper;
import com.rxinyun.admin.business.modular.task.param.TaskExecutionLogParam;
import com.rxinyun.admin.business.modular.task.service.TaskExecutionLogService;
import com.rxinyun.common.page.CommonPageRequest;
import org.springframework.stereotype.Service;

@Service
public class TaskExecutionLogServiceImpl extends ServiceImpl<TaskExecutionLogMapper, TaskExecutionLog> implements TaskExecutionLogService {
   @Override
   public Page<TaskExecutionLog> page(TaskExecutionLogParam pageParam) {
      QueryWrapper<TaskExecutionLog> queryWrapper = new QueryWrapper().checkSqlInjection();
      if (pageParam.getSortField() == null || pageParam.getSortOrder() == null) {
         queryWrapper.lambda().orderByDesc(TaskExecutionLog::getCreateTime);
      } else if ("ASCEND".equalsIgnoreCase(pageParam.getSortOrder())) {
         queryWrapper.orderByAsc(pageParam.getSortField());
      } else {
         queryWrapper.orderByDesc(pageParam.getSortField());
      }

      return (Page<TaskExecutionLog>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
   }
}
