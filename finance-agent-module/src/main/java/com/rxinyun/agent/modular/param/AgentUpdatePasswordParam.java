package com.rxinyun.agent.modular.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;

public class AgentUpdatePasswordParam {
   @Schema(
      description = "旧密码",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "旧密码不能为空"
   )
   private String oldPassword;
   @Schema(
      description = "新密码",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "新密码不能为空"
   )
   private String newPassword;
   @Schema(
      description = "确认密码",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "确认密码不能为空"
   )
   private String confirmPassword;

   public String getOldPassword() {
      return this.oldPassword;
   }

   public String getNewPassword() {
      return this.newPassword;
   }

   public String getConfirmPassword() {
      return this.confirmPassword;
   }

   public void setOldPassword(final String oldPassword) {
      this.oldPassword = oldPassword;
   }

   public void setNewPassword(final String newPassword) {
      this.newPassword = newPassword;
   }

   public void setConfirmPassword(final String confirmPassword) {
      this.confirmPassword = confirmPassword;
   }
}
