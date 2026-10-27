package com.rxinyun.admin.dev.modular.message.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.rxinyun.common.pojo.CommonEntity;
import io.swagger.v3.oas.annotations.media.Schema;

@TableName("tools_telegram_template")
public class ToolsTelegramTemplate extends CommonEntity {
   @Schema(
      description = "主键ID"
   )
   private String id;
   @Schema(
      description = "模板名称"
   )
   private String templateName;
   @Schema(
      description = "模板内容"
   )
   private String templateContent;
   @Schema(
      description = "模板状态 (0:未提交审核,1:通过审核, -1 未通过审核)"
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
