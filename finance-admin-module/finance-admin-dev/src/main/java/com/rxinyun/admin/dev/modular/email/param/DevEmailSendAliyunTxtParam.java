package com.rxinyun.admin.dev.modular.email.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;

public class DevEmailSendAliyunTxtParam {
   @Schema(
      description = "发件人邮箱，即管理控制台中配置的发信地址",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "sendAccount不能为空"
   )
   private String sendAccount;
   @Schema(
      description = "接收人邮箱地址，多个逗号拼接",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "receiveAccounts不能为空"
   )
   private String receiveAccounts;
   @Schema(
      description = "邮件主题",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "subject不能为空"
   )
   private String subject;
   @Schema(
      description = "邮件正文",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "content不能为空"
   )
   private String content;
   @Schema(
      description = "发件人昵称"
   )
   private String sendUser;

   public String getSendAccount() {
      return this.sendAccount;
   }

   public String getReceiveAccounts() {
      return this.receiveAccounts;
   }

   public String getSubject() {
      return this.subject;
   }

   public String getContent() {
      return this.content;
   }

   public String getSendUser() {
      return this.sendUser;
   }

   public void setSendAccount(final String sendAccount) {
      this.sendAccount = sendAccount;
   }

   public void setReceiveAccounts(final String receiveAccounts) {
      this.receiveAccounts = receiveAccounts;
   }

   public void setSubject(final String subject) {
      this.subject = subject;
   }

   public void setContent(final String content) {
      this.content = content;
   }

   public void setSendUser(final String sendUser) {
      this.sendUser = sendUser;
   }
}
