package com.rxinyun.admin.dev.modular.api.provider;

import com.rxinyun.admin.dev.modular.api.entity.ResourcePoolRequest;
import com.rxinyun.admin.dev.modular.api.handler.ResourcePoolRequestHandler;
import com.rxinyun.admin.dev.modular.api.service.ResourcePoolRequestService;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.dev.api.ResourcePoolRequestHandlerApi;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class ResourcePoolRequestHandlerApiProvider implements ResourcePoolRequestHandlerApi {
   @Resource
   private ResourcePoolRequestHandler resourcePoolRequestHandler;
   @Resource
   private ResourcePoolRequestService resourcePoolRequestService;

   public void sendPendingRequestByOrderNo(String orderNo) {
      ResourcePoolRequest request = this.resourcePoolRequestService.getByOrderNo(orderNo);
      if (request == null) {
         throw new CommonException("资源池请求不存在", new Object[0]);
      } else {
         this.resourcePoolRequestHandler.sendRequest(request);
      }
   }
}
