package com.rxinyun.admin.order.modular.ecs.service;

import com.rxinyun.admin.order.modular.ecs.param.ServerRefundParam;
import com.rxinyun.admin.order.modular.ecs.response.ServerRefundPreviewResponse;
import com.rxinyun.common.pojo.CommonResult;

public interface ServerRefundService {
   CommonResult<ServerRefundPreviewResponse> preview(ServerRefundParam param);

   CommonResult<String> execute(ServerRefundParam param);
}
