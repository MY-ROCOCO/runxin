package com.rxinyun.admin.member.modular.user.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ClientEmailUpdateParam {
   @Schema(
      description = "会员ID",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotNull(
      message = "会员ID不能为空"
   )
   private Long id;
   @Schema(
      description = "新邮箱",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "新邮箱不能为空"
   )
   private String email;

   public Long getId() {
      return this.id;
   }

   public String getEmail() {
      return this.email;
   }

   public void setId(final Long id) {
      this.id = id;
   }

   public void setEmail(final String email) {
      this.email = email;
   }

   @Override
   public boolean equals(final Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof ClientEmailUpdateParam other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else {
         Object this$id = this.getId();
         Object other$id = other.getId();
         if (this$id == null ? other$id == null : this$id.equals(other$id)) {
            Object this$email = this.getEmail();
            Object other$email = other.getEmail();
            return this$email == null ? other$email == null : this$email.equals(other$email);
         } else {
            return false;
         }
      }
   }

   protected boolean canEqual(final Object other) {
      return other instanceof ClientEmailUpdateParam;
   }

   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $id = this.getId();
      result = result * 59 + ($id == null ? 43 : $id.hashCode());
      Object $email = this.getEmail();
      return result * 59 + ($email == null ? 43 : $email.hashCode());
   }

   @Override
   public String toString() {
      return "ClientEmailUpdateParam(id=" + this.getId() + ", email=" + this.getEmail() + ")";
   }
}
