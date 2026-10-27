package com.rxinyun.admin.dev.modular.log.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rxinyun.admin.dev.modular.log.entity.SysLog;
import com.rxinyun.admin.dev.modular.log.param.LogDeleteParam;
import com.rxinyun.admin.dev.modular.log.param.LogIdParam;
import com.rxinyun.admin.dev.modular.log.param.LogPageParam;
import com.rxinyun.admin.dev.modular.log.result.DevLogOpBarChartDataResult;
import com.rxinyun.admin.dev.modular.log.result.DevLogOpPieChartDataResult;
import com.rxinyun.admin.dev.modular.log.result.DevLogVisLineChartDataResult;
import com.rxinyun.admin.dev.modular.log.result.DevLogVisPieChartDataResult;
import java.util.List;

public interface SysLogService extends IService<SysLog> {
   Page<SysLog> page(LogPageParam devLogPageParam);

   SysLog detail(LogIdParam devLogIdParam);

   void delete(LogDeleteParam devLogDeleteParam);

   List<DevLogVisLineChartDataResult> visLogLineChartData();

   List<DevLogVisPieChartDataResult> visLogPieChartData();

   List<DevLogOpBarChartDataResult> opLogBarChartData();

   List<DevLogOpPieChartDataResult> opLogPieChartData();
}
