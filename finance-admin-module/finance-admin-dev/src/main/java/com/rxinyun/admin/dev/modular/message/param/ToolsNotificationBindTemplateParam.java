package com.rxinyun.admin.dev.modular.message.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public class ToolsNotificationBindTemplateParam {
   @Schema(
      description = "场景id"
   )
   @NotBlank(
      message = "场景id不能为空"
   )
   private String id;
   @Schema(
      description = "公众号模板消息ID"
   )
   private String wechatTemplateId;
   @Schema(
      description = "小程序订阅消息ID"
   )
   private String wxSubscribeTemplateId;
   @Schema(
      description = "短信模板ID"
   )
   private String smsTemplateId;
   @Schema(
      description = "邮件模板ID"
   )
   private String emailTemplateId;
   @Schema(
      description = "钉钉模板ID"
   )
   private String dingTemplateId;
   @Schema(
      description = "企业微信模板ID"
   )
   private String wxWorkTemplateId;
   @Schema(
      description = "Telegram模板ID"
   )
   private String telegramTemplateId;

   public String getId() {
      return this.id;
   }

   public String getWechatTemplateId() {
      return this.wechatTemplateId;
   }

   public String getWxSubscribeTemplateId() {
      return this.wxSubscribeTemplateId;
   }

   public String getSmsTemplateId() {
      return this.smsTemplateId;
   }

   public String getEmailTemplateId() {
      return this.emailTemplateId;
   }

   public String getDingTemplateId() {
      return this.dingTemplateId;
   }

   public String getWxWorkTemplateId() {
      return this.wxWorkTemplateId;
   }

   public String getTelegramTemplateId() {
      return this.telegramTemplateId;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setWechatTemplateId(final String wechatTemplateId) {
      this.wechatTemplateId = wechatTemplateId;
   }

   public void setWxSubscribeTemplateId(final String wxSubscribeTemplateId) {
      this.wxSubscribeTemplateId = wxSubscribeTemplateId;
   }

   public void setSmsTemplateId(final String smsTemplateId) {
      this.smsTemplateId = smsTemplateId;
   }

   public void setEmailTemplateId(final String emailTemplateId) {
      this.emailTemplateId = emailTemplateId;
   }

   public void setDingTemplateId(final String dingTemplateId) {
      this.dingTemplateId = dingTemplateId;
   }

   public void setWxWorkTemplateId(final String wxWorkTemplateId) {
      this.wxWorkTemplateId = wxWorkTemplateId;
   }

   public void setTelegramTemplateId(final String telegramTemplateId) {
      this.telegramTemplateId = telegramTemplateId;
   }
}
