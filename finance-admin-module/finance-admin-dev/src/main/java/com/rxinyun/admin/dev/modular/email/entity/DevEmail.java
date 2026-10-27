package com.rxinyun.admin.dev.modular.email.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.rxinyun.common.pojo.CommonEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;

@TableName("DEV_EMAIL")
public class DevEmail extends CommonEntity {
   @Schema(
      description = "id"
   )
   private String id;
   @Schema(
      description = "邮件引擎"
   )
   private String engine;
   @Schema(
      description = "发件人邮箱"
   )
   private String sendAccount;
   @Schema(
      description = "发件人昵称"
   )
   private String sendUser;
   @Schema(
      description = "接收人"
   )
   private String receiveAccounts;
   @Schema(
      description = "邮件主题"
   )
   private String subject;
   @Schema(
      description = "邮件正文"
   )
   private String content;
   @Schema(
      description = "标签名"
   )
   private String tagName;
   @Schema(
      description = "模板名"
   )
   private String templateName;
   @Schema(
      description = "发送参数"
   )
   private String templateParam;
   @Schema(
      description = "回执信息"
   )
   private String receiptInfo;
   @Schema(
      description = "发送状态：PENDING-待发送/SENDING-发送中/SUCCESS-发送成功/FAILED-发送失败"
   )
   private String sendStatus;
   @Schema(
      description = "发送完成时间"
   )
   private Date sendTime;
   @Schema(
      description = "失败原因"
   )
   private String failReason;
   @Schema(
      description = "重试次数"
   )
   private Integer retryCount;
   @Schema(
      description = "扩展信息"
   )
   private String extJson;

   public String getId() {
      return this.id;
   }

   public String getEngine() {
      return this.engine;
   }

   public String getSendAccount() {
      return this.sendAccount;
   }

   public String getSendUser() {
      return this.sendUser;
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

   public String getTagName() {
      return this.tagName;
   }

   public String getTemplateName() {
      return this.templateName;
   }

   public String getTemplateParam() {
      return this.templateParam;
   }

   public String getReceiptInfo() {
      return this.receiptInfo;
   }

   public String getSendStatus() {
      return this.sendStatus;
   }

   public Date getSendTime() {
      return this.sendTime;
   }

   public String getFailReason() {
      return this.failReason;
   }

   public Integer getRetryCount() {
      return this.retryCount;
   }

   public String getExtJson() {
      return this.extJson;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setEngine(final String engine) {
      this.engine = engine;
   }

   public void setSendAccount(final String sendAccount) {
      this.sendAccount = sendAccount;
   }

   public void setSendUser(final String sendUser) {
      this.sendUser = sendUser;
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

   public void setTagName(final String tagName) {
      this.tagName = tagName;
   }

   public void setTemplateName(final String templateName) {
      this.templateName = templateName;
   }

   public void setTemplateParam(final String templateParam) {
      this.templateParam = templateParam;
   }

   public void setReceiptInfo(final String receiptInfo) {
      this.receiptInfo = receiptInfo;
   }

   public void setSendStatus(final String sendStatus) {
      this.sendStatus = sendStatus;
   }

   public void setSendTime(final Date sendTime) {
      this.sendTime = sendTime;
   }

   public void setFailReason(final String failReason) {
      this.failReason = failReason;
   }

   public void setRetryCount(final Integer retryCount) {
      this.retryCount = retryCount;
   }

   public void setExtJson(final String extJson) {
      this.extJson = extJson;
   }
}
