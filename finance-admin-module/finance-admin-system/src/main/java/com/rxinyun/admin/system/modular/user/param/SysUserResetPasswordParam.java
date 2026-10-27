package com.rxinyun.admin.system.modular.user.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;

public class SysUserResetPasswordParam {
   @Schema(
      description = "id",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "id不能为空"
   )
   private String id;
   @Schema(
      description = "新密码",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "新密码不能为空"
   )
   private String newPassword;

   public String getId() {
      return this.id;
   }

   public String getNewPassword() {
      return this.newPassword;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setNewPassword(final String newPassword) {
      this.newPassword = newPassword;
   }
}
