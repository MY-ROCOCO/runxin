package com.rxinyun.admin.system.modular.user.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;

public class SysUserFindPwdByEmailParam {
   @Schema(
      description = "邮箱",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "email不能为空"
   )
   private String email;
   @Schema(
      description = "验证码",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "validCode不能为空"
   )
   private String validCode;
   @Schema(
      description = "验证码请求号",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "validCodeReqNo不能为空"
   )
   private String validCodeReqNo;
   @Schema(
      description = "新密码",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "newPassword不能为空"
   )
   private String newPassword;

   public String getEmail() {
      return this.email;
   }

   public String getValidCode() {
      return this.validCode;
   }

   public String getValidCodeReqNo() {
      return this.validCodeReqNo;
   }

   public String getNewPassword() {
      return this.newPassword;
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

   public void setNewPassword(final String newPassword) {
      this.newPassword = newPassword;
   }
}
