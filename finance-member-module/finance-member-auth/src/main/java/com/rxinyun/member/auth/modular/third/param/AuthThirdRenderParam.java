package com.rxinyun.member.auth.modular.third.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;

public class AuthThirdRenderParam {
   @Schema(
      description = "第三方平台标识",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "platform不能为空"
   )
   private String platform;

   public String getPlatform() {
      return this.platform;
   }

   public void setPlatform(final String platform) {
      this.platform = platform;
   }
}
