package com.rxinyun.member.auth.modular.login.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;

public class AuthAccountPasswordLoginParam {
   @Schema(
      description = "账号或邮箱",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "账号不能为空"
   )
   private String account;
   @Schema(
      description = "密码",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "密码不能为空"
   )
   private String password;
   @Schema(
      description = "设备类型： PC, APP, MINI"
   )
   private String device;
   @Schema(
      description = "验证码"
   )
   private String validCode;
   @Schema(
      description = "验证码请求号"
   )
   private String validCodeReqNo;

   public String getAccount() {
      return this.account;
   }

   public String getPassword() {
      return this.password;
   }

   public String getDevice() {
      return this.device;
   }

   public String getValidCode() {
      return this.validCode;
   }

   public String getValidCodeReqNo() {
      return this.validCodeReqNo;
   }

   public void setAccount(final String account) {
      this.account = account;
   }

   public void setPassword(final String password) {
      this.password = password;
   }

   public void setDevice(final String device) {
      this.device = device;
   }

   public void setValidCode(final String validCode) {
      this.validCode = validCode;
   }

   public void setValidCodeReqNo(final String validCodeReqNo) {
      this.validCodeReqNo = validCodeReqNo;
   }
}
