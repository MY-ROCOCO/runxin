package com.rxinyun.admin.dev.modular.log.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.dev.modular.log.param.ServerLogPageParam;
import com.rxinyun.admin.dev.modular.log.result.ServerOperationLogResult;

public interface ServerLogService {
   Page<ServerOperationLogResult> page(ServerLogPageParam serverLogPageParam);
}
