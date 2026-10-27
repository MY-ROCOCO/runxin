package com.rxinyun.admin.order.modular.ecs.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.order.modular.ecs.entity.ServerRefundRecord;
import com.rxinyun.admin.order.modular.ecs.param.ServerRefundRecordIdParam;
import com.rxinyun.admin.order.modular.ecs.param.ServerRefundRecordPageParam;
import com.rxinyun.common.pojo.CommonResult;

public interface ServerRefundRecordService {
   Page<ServerRefundRecord> page(ServerRefundRecordPageParam pageParam);

   CommonResult<String> approve(ServerRefundRecordIdParam param);
}
