package com.rxinyun.admin.dev.modular.message.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public class ToolsTelegramTemplateEditParam {
   @Schema(
      description = "主键ID"
   )
   @NotNull(
      message = "ID不能为空"
   )
   private String id;
   @Schema(
      description = "模板名称"
   )
   @NotNull(
      message = "模板名称不能为空"
   )
   private String templateName;
   @Schema(
      description = "模板内容"
   )
   @NotNull(
      message = "模板内容不能为空"
   )
   private String templateContent;
   @Schema(
      description = "模板状态 (0:未提交审核,1:通过审核, -1 未通过审核)"
   )
   @NotNull(
      message = "模板状态不能为空"
   )
   private Integer status;
   @Schema(
      description = "应用场景"
   )
   private String scene;
   @Schema(
      description = "应用场景描述"
   )
   private String sceneDesc;
   @Schema(
      description = "备注说明"
   )
   private String remark;

   public String getId() {
      return this.id;
   }

   public String getTemplateName() {
      return this.templateName;
   }

   public String getTemplateContent() {
      return this.templateContent;
   }

   public Integer getStatus() {
      return this.status;
   }

   public String getScene() {
      return this.scene;
   }

   public String getSceneDesc() {
      return this.sceneDesc;
   }

   public String getRemark() {
      return this.remark;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setTemplateName(final String templateName) {
      this.templateName = templateName;
   }

   public void setTemplateContent(final String templateContent) {
      this.templateContent = templateContent;
   }

   public void setStatus(final Integer status) {
      this.status = status;
   }

   public void setScene(final String scene) {
      this.scene = scene;
   }

   public void setSceneDesc(final String sceneDesc) {
      this.sceneDesc = sceneDesc;
   }

   public void setRemark(final String remark) {
      this.remark = remark;
   }
}
