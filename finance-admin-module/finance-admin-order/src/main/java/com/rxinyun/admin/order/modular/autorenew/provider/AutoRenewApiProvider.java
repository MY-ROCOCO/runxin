package com.rxinyun.admin.order.modular.autorenew.provider;

import com.rxinyun.admin.order.modular.autorenew.service.AutoRenewService;
import com.rxinyun.order.api.AutoRenewApi;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class AutoRenewApiProvider implements AutoRenewApi {
   @Resource
   private AutoRenewService autoRenewService;

   public void execute() {
      this.autoRenewService.execute();
   }
}
