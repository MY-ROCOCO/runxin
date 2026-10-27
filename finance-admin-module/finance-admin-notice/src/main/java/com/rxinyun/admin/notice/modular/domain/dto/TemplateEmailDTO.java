package com.rxinyun.admin.notice.modular.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class TemplateEmailDTO {
   @Schema(
      description = "邮件模板标识"
   )
   private String id;
   @NotBlank(
      message = "邮件标题不能为空"
   )
   @Schema(
      description = "邮件标题"
   )
   private String title;
   @NotNull(
      message = "状态不能为空,0关闭，1开启"
   )
   @Schema(
      description = "是否开启发送邮件，0关闭，1开启"
   )
   private Integer status;
   @NotBlank(
      message = "邮件内容不能为空"
   )
   @Schema(
      description = "邮件内容"
   )
   private String content;
   @NotBlank(
      message = "通知模板id不能为空"
   )
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
