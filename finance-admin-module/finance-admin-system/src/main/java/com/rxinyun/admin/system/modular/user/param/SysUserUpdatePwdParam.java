package com.rxinyun.admin.system.modular.user.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;

public class SysUserUpdatePwdParam {
   @Schema(
      description = "用户ID"
   )
   private String id;
   @Schema(
      description = "旧密码",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "password不能为空"
   )
   private String oldPassword;
   @Schema(
      description = "新密码",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "newPassword不能为空"
   )
   private String newPassword;

   public String getId() {
      return this.id;
   }

   public String getOldPassword() {
      return this.oldPassword;
   }

   public String getNewPassword() {
      return this.newPassword;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setOldPassword(final String oldPassword) {
      this.oldPassword = oldPassword;
   }

   public void setNewPassword(final String newPassword) {
      this.newPassword = newPassword;
   }
}
