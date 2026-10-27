package com.rxinyun.admin.dev.modular.email.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;

public class DevEmailSendAliyunTmpParam {
   @Schema(
      description = "管理控制台中配置的发信地址",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "sendAccount不能为空"
   )
   private String sendAccount;
   @Schema(
      description = "预先创建且上传了收件人的收件人列表名称",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "receiveAccounts不能为空"
   )
   private String receiveAccounts;
   @Schema(
      description = "预先创建且通过审核的模板名称",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "templateName不能为空"
   )
   private String templateName;
   @Schema(
      description = "标签名"
   )
   private String tagName;

   public String getSendAccount() {
      return this.sendAccount;
   }

   public String getReceiveAccounts() {
      return this.receiveAccounts;
   }

   public String getTemplateName() {
      return this.templateName;
   }

   public String getTagName() {
      return this.tagName;
   }

   public void setSendAccount(final String sendAccount) {
      this.sendAccount = sendAccount;
   }

   public void setReceiveAccounts(final String receiveAccounts) {
      this.receiveAccounts = receiveAccounts;
   }

   public void setTemplateName(final String templateName) {
      this.templateName = templateName;
   }

   public void setTagName(final String tagName) {
      this.tagName = tagName;
   }
}
