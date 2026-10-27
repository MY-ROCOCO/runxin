package com.rxinyun.admin.dev.modular.sms.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;

public class DevSmsSendAliyunParam {
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
      description = "短信模板变量对应的实际值，JSON格式"
   )
   private String templateParam;
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

   public void setSignName(final String signName) {
      this.signName = signName;
   }
}
