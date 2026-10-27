package com.rxinyun.admin.dev.modular.sms.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;

public class DevSmsSendTencentParam {
   @Schema(
      description = "手机号，多个逗号拼接",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "phoneNumbers不能为空"
   )
   private String phoneNumbers;
   @Schema(
      description = "短信服务控制台配置且审核通过的模板编码",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "templateCode不能为空"
   )
   private String templateCode;
   @Schema(
      description = "短信模板变量对应的顺序。支持传入多个参数，逗号拼接"
   )
   private String templateParam;
   @Schema(
      description = "在短信控制台添加应用后生成的实际SdkAppId"
   )
   private String sdkAppId;
   @Schema(
      description = "短信服务控制台配置且审核通过的短信签名"
   )
   private String signName;

   public String getPhoneNumbers() {
      return this.phoneNumbers;
   }

   public String getTemplateCode() {
      return this.templateCode;
   }

   public String getTemplateParam() {
      return this.templateParam;
   }

   public String getSdkAppId() {
      return this.sdkAppId;
   }

   public String getSignName() {
      return this.signName;
   }

   public void setPhoneNumbers(final String phoneNumbers) {
      this.phoneNumbers = phoneNumbers;
   }

   public void setTemplateCode(final String templateCode) {
      this.templateCode = templateCode;
   }

   public void setTemplateParam(final String templateParam) {
      this.templateParam = templateParam;
   }

   public void setSdkAppId(final String sdkAppId) {
      this.sdkAppId = sdkAppId;
   }

   public void setSignName(final String signName) {
      this.signName = signName;
   }
}
