package com.rxinyun.admin.dev.modular.email.param;

import cn.hutool.core.collection.CollectionUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import java.io.File;
import java.util.List;

public class DevEmailSendLocalTxtParam {
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
      description = "附件列表",
      hidden = true
   )
   private List<File> files = CollectionUtil.newArrayList(new File[0]);

   public String getReceiveAccounts() {
      return this.receiveAccounts;
   }

   public String getSubject() {
      return this.subject;
   }

   public String getContent() {
      return this.content;
   }

   public List<File> getFiles() {
      return this.files;
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

   public void setFiles(final List<File> files) {
      this.files = files;
   }
}
