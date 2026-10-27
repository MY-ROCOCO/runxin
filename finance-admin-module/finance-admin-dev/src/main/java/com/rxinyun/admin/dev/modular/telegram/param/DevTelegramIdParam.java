package com.rxinyun.admin.dev.modular.telegram.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public class DevTelegramIdParam {
   @Schema(
      description = "id"
   )
   @NotBlank(
      message = "id不能为空"
   )
   private String id;

   public String getId() {
      return this.id;
   }

   public void setId(final String id) {
      this.id = id;
   }
}
