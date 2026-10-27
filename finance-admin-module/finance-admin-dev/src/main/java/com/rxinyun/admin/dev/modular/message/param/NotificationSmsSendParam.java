package com.rxinyun.admin.dev.modular.message.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import java.util.Map;

public class NotificationSmsSendParam {
   @Schema(
      description = "消息通知场景ID",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "场景ID不能为空"
   )
   private String notificationId;
   @Schema(
      description = "手机号，多个逗号拼接",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "手机号不能为空"
   )
   private String phoneNumbers;
   @Schema(
      description = "短信模板变量对应的实际值，如：{username: '张三', code: '123456'}"
   )
   private Map<String, Object> templateParams;
   @Schema(
      description = "是否强制发送（忽略频率限制），默认false"
   )
   private Boolean forceSend = false;
   @Schema(
      description = "业务标识（用于记录业务场景），如：USER_REGISTER、ORDER_PAYMENT等"
   )
   private String businessFlag;

   public String getNotificationId() {
      return this.notificationId;
   }

   public String getPhoneNumbers() {
      return this.phoneNumbers;
   }

   public Map<String, Object> getTemplateParams() {
      return this.templateParams;
   }

   public Boolean getForceSend() {
      return this.forceSend;
   }

   public String getBusinessFlag() {
      return this.businessFlag;
   }

   public void setNotificationId(final String notificationId) {
      this.notificationId = notificationId;
   }

   public void setPhoneNumbers(final String phoneNumbers) {
      this.phoneNumbers = phoneNumbers;
   }

   public void setTemplateParams(final Map<String, Object> templateParams) {
      this.templateParams = templateParams;
   }

   public void setForceSend(final Boolean forceSend) {
      this.forceSend = forceSend;
   }

   public void setBusinessFlag(final String businessFlag) {
      this.businessFlag = businessFlag;
   }
}
