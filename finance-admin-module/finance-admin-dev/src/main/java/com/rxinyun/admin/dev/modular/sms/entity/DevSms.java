package com.rxinyun.admin.dev.modular.sms.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.rxinyun.common.pojo.CommonEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;

@TableName("DEV_SMS")
public class DevSms extends CommonEntity {
   @Schema(
      description = "id"
   )
   private String id;
   @Schema(
      description = "短信引擎"
   )
   private String engine;
   @Schema(
      description = "手机号"
   )
   private String phoneNumbers;
   @Schema(
      description = "短信签名"
   )
   private String signName;
   @Schema(
      description = "模板编码"
   )
   private String templateCode;
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
      description = "IP地址"
   )
   private String ip;
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

   public String getPhoneNumbers() {
      return this.phoneNumbers;
   }

   public String getSignName() {
      return this.signName;
   }

   public String getTemplateCode() {
      return this.templateCode;
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

   public String getIp() {
      return this.ip;
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

   public void setPhoneNumbers(final String phoneNumbers) {
      this.phoneNumbers = phoneNumbers;
   }

   public void setSignName(final String signName) {
      this.signName = signName;
   }

   public void setTemplateCode(final String templateCode) {
      this.templateCode = templateCode;
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

   public void setIp(final String ip) {
      this.ip = ip;
   }

   public void setExtJson(final String extJson) {
      this.extJson = extJson;
   }
}
