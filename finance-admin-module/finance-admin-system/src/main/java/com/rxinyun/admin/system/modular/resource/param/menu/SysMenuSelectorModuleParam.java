package com.rxinyun.admin.system.modular.resource.param.menu;

import io.swagger.v3.oas.annotations.media.Schema;

public class SysMenuSelectorModuleParam {
   @Schema(
      description = "名称关键词"
   )
   private String searchKey;

   public String getSearchKey() {
      return this.searchKey;
   }

   public void setSearchKey(final String searchKey) {
      this.searchKey = searchKey;
   }
}
