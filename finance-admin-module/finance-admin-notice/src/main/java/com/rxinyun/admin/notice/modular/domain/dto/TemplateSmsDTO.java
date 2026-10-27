package com.rxinyun.admin.notice.modular.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class TemplateSmsDTO {
   @Schema(
      description = "短信模板标识"
   )
   private String id;
   @Schema(
      description = "模板ID/CODE"
   )
   private String code;
   @NotNull(
      message = "状态不能为空,0关闭，1开启"
   )
   @Schema(
      description = "是否开启发送短信，0关闭，1开启"
   )
   private Integer status;
   @NotBlank(
      message = "内容不能为空"
   )
   @Schema(
      description = "短信内容"
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

   public String getCode() {
      return this.code;
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

   public void setCode(final String code) {
      this.code = code;
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
