package com.rxinyun.member.tools.modular.config.param;

import io.swagger.v3.oas.annotations.media.Schema;

public class MemberConfigListParam {
   @Schema(
      description = "配置分类"
   )
   private String category;

   public String getCategory() {
      return this.category;
   }

   public void setCategory(final String category) {
      this.category = category;
   }
}
