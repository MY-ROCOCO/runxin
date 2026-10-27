package com.rxinyun.admin.dev.modular.job.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rxinyun.admin.dev.modular.job.entity.DevJob;
import com.rxinyun.admin.dev.modular.job.param.DevJobAddParam;
import com.rxinyun.admin.dev.modular.job.param.DevJobEditParam;
import com.rxinyun.admin.dev.modular.job.param.DevJobIdParam;
import com.rxinyun.admin.dev.modular.job.param.DevJobListParam;
import com.rxinyun.admin.dev.modular.job.param.DevJobPageParam;
import com.rxinyun.admin.dev.modular.job.result.DevJobActionClassResult;
import java.util.List;

public interface DevJobService extends IService<DevJob> {
   Page<DevJob> page(DevJobPageParam devJobPageParam);

   List<DevJob> list(DevJobListParam devJobListParam);

   void add(DevJobAddParam devJobAddParam);

   void edit(DevJobEditParam devJobEditParam);

   void delete(List<DevJobIdParam> devJobIdParamList);

   DevJob detail(DevJobIdParam devJobIdParam);

   DevJob queryEntity(String id);

   void stopJob(DevJobIdParam devJobIdParam);

   void runJob(DevJobIdParam devJobIdParam);

   void runJobNow(DevJobIdParam devJobIdParam);

   List<DevJobActionClassResult> getActionClass();
}
