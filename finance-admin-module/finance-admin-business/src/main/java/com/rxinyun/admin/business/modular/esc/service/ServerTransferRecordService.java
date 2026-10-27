package com.rxinyun.admin.business.modular.esc.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.business.modular.esc.entity.ServerTransferRecord;
import com.rxinyun.admin.business.modular.esc.param.ServerTransferRecordAddParam;
import com.rxinyun.admin.business.modular.esc.param.ServerTransferRecordIdParam;
import com.rxinyun.admin.business.modular.esc.param.ServerTransferRecordPageParam;
import com.rxinyun.common.pojo.CommonResult;

public interface ServerTransferRecordService {
   CommonResult add(ServerTransferRecordAddParam serverTransferRecordAddParam);

   CommonResult updateStatus(ServerTransferRecordIdParam serverTransferRecordIdParam);

   Page<ServerTransferRecord> page(ServerTransferRecordPageParam pageParam);
}
