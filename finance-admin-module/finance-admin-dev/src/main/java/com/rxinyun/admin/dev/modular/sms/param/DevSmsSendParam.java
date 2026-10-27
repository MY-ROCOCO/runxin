package com.rxinyun.admin.dev.modular.sms.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;

public class DevSmsSendParam {
   @Schema(
      description = "手机号，多个逗号拼接",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "phoneNumbers不能为空"
   )
   private String phoneNumbers;
   @Schema(
      description = "短信服务控制台配置且审核通过的模板编码"
   )
   private String templateCode;
   @Schema(
      description = "短信场景标识，用于从模板表中获取模板信息"
   )
   private String scene;
   @Schema(
      description = "短信模板变量对应的实际值，JSON格式"
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
   @Schema(
      description = "短信模板内容，短信宝发送时使用"
   )
   private String templateContent;
   @Schema(
      description = "IP地址"
   )
   private String ip;

   public String getPhoneNumbers() {
      return this.phoneNumbers;
   }

   public String getTemplateCode() {
      return this.templateCode;
   }

   public String getScene() {
      return this.scene;
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

   public String getTemplateContent() {
      return this.templateContent;
   }

   public String getIp() {
      return this.ip;
   }

   public void setPhoneNumbers(final String phoneNumbers) {
      this.phoneNumbers = phoneNumbers;
   }

   public void setTemplateCode(final String templateCode) {
      this.templateCode = templateCode;
   }

   public void setScene(final String scene) {
      this.scene = scene;
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

   public void setTemplateContent(final String templateContent) {
      this.templateContent = templateContent;
   }

   public void setIp(final String ip) {
      this.ip = ip;
   }
}
