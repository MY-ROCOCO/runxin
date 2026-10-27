package com.rxinyun.admin.system.modular.resource.param.menu;

import io.swagger.v3.oas.annotations.media.Schema;

public class SysMenuSelectorMenuParam {
   @Schema(
      description = "模块"
   )
   private String module;

   public String getModule() {
      return this.module;
   }

   public void setModule(final String module) {
      this.module = module;
   }
}
