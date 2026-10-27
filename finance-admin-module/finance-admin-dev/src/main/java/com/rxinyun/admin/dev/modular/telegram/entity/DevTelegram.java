package com.rxinyun.admin.dev.modular.telegram.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.rxinyun.common.pojo.CommonEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;

@TableName("DEV_TELEGRAM")
public class DevTelegram extends CommonEntity {
   @Schema(
      description = "id"
   )
   private String id;
   @Schema(
      description = "Telegram chat id"
   )
   private String chatId;
   @Schema(
      description = "消息内容"
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
