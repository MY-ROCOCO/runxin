package com.rxinyun.admin.system.modular.resource.provider;

import com.rxinyun.admin.system.modular.resource.service.SysButtonService;
import com.rxinyun.sys.api.SysButtonApi;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class SysButtonApiProvider implements SysButtonApi {
   @Resource
   private SysButtonService sysButtonService;

   public void addForGenButton(String menuId, String className, String functionName) {
      this.sysButtonService.addForGenButton(menuId, className, functionName);
   }
}
