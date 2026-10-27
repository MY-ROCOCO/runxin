package com.rxinyun.admin.dev.modular.message.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public class ToolsNotificationAddParam {
   @Schema(
      description = "通知类型"
   )
   @NotNull(
      message = "通知类型不能为空"
   )
   private String type;
   @Schema(
      description = "配置类型：VERIFY_CODE=验证码类型，NOTICE=通知类型"
   )
   private String configType;
   @Schema(
      description = "通知场景说明"
   )
   @NotNull(
      message = "通知场景说明不能为空"
   )
   private String description;
   @Schema(
      description = "短信通知状态 (0:不存在,1:开启,2:关闭)"
   )
   private Integer smsStatus;
   @Schema(
      description = "邮件通知状态 (0:不存在,1:开启,2:关闭)"
   )
   private Integer emailStatus;
   @Schema(
      description = "公众号通知状态 (0:不存在,1:开启,2:关闭)"
   )
   private Integer wechatStatus;
   @Schema(
      description = "小程序订阅消息状态 (0:不存在,1:开启,2:关闭)"
   )
   private Integer wxSubscribeStatus;
   @Schema(
      description = "钉钉通知状态 (0:不存在,1:开启,2:关闭)"
   )
   private Integer dingStatus;
   @Schema(
      description = "企业微信通知状态 (0:不存在,1:开启,2:关闭)"
   )
   private Integer wxWorkStatus;
   @Schema(
      description = "Telegram通知状态 (0:不存在,1:开启,2:关闭)"
   )
   private Integer telegramStatus;
   @Schema(
      description = "发送目标类型 (1:用户,2:管理员)"
   )
   private Integer targetType;
   @Schema(
      description = "启用状态 (1:启用, 0:禁用)"
   )
   private Integer status;
   @Schema(
      description = "场景参数说明，JSON格式，如：{\"code\":\"验证码\",\"username\":\"用户名\"}"
   )
   private String sceneParams;

   public String getType() {
      return this.type;
   }

   public String getConfigType() {
      return this.configType;
   }

   public String getDescription() {
      return this.description;
   }

   public Integer getSmsStatus() {
      return this.smsStatus;
   }

   public Integer getEmailStatus() {
      return this.emailStatus;
   }

   public Integer getWechatStatus() {
      return this.wechatStatus;
   }

   public Integer getWxSubscribeStatus() {
      return this.wxSubscribeStatus;
   }

   public Integer getDingStatus() {
      return this.dingStatus;
   }

   public Integer getWxWorkStatus() {
      return this.wxWorkStatus;
   }

   public Integer getTelegramStatus() {
      return this.telegramStatus;
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

   public void setType(final String type) {
      this.type = type;
   }

   public void setConfigType(final String configType) {
      this.configType = configType;
   }

   public void setDescription(final String description) {
      this.description = description;
   }

   public void setSmsStatus(final Integer smsStatus) {
      this.smsStatus = smsStatus;
   }

   public void setEmailStatus(final Integer emailStatus) {
      this.emailStatus = emailStatus;
   }

   public void setWechatStatus(final Integer wechatStatus) {
      this.wechatStatus = wechatStatus;
   }

   public void setWxSubscribeStatus(final Integer wxSubscribeStatus) {
      this.wxSubscribeStatus = wxSubscribeStatus;
   }

   public void setDingStatus(final Integer dingStatus) {
      this.dingStatus = dingStatus;
   }

   public void setWxWorkStatus(final Integer wxWorkStatus) {
      this.wxWorkStatus = wxWorkStatus;
   }

   public void setTelegramStatus(final Integer telegramStatus) {
      this.telegramStatus = telegramStatus;
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
