package com.rxinyun.dev.api;

import com.rxinyun.common.pojo.CommonResult;

public interface ResourcePoolRefundApi {
   CommonResult<String> refundExecute(String resourcePoolId, int productType, String requestBodyJson);
}
