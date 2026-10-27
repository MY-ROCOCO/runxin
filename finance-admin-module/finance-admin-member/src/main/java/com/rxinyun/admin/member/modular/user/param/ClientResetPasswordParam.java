package com.rxinyun.admin.member.modular.user.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ClientResetPasswordParam {
   @Schema(
      description = "会员ID",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotNull(
      message = "会员ID不能为空"
   )
   private Long id;
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

   public Long getId() {
      return this.id;
   }

   public String getNewPassword() {
      return this.newPassword;
   }

   public String getPasswordConfirm() {
      return this.passwordConfirm;
   }

   public void setId(final Long id) {
      this.id = id;
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
      } else if (!(o instanceof ClientResetPasswordParam other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else {
         Object this$id = this.getId();
         Object other$id = other.getId();
         if (this$id == null ? other$id == null : this$id.equals(other$id)) {
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
      return other instanceof ClientResetPasswordParam;
   }

   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $id = this.getId();
      result = result * 59 + ($id == null ? 43 : $id.hashCode());
      Object $newPassword = this.getNewPassword();
      result = result * 59 + ($newPassword == null ? 43 : $newPassword.hashCode());
      Object $passwordConfirm = this.getPasswordConfirm();
      return result * 59 + ($passwordConfirm == null ? 43 : $passwordConfirm.hashCode());
   }

   @Override
   public String toString() {
      return "ClientResetPasswordParam(id=" + this.getId() + ", newPassword=" + this.getNewPassword() + ", passwordConfirm=" + this.getPasswordConfirm() + ")";
   }
}
