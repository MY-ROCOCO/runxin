package com.rxinyun.admin.dev.modular.message.param;

import io.swagger.v3.oas.annotations.media.Schema;

public class ToolsTelegramTemplatePageParam {
   @Schema(
      description = "模板名称关键词"
   )
   private String searchKey;

   public String getSearchKey() {
      return this.searchKey;
   }

   public void setSearchKey(final String searchKey) {
      this.searchKey = searchKey;
   }
}
