package com.rxinyun.admin.system.modular.resource.param.menu;

import io.swagger.v3.oas.annotations.media.Schema;

public class SysMenuTreeParam {
   @Schema(
      description = "模块"
   )
   private String module;
   @Schema(
      description = "名称关键词"
   )
   private String searchKey;

   public String getModule() {
      return this.module;
   }

   public String getSearchKey() {
      return this.searchKey;
   }

   public void setModule(final String module) {
      this.module = module;
   }

   public void setSearchKey(final String searchKey) {
      this.searchKey = searchKey;
   }
}
