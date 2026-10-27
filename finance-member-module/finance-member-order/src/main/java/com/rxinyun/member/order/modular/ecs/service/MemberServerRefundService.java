package com.rxinyun.member.order.modular.ecs.service;

import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.member.order.modular.ecs.param.MemberServerRefundParam;
import com.rxinyun.member.order.modular.ecs.response.MemberServerRefundPreviewResponse;

public interface MemberServerRefundService {
   CommonResult<MemberServerRefundPreviewResponse> preview(MemberServerRefundParam param);

   CommonResult<String> execute(MemberServerRefundParam param);
}
