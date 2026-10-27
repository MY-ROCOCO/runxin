package com.rxinyun.admin.dev.modular.log.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.dev.modular.log.param.IndependentLogPageParam;
import com.rxinyun.admin.dev.modular.log.result.ServerOperationLogResult;

public interface IndependentLogService {
   Page<ServerOperationLogResult> page(IndependentLogPageParam independentLogPageParam);
}
