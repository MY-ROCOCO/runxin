package com.rxinyun.admin.business.modular.vhost.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;

public class AdminVhostIdParam {
   @NotBlank(
      message = "虚拟主机不能为空"
   )
   @Schema(
      description = "虚拟主机实例ID",
      requiredMode = RequiredMode.REQUIRED
   )
   private String id;

   public String getId() {
      return this.id;
   }

   public void setId(final String id) {
      this.id = id;
   }
}
