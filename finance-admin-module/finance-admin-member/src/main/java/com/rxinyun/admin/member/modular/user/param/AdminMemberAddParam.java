package com.rxinyun.admin.member.modular.user.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;

public class AdminMemberAddParam {
   @Schema(
      description = "所属会员组ID",
      example = ""
   )
   private String groupId;
   @Schema(
      description = "用户名（账号）",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "用户名不能为空"
   )
   private String account;
   @Schema(
      description = "手机号"
   )
   private String phone;
   @Schema(
      description = "邮箱",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "邮箱不能为空"
   )
   private String email;
   @Schema(
      description = "密码（明文或前端 SM2 加密后传入）",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "密码不能为空"
   )
   private String password;

   public String getGroupId() {
      return this.groupId;
   }

   public String getAccount() {
      return this.account;
   }

   public String getPhone() {
      return this.phone;
   }

   public String getEmail() {
      return this.email;
   }

   public String getPassword() {
      return this.password;
   }

   public void setGroupId(final String groupId) {
      this.groupId = groupId;
   }

   public void setAccount(final String account) {
      this.account = account;
   }

   public void setPhone(final String phone) {
      this.phone = phone;
   }

   public void setEmail(final String email) {
      this.email = email;
   }

   public void setPassword(final String password) {
      this.password = password;
   }

   @Override
   public boolean equals(final Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof AdminMemberAddParam other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else {
         Object this$groupId = this.getGroupId();
         Object other$groupId = other.getGroupId();
         if (this$groupId == null ? other$groupId == null : this$groupId.equals(other$groupId)) {
            Object this$account = this.getAccount();
            Object other$account = other.getAccount();
            if (this$account == null ? other$account == null : this$account.equals(other$account)) {
               Object this$phone = this.getPhone();
               Object other$phone = other.getPhone();
               if (this$phone == null ? other$phone == null : this$phone.equals(other$phone)) {
                  Object this$email = this.getEmail();
                  Object other$email = other.getEmail();
                  if (this$email == null ? other$email == null : this$email.equals(other$email)) {
                     Object this$password = this.getPassword();
                     Object other$password = other.getPassword();
                     return this$password == null ? other$password == null : this$password.equals(other$password);
                  } else {
                     return false;
                  }
               } else {
                  return false;
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   protected boolean canEqual(final Object other) {
      return other instanceof AdminMemberAddParam;
   }

   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $groupId = this.getGroupId();
      result = result * 59 + ($groupId == null ? 43 : $groupId.hashCode());
      Object $account = this.getAccount();
      result = result * 59 + ($account == null ? 43 : $account.hashCode());
      Object $phone = this.getPhone();
      result = result * 59 + ($phone == null ? 43 : $phone.hashCode());
      Object $email = this.getEmail();
      result = result * 59 + ($email == null ? 43 : $email.hashCode());
      Object $password = this.getPassword();
      return result * 59 + ($password == null ? 43 : $password.hashCode());
   }

   @Override
   public String toString() {
      return "AdminMemberAddParam(groupId="
         + this.getGroupId()
         + ", account="
         + this.getAccount()
         + ", phone="
         + this.getPhone()
         + ", email="
         + this.getEmail()
         + ", password="
         + this.getPassword()
         + ")";
   }
}
