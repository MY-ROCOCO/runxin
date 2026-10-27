package com.rxinyun.admin.dev.modular.sms.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;

public class DevSmsSendXiaonuoParam {
   @Schema(
      description = "手机号，多个逗号拼接",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "phoneNumbers不能为空"
   )
   private String phoneNumbers;
   @Schema(
      description = "短信内容"
   )
   private String message;
   @Schema(
      description = "短信签名"
   )
   private String signName;

   public String getPhoneNumbers() {
      return this.phoneNumbers;
   }

   public String getMessage() {
      return this.message;
   }

   public String getSignName() {
      return this.signName;
   }

   public void setPhoneNumbers(final String phoneNumbers) {
      this.phoneNumbers = phoneNumbers;
   }

   public void setMessage(final String message) {
      this.message = message;
   }

   public void setSignName(final String signName) {
      this.signName = signName;
   }
}
