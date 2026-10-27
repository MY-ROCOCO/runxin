package com.rxinyun.admin.system.modular.resource.provider;

import com.rxinyun.admin.system.modular.resource.service.SysMenuService;
import com.rxinyun.sys.api.SysMenuApi;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class SysMenuApiProvider implements SysMenuApi {
   @Resource
   private SysMenuService sysMenuService;

   public String addForGenMenu(String parentId, String busName, String module, String title, String path) {
      return this.sysMenuService.addForGenMenu(parentId, busName, title, module, path);
   }
}
