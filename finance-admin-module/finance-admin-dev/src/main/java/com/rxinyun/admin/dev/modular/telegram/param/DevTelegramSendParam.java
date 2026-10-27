package com.rxinyun.admin.dev.modular.telegram.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public class DevTelegramSendParam {
   @Schema(
      description = "Telegram chat id"
   )
   @NotBlank(
      message = "chatId不能为空"
   )
   private String chatId;
   @Schema(
      description = "消息内容"
   )
   @NotBlank(
      message = "消息内容不能为空"
   )
   private String content;
   @Schema(
      description = "模板名"
   )
   private String templateName;
   @Schema(
      description = "发送参数"
   )
   private String templateParam;
   @Schema(
      description = "Telegram解析模式，例如 HTML"
   )
   private String parseMode;

   public String getChatId() {
      return this.chatId;
   }

   public String getContent() {
      return this.content;
   }

   public String getTemplateName() {
      return this.templateName;
   }

   public String getTemplateParam() {
      return this.templateParam;
   }

   public String getParseMode() {
      return this.parseMode;
   }

   public void setChatId(final String chatId) {
      this.chatId = chatId;
   }

   public void setContent(final String content) {
      this.content = content;
   }

   public void setTemplateName(final String templateName) {
      this.templateName = templateName;
   }

   public void setTemplateParam(final String templateParam) {
      this.templateParam = templateParam;
   }

   public void setParseMode(final String parseMode) {
      this.parseMode = parseMode;
   }
}
