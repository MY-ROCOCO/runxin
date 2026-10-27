package com.rxinyun.admin.production.modular.vhost.master.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public class VhostMasterIdParam {
   @Schema(
      description = "主控ID"
   )
   @NotBlank(
      message = "主控ID不能为空"
   )
   private String id;

   public String getId() {
      return this.id;
   }

   public void setId(final String id) {
      this.id = id;
   }
}
