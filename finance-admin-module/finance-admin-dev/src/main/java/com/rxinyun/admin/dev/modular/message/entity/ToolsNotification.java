package com.rxinyun.admin.dev.modular.message.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.rxinyun.common.pojo.CommonEntity;
import io.swagger.v3.oas.annotations.media.Schema;

@TableName("tools_notification")
public class ToolsNotification extends CommonEntity {
   @Schema(
      description = "主键ID"
   )
   private String id;
   @Schema(
      description = "通知类型"
   )
   private String type;
   @Schema(
      description = "配置类型：VERIFY_CODE=验证码类型，NOTICE=通知类型"
   )
   private String configType;
   @Schema(
      description = "通知场景说明"
   )
   private String description;
   @Schema(
      description = "公众号模板消息状态 (0:不存在,1:开启,2:关闭)"
   )
   private Integer wechatStatus;
   @Schema(
      description = "公众号模板消息ID"
   )
   private String wechatTemplateId;
   @Schema(
      description = "小程序订阅消息状态 (0:不存在,1:开启,2:关闭)"
   )
   private Integer wxSubscribeStatus;
   @Schema(
      description = "小程序订阅消息ID"
   )
   private String wxSubscribeTemplateId;
   @Schema(
      description = "短信通知状态 (0:不存在,1:开启,2:关闭)"
   )
   private Integer smsStatus;
   @Schema(
      description = "短信模板ID"
   )
   private String smsTemplateId;
   @Schema(
      description = "邮件通知状态 (0:不存在,1:开启,2:关闭)"
   )
   private Integer emailStatus;
   @Schema(
      description = "邮件模板ID"
   )
   private String emailTemplateId;
   @Schema(
      description = "钉钉通知状态 (0:不存在,1:开启,2:关闭)"
   )
   private Integer dingStatus;
   @Schema(
      description = "钉钉模板ID"
   )
   private String dingTemplateId;
   @Schema(
      description = "企业微信通知状态 (0:不存在,1:开启,2:关闭)"
   )
   private Integer wxWorkStatus;
   @Schema(
      description = "企业微信模板ID"
   )
   private String wxWorkTemplateId;
   @Schema(
      description = "Telegram通知状态 (0:不存在,1:开启,2:关闭)"
   )
   private Integer telegramStatus;
   @Schema(
      description = "Telegram模板ID"
   )
   private String telegramTemplateId;
   @Schema(
      description = "发送目标类型 (1:用户,2:管理员)"
   )
   private Integer targetType;
   @Schema(
      description = "启用状态 (1:启用, 0:禁用)"
   )
   private Integer status;
   @Schema(
      description = "场景参数说明，JSON格式，如：{\"code\":\"验证码\",\"username\":\"用户名\",\"amount\":\"金额\"}"
   )
   private String sceneParams;

   public String getId() {
      return this.id;
   }

   public String getType() {
      return this.type;
   }

   public String getConfigType() {
      return this.configType;
   }

   public String getDescription() {
      return this.description;
   }

   public Integer getWechatStatus() {
      return this.wechatStatus;
   }

   public String getWechatTemplateId() {
      return this.wechatTemplateId;
   }

   public Integer getWxSubscribeStatus() {
      return this.wxSubscribeStatus;
   }

   public String getWxSubscribeTemplateId() {
      return this.wxSubscribeTemplateId;
   }

   public Integer getSmsStatus() {
      return this.smsStatus;
   }

   public String getSmsTemplateId() {
      return this.smsTemplateId;
   }

   public Integer getEmailStatus() {
      return this.emailStatus;
   }

   public String getEmailTemplateId() {
      return this.emailTemplateId;
   }

   public Integer getDingStatus() {
      return this.dingStatus;
   }

   public String getDingTemplateId() {
      return this.dingTemplateId;
   }

   public Integer getWxWorkStatus() {
      return this.wxWorkStatus;
   }

   public String getWxWorkTemplateId() {
      return this.wxWorkTemplateId;
   }

   public Integer getTelegramStatus() {
      return this.telegramStatus;
   }

   public String getTelegramTemplateId() {
      return this.telegramTemplateId;
   }

   public Integer getTargetType() {
      return this.targetType;
   }

   public Integer getStatus() {
      return this.status;
   }

   public String getSceneParams() {
      return this.sceneParams;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setType(final String type) {
      this.type = type;
   }

   public void setConfigType(final String configType) {
      this.configType = configType;
   }

   public void setDescription(final String description) {
      this.description = description;
   }

   public void setWechatStatus(final Integer wechatStatus) {
      this.wechatStatus = wechatStatus;
   }

   public void setWechatTemplateId(final String wechatTemplateId) {
      this.wechatTemplateId = wechatTemplateId;
   }

   public void setWxSubscribeStatus(final Integer wxSubscribeStatus) {
      this.wxSubscribeStatus = wxSubscribeStatus;
   }

   public void setWxSubscribeTemplateId(final String wxSubscribeTemplateId) {
      this.wxSubscribeTemplateId = wxSubscribeTemplateId;
   }

   public void setSmsStatus(final Integer smsStatus) {
      this.smsStatus = smsStatus;
   }

   public void setSmsTemplateId(final String smsTemplateId) {
      this.smsTemplateId = smsTemplateId;
   }

   public void setEmailStatus(final Integer emailStatus) {
      this.emailStatus = emailStatus;
   }

   public void setEmailTemplateId(final String emailTemplateId) {
      this.emailTemplateId = emailTemplateId;
   }

   public void setDingStatus(final Integer dingStatus) {
      this.dingStatus = dingStatus;
   }

   public void setDingTemplateId(final String dingTemplateId) {
      this.dingTemplateId = dingTemplateId;
   }

   public void setWxWorkStatus(final Integer wxWorkStatus) {
      this.wxWorkStatus = wxWorkStatus;
   }

   public void setWxWorkTemplateId(final String wxWorkTemplateId) {
      this.wxWorkTemplateId = wxWorkTemplateId;
   }

   public void setTelegramStatus(final Integer telegramStatus) {
      this.telegramStatus = telegramStatus;
   }

   public void setTelegramTemplateId(final String telegramTemplateId) {
      this.telegramTemplateId = telegramTemplateId;
   }

   public void setTargetType(final Integer targetType) {
      this.targetType = targetType;
   }

   public void setStatus(final Integer status) {
      this.status = status;
   }

   public void setSceneParams(final String sceneParams) {
      this.sceneParams = sceneParams;
   }
}
