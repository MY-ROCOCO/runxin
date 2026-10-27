package com.rxinyun.admin.dev.modular.dict.param;

import io.swagger.v3.oas.annotations.media.Schema;

public class DevDictListParam {
   @Schema(
      description = "父id"
   )
   private String parentId;
   @Schema(
      description = "字典分类"
   )
   private String category;

   public String getParentId() {
      return this.parentId;
   }

   public String getCategory() {
      return this.category;
   }

   public void setParentId(final String parentId) {
      this.parentId = parentId;
   }

   public void setCategory(final String category) {
      this.category = category;
   }
}
