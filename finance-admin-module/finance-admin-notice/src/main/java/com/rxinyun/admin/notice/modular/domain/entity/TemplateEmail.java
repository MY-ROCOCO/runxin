package com.rxinyun.admin.notice.modular.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;

@TableName(
   value = "template_email",
   autoResultMap = true
)
public class TemplateEmail {
   @Schema(
      description = "邮件模板标识"
   )
   private String id;
   @Schema(
      description = "邮件标题"
   )
   private String title;
   @Schema(
      description = "是否开启发送邮件，0关闭，1开启"
   )
   private Integer status;
   @Schema(
      description = "邮件内容"
   )
   private String content;
   @Schema(
      description = "通知模板id"
   )
   private String templateId;

   public String getId() {
      return this.id;
   }

   public String getTitle() {
      return this.title;
   }

   public Integer getStatus() {
      return this.status;
   }

   public String getContent() {
      return this.content;
   }

   public String getTemplateId() {
      return this.templateId;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setTitle(final String title) {
      this.title = title;
   }

   public void setStatus(final Integer status) {
      this.status = status;
   }

   public void setContent(final String content) {
      this.content = content;
   }

   public void setTemplateId(final String templateId) {
      this.templateId = templateId;
   }
}
