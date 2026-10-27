package com.rxinyun.admin.notice.modular.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public class TabEmailSmsParam {
   @Schema(
      description = "关系标识"
   )
   private String id;
   @NotBlank(
      message = "标签id不能为空"
   )
   @Schema(
      description = "标签ID"
   )
   private String tabId;
   @Schema(
      description = "邮件模板ID"
   )
   private String emailId;
   @Schema(
      description = "短信模板ID"
   )
   private String smsId;

   public String getId() {
      return this.id;
   }

   public String getTabId() {
      return this.tabId;
   }

   public String getEmailId() {
      return this.emailId;
   }

   public String getSmsId() {
      return this.smsId;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setTabId(final String tabId) {
      this.tabId = tabId;
   }

   public void setEmailId(final String emailId) {
      this.emailId = emailId;
   }

   public void setSmsId(final String smsId) {
      this.smsId = smsId;
   }
}
