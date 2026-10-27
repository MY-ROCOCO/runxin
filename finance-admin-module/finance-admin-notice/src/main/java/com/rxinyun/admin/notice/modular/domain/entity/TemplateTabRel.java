package com.rxinyun.admin.notice.modular.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;

@TableName(
   value = "template_tab_rel",
   autoResultMap = true
)
public class TemplateTabRel {
   @Schema(
      description = "关系标识"
   )
   private String id;
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
