package com.rxinyun.member.auth.modular.monitor.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;

public class AuthExitSessionParam {
   @Schema(
      description = "用户id",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "userId不能为空"
   )
   private String userId;

   public String getUserId() {
      return this.userId;
   }

   public void setUserId(final String userId) {
      this.userId = userId;
   }
}
