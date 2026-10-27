package com.rxinyun.member.tools.modular.log.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;

public class MemberIndependentLogPageParam extends MemberLogPageParam {
   @Schema(
      description = "独立服务器ID",
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
