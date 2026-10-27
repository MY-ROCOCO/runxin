package com.rxinyun.admin.auth.modular.third.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;

public class AuthThirdCallbackParam {
   @Schema(
      description = "第三方平台标识",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "platform不能为空"
   )
   private String platform;
   @Schema(
      description = "第三方回调code",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "code不能为空"
   )
   private String code;
   @Schema(
      description = "第三方回调state",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "state不能为空"
   )
   private String state;

   public String getPlatform() {
      return this.platform;
   }

   public String getCode() {
      return this.code;
   }

   public String getState() {
      return this.state;
   }

   public void setPlatform(final String platform) {
      this.platform = platform;
   }

   public void setCode(final String code) {
      this.code = code;
   }

   public void setState(final String state) {
      this.state = state;
   }
}
