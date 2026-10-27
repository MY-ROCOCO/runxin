package com.rxinyun.admin.dev.modular.email.param;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

public class DevEmailSendTencentTmpParam {
   @Schema(
      description = "管理控制台中配置的发信地址",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "sendAccount不能为空"
   )
   private String sendAccount;
   @Schema(
      description = "预先创建且上传了收件人的收件人列表id",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "receiveAccounts不能为空"
   )
   private String receiveAccounts;
   @Schema(
      description = "预先创建且通过审核的模板Id",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "templateName不能为空"
   )
   private String templateName;
   @Schema(
      description = "邮件主题",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "subject不能为空"
   )
   private String subject;
   @Schema(
      description = "发件人昵称"
   )
   private String sendUser;
   @Schema(
      description = "预先创建且通过审核的模板的参数json"
   )
   private String templateParam;
   @Schema(
      description = "附件列表",
      hidden = true
   )
   List<JSONObject> attachmentList = CollectionUtil.newArrayList(new JSONObject[0]);

   public String getSendAccount() {
      return this.sendAccount;
   }

   public String getReceiveAccounts() {
      return this.receiveAccounts;
   }

   public String getTemplateName() {
      return this.templateName;
   }

   public String getSubject() {
      return this.subject;
   }

   public String getSendUser() {
      return this.sendUser;
   }

   public String getTemplateParam() {
      return this.templateParam;
   }

   public List<JSONObject> getAttachmentList() {
      return this.attachmentList;
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

   public void setSubject(final String subject) {
      this.subject = subject;
   }

   public void setSendUser(final String sendUser) {
      this.sendUser = sendUser;
   }

   public void setTemplateParam(final String templateParam) {
      this.templateParam = templateParam;
   }

   public void setAttachmentList(final List<JSONObject> attachmentList) {
      this.attachmentList = attachmentList;
   }
}
