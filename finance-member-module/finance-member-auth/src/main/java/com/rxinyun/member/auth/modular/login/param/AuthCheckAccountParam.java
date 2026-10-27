package com.rxinyun.member.auth.modular.login.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;

public class AuthCheckAccountParam {
   @Schema(
      description = "账号",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "账号不能为空"
   )
   private String account;
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

   public String getAccount() {
      return this.account;
   }

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

   public void setAccount(final String account) {
      this.account = account;
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

   @Override
   public boolean equals(final Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof AuthCheckAccountParam other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else {
         Object this$account = this.getAccount();
         Object other$account = other.getAccount();
         if (this$account == null ? other$account == null : this$account.equals(other$account)) {
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
                     return this$device == null ? other$device == null : this$device.equals(other$device);
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
      return other instanceof AuthCheckAccountParam;
   }

   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $account = this.getAccount();
      result = result * 59 + ($account == null ? 43 : $account.hashCode());
      Object $phone = this.getPhone();
      result = result * 59 + ($phone == null ? 43 : $phone.hashCode());
      Object $validCode = this.getValidCode();
      result = result * 59 + ($validCode == null ? 43 : $validCode.hashCode());
      Object $validCodeReqNo = this.getValidCodeReqNo();
      result = result * 59 + ($validCodeReqNo == null ? 43 : $validCodeReqNo.hashCode());
      Object $device = this.getDevice();
      return result * 59 + ($device == null ? 43 : $device.hashCode());
   }

   @Override
   public String toString() {
      return "AuthCheckAccountParam(account="
         + this.getAccount()
         + ", phone="
         + this.getPhone()
         + ", validCode="
         + this.getValidCode()
         + ", validCodeReqNo="
         + this.getValidCodeReqNo()
         + ", device="
         + this.getDevice()
         + ")";
   }
}
