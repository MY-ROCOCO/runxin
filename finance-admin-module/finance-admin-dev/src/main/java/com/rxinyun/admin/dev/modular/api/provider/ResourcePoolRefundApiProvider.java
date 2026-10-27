package com.rxinyun.admin.dev.modular.api.provider;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.rxinyun.admin.dev.modular.api.entity.ResourcePool;
import com.rxinyun.admin.dev.modular.api.service.ResourcePoolService;
import com.rxinyun.common.enums.order.ProductTypeEnum;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.common.util.CommonCryptogramUtil;
import com.rxinyun.dev.api.ResourcePoolRefundApi;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ResourcePoolRefundApiProvider implements ResourcePoolRefundApi {
   private static final Logger log = LoggerFactory.getLogger(ResourcePoolRefundApiProvider.class);
   @Resource
   private ResourcePoolService resourcePoolService;

   public CommonResult<String> refundExecute(String resourcePoolId, int productType, String requestBodyJson) {
      ResourcePool pool = this.resourcePoolService.getByIdForApi(resourcePoolId);
      if (pool == null) {
         return CommonResult.error("资源池不存在或已禁用");
      } else {
         String apiUrl = pool.getApiUrl();
         if (!apiUrl.endsWith("/")) {
            apiUrl = apiUrl + "/";
         }

         boolean independent = ProductTypeEnum.INDEPENDENT_SERVER.getCode().equals(productType);
         String path = independent ? "api/order/independent/refund/execute" : "api/order/refund/execute";
         String url = apiUrl + path;
         String apiSecret = CommonCryptogramUtil.doSm4CbcDecrypt(pool.getApiSecret());
         HttpResponse response = ((HttpRequest)((HttpRequest)((HttpRequest)HttpRequest.post(url).header("Content-Type", "application/json"))
                  .header("X-API-Key", pool.getApiKey()))
               .header("X-API-Secret", apiSecret))
            .body(requestBodyJson)
            .timeout(30000)
            .execute();
         String responseBody = response.body();
         if (!response.isOk()) {
            log.error("资源池退款执行 HTTP 失败 url={} status={} body={}", new Object[]{url, response.getStatus(), responseBody});
            return CommonResult.error("资源池请求失败 HTTP " + response.getStatus());
         } else {
            return CommonResult.data(responseBody);
         }
      }
   }
}
