package com.rxinyun.admin.production.modular.vhost.version.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public class VhostVersionIdParam {
   @Schema(
      description = "版本ID"
   )
   @NotBlank(
      message = "版本ID不能为空"
   )
   private String id;

   public String getId() {
      return this.id;
   }

   public void setId(final String id) {
      this.id = id;
   }
}
