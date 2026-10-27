package com.rxinyun.admin.dev.modular.dict.param;

import io.swagger.v3.oas.annotations.media.Schema;

public class DevDictTreeParam {
   @Schema(
      description = "字典分类"
   )
   private String category;

   public String getCategory() {
      return this.category;
   }

   public void setCategory(final String category) {
      this.category = category;
   }
}
