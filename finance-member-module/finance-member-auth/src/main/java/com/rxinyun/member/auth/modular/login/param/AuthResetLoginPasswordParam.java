package com.rxinyun.member.auth.modular.login.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;

public class AuthResetLoginPasswordParam {
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

   public String getNewPassword() {
      return this.newPassword;
   }

   public String getPasswordConfirm() {
      return this.passwordConfirm;
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
      } else if (!(o instanceof AuthResetLoginPasswordParam other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else {
         Object this$phone = this.getPhone();
         Object other$phone = other.getPhone();
         if (this$phone == null ? other$phone == null : this$phone.equals(other$phone)) {
            Object this$validCode = this.getValidCode();
            Object other$validCode = other.getValidCode();
            if (this$validCode == null ? other$validCode == null : this$validCode.equals(other$validCode)) {
               Object this$validCodeReqNo = this.getValidCodeReqNo();
               Object other$validCodeReqNo = other.getValidCodeReqNo();
               if (this$validCodeReqNo == null ? other$validCodeReqNo == null : this$validCodeReqNo.equals(other$validCodeReqNo)) {
                  Object this$device = this.getDevice();
                  Object other$device = other.getDevice();
                  if (this$device == null ? other$device == null : this$device.equals(other$device)) {
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
      return other instanceof AuthResetLoginPasswordParam;
   }

   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $phone = this.getPhone();
      result = result * 59 + ($phone == null ? 43 : $phone.hashCode());
      Object $validCode = this.getValidCode();
      result = result * 59 + ($validCode == null ? 43 : $validCode.hashCode());
      Object $validCodeReqNo = this.getValidCodeReqNo();
      result = result * 59 + ($validCodeReqNo == null ? 43 : $validCodeReqNo.hashCode());
      Object $device = this.getDevice();
      result = result * 59 + ($device == null ? 43 : $device.hashCode());
      Object $newPassword = this.getNewPassword();
      result = result * 59 + ($newPassword == null ? 43 : $newPassword.hashCode());
      Object $passwordConfirm = this.getPasswordConfirm();
      return result * 59 + ($passwordConfirm == null ? 43 : $passwordConfirm.hashCode());
   }

   @Override
   public String toString() {
      return "AuthResetLoginPasswordParam(phone="
         + this.getPhone()
         + ", validCode="
         + this.getValidCode()
         + ", validCodeReqNo="
         + this.getValidCodeReqNo()
         + ", device="
         + this.getDevice()
         + ", newPassword="
         + this.getNewPassword()
         + ", passwordConfirm="
         + this.getPasswordConfirm()
         + ")";
   }
}
