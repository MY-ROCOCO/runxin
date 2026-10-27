package com.rxinyun.admin.auth.modular.monitor.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;

public class AuthExitTokenParam {
   @Schema(
      description = "token值",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "tokenValue不能为空"
   )
   private String tokenValue;

   public String getTokenValue() {
      return this.tokenValue;
   }

   public void setTokenValue(final String tokenValue) {
      this.tokenValue = tokenValue;
   }
}
