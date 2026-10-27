package com.rxinyun.admin.production.modular.vhost.region.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public class VhostRegionIdParam {
   @Schema(
      description = "地域ID"
   )
   @NotBlank(
      message = "地域ID不能为空"
   )
   private String id;

   public String getId() {
      return this.id;
   }

   public void setId(final String id) {
      this.id = id;
   }
}
