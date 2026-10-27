package com.rxinyun.member.auth.modular.login.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;

public class AuthUpdateLoginPasswordParam {
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
      description = "密码确认",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "密码确认不能为空"
   )
   private String passwordConfirm;

   public String getOldPassword() {
      return this.oldPassword;
   }

   public String getNewPassword() {
      return this.newPassword;
   }

   public String getPasswordConfirm() {
      return this.passwordConfirm;
   }

   public void setOldPassword(final String oldPassword) {
      this.oldPassword = oldPassword;
   }

   public void setNewPassword(final String newPassword) {
      this.newPassword = newPassword;
   }

   public void setPasswordConfirm(final String passwordConfirm) {
      this.passwordConfirm = passwordConfirm;
   }

   @Override
   public boolean equals(final Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof AuthUpdateLoginPasswordParam other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else {
         Object this$oldPassword = this.getOldPassword();
         Object other$oldPassword = other.getOldPassword();
         if (this$oldPassword == null ? other$oldPassword == null : this$oldPassword.equals(other$oldPassword)) {
            Object this$newPassword = this.getNewPassword();
            Object other$newPassword = other.getNewPassword();
            if (this$newPassword == null ? other$newPassword == null : this$newPassword.equals(other$newPassword)) {
               Object this$passwordConfirm = this.getPasswordConfirm();
               Object other$passwordConfirm = other.getPasswordConfirm();
               return this$passwordConfirm == null ? other$passwordConfirm == null : this$passwordConfirm.equals(other$passwordConfirm);
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   protected boolean canEqual(final Object other) {
      return other instanceof AuthUpdateLoginPasswordParam;
   }

   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $oldPassword = this.getOldPassword();
      result = result * 59 + ($oldPassword == null ? 43 : $oldPassword.hashCode());
      Object $newPassword = this.getNewPassword();
      result = result * 59 + ($newPassword == null ? 43 : $newPassword.hashCode());
      Object $passwordConfirm = this.getPasswordConfirm();
      return result * 59 + ($passwordConfirm == null ? 43 : $passwordConfirm.hashCode());
   }

   @Override
   public String toString() {
      return "AuthUpdateLoginPasswordParam(oldPassword="
         + this.getOldPassword()
         + ", newPassword="
         + this.getNewPassword()
         + ", passwordConfirm="
         + this.getPasswordConfirm()
         + ")";
   }
}
