package com.rxinyun.member.auth.modular.login.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import me.zhyd.oauth.model.AuthUser;

public class AuthRegisterParam {
   @Schema(
      description = "账号",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "account不能为空"
   )
   private String account;
   @Schema(
      description = "邮箱"
   )
   private String email;
   @Schema(
      description = "手机号",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "手机号不能为空"
   )
   private String phone;
   @Schema(
      description = "短信验证码",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "短信验证码不能为空"
   )
   private String validCode;
   @Schema(
      description = "短信验证码请求号",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "短信验证码请求号不能为空"
   )
   private String validCodeReqNo;
   @Schema(
      description = "密码",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "密码不能为空"
   )
   private String password;
   @Schema(
      description = "密码确认",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "密码确认不能为空"
   )
   private String passwordConfirm;
   @Schema(
      description = "第三方信息"
   )
   private AuthUser authUser;
   @Schema(
      description = "代理编码"
   )
   private String agentCode;

   public String getAccount() {
      return this.account;
   }

   public String getEmail() {
      return this.email;
   }

   public String getPhone() {
      return this.phone;
   }

   public String getValidCode() {
      return this.validCode;
   }

   public String getValidCodeReqNo() {
      return this.validCodeReqNo;
   }

   public String getPassword() {
      return this.password;
   }

   public String getPasswordConfirm() {
      return this.passwordConfirm;
   }

   public AuthUser getAuthUser() {
      return this.authUser;
   }

   public String getAgentCode() {
      return this.agentCode;
   }

   public void setAccount(final String account) {
      this.account = account;
   }

   public void setEmail(final String email) {
      this.email = email;
   }

   public void setPhone(final String phone) {
      this.phone = phone;
   }

   public void setValidCode(final String validCode) {
      this.validCode = validCode;
   }

   public void setValidCodeReqNo(final String validCodeReqNo) {
      this.validCodeReqNo = validCodeReqNo;
   }

   public void setPassword(final String password) {
      this.password = password;
   }

   public void setPasswordConfirm(final String passwordConfirm) {
      this.passwordConfirm = passwordConfirm;
   }

   public void setAuthUser(final AuthUser authUser) {
      this.authUser = authUser;
   }

   public void setAgentCode(final String agentCode) {
      this.agentCode = agentCode;
   }
}
