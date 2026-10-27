package com.rxinyun.member.auth.modular.login.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;

public class AuthGetEmailValidCodeParam {
   @Schema(
      description = "邮箱",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "邮箱不能为空"
   )
   private String email;
   @Schema(
      description = "图形验证码",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "图形验证码不能为空"
   )
   private String validCode;
   @Schema(
      description = "图形验证码请求号",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "图形验证码请求号不能为空"
   )
   private String validCodeReqNo;

   public String getEmail() {
      return this.email;
   }

   public String getValidCode() {
      return this.validCode;
   }

   public String getValidCodeReqNo() {
      return this.validCodeReqNo;
   }

   public void setEmail(final String email) {
      this.email = email;
   }

   public void setValidCode(final String validCode) {
      this.validCode = validCode;
   }

   public void setValidCodeReqNo(final String validCodeReqNo) {
      this.validCodeReqNo = validCodeReqNo;
   }
}
