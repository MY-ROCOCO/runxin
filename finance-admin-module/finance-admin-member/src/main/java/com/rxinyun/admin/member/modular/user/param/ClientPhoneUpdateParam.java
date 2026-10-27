package com.rxinyun.admin.member.modular.user.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ClientPhoneUpdateParam {
   @Schema(
      description = "会员ID",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotNull(
      message = "会员ID不能为空"
   )
   private Long id;
   @Schema(
      description = "新手机号",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "新手机号不能为空"
   )
   private String phone;

   public Long getId() {
      return this.id;
   }

   public String getPhone() {
      return this.phone;
   }

   public void setId(final Long id) {
      this.id = id;
   }

   public void setPhone(final String phone) {
      this.phone = phone;
   }

   @Override
   public boolean equals(final Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof ClientPhoneUpdateParam other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else {
         Object this$id = this.getId();
         Object other$id = other.getId();
         if (this$id == null ? other$id == null : this$id.equals(other$id)) {
            Object this$phone = this.getPhone();
            Object other$phone = other.getPhone();
            return this$phone == null ? other$phone == null : this$phone.equals(other$phone);
         } else {
            return false;
         }
      }
   }

   protected boolean canEqual(final Object other) {
      return other instanceof ClientPhoneUpdateParam;
   }

   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $id = this.getId();
      result = result * 59 + ($id == null ? 43 : $id.hashCode());
      Object $phone = this.getPhone();
      return result * 59 + ($phone == null ? 43 : $phone.hashCode());
   }

   @Override
   public String toString() {
      return "ClientPhoneUpdateParam(id=" + this.getId() + ", phone=" + this.getPhone() + ")";
   }
}
