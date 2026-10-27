package com.rxinyun.admin.dev.modular.message.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.rxinyun.common.pojo.CommonEntity;
import io.swagger.v3.oas.annotations.media.Schema;

@TableName("tools_email_template")
public class ToolsEmailTemplate extends CommonEntity {
   @Schema(
      description = "主键ID"
   )
   private String id;
   @Schema(
      description = "模板编码"
   )
   private String templateCode;
   @Schema(
      description = "模板名称"
   )
   private String templateName;
   @Schema(
      description = "模板主题"
   )
   private String templateSubject;
   @Schema(
      description = "模板正文"
   )
   private String templateContent;
   @Schema(
      description = "标签名"
   )
   private String tagName;
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

   public String getTemplateCode() {
      return this.templateCode;
   }

   public String getTemplateName() {
      return this.templateName;
   }

   public String getTemplateSubject() {
      return this.templateSubject;
   }

   public String getTemplateContent() {
      return this.templateContent;
   }

   public String getTagName() {
      return this.tagName;
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

   public void setTemplateCode(final String templateCode) {
      this.templateCode = templateCode;
   }

   public void setTemplateName(final String templateName) {
      this.templateName = templateName;
   }

   public void setTemplateSubject(final String templateSubject) {
      this.templateSubject = templateSubject;
   }

   public void setTemplateContent(final String templateContent) {
      this.templateContent = templateContent;
   }

   public void setTagName(final String tagName) {
      this.tagName = tagName;
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
