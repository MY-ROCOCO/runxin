package com.rxinyun.admin.dev.modular.log.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;

public class LogDeleteParam {
   @Schema(
      description = "日志分类",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "category不能为空"
   )
   private String category;

   public String getCategory() {
      return this.category;
   }

   public void setCategory(final String category) {
      this.category = category;
   }
}
