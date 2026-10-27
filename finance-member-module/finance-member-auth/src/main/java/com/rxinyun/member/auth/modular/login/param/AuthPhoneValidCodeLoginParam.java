package com.rxinyun.member.auth.modular.login.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import me.zhyd.oauth.model.AuthUser;

public class AuthPhoneValidCodeLoginParam {
   @Schema(
      description = "手机号",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "手机号不能为空"
   )
   private String phone;
   @Schema(
      description = "验证码",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "验证码不能为空"
   )
   private String validCode;
   @Schema(
      description = "验证码请求号",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "验证码请求号不能为空"
   )
   private String validCodeReqNo;
   @Schema(
      description = "设备"
   )
   private String device;
   @Schema(
      description = "第三方信息"
   )
   private AuthUser authUser;

   public String getPhone() {
      return this.phone;
   }

   public String getValidCode() {
      return this.validCode;
   }

   public String getValidCodeReqNo() {
      return this.validCodeReqNo;
   }

   public String getDevice() {
      return this.device;
   }

   public AuthUser getAuthUser() {
      return this.authUser;
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

   public void setDevice(final String device) {
      this.device = device;
   }

   public void setAuthUser(final AuthUser authUser) {
      this.authUser = authUser;
   }
}
