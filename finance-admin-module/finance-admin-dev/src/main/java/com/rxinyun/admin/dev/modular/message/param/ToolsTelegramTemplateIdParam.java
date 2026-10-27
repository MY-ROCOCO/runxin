package com.rxinyun.admin.dev.modular.message.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public class ToolsTelegramTemplateIdParam {
   @Schema(
      description = "主键ID"
   )
   @NotBlank(
      message = "ID不能为空"
   )
   private String id;

   public String getId() {
      return this.id;
   }

   public void setId(final String id) {
      this.id = id;
   }
}
