package com.rxinyun.admin.dev.modular.log.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;

public class ServerLogPageParam extends LogPageParam {
   @Schema(
      description = "云服务器ID",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "serverId不能为空"
   )
   private String serverId;

   public String getServerId() {
      return this.serverId;
   }

   public void setServerId(final String serverId) {
      this.serverId = serverId;
   }
}
