package com.rxinyun.admin.news.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;

public class NewsDto {
   @Schema(
      description = "新闻id"
   )
   private String id;
   @Schema(
      description = "标题",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "title不能为空"
   )
   @JsonProperty("title")
   private String title;
   @Schema(
      description = "是否置顶,0为不置顶,1为置顶,默认不置顶"
   )
   private Integer property;
   @Schema(
      description = "新闻关键字"
   )
   private String keyword;
   @Schema(
      description = "新闻描述"
   )
   private String description;
   @Schema(
      description = "新闻内容",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "content不能为空"
   )
   private String content;
   @Schema(
      description = "内容格式: 0 纯文本 1 HTML，默认 0"
   )
   private Integer contentFormat;
   @Schema(
      description = "新闻发布状态,0为未发布,1为发布,默认发布"
   )
   private Integer status;
   @Schema(
      description = "新闻类型的id",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "typeId不能为空"
   )
   private String typeId;

   public String getId() {
      return this.id;
   }

   public String getTitle() {
      return this.title;
   }

   public Integer getProperty() {
      return this.property;
   }

   public String getKeyword() {
      return this.keyword;
   }

   public String getDescription() {
      return this.description;
   }

   public String getContent() {
      return this.content;
   }

   public Integer getContentFormat() {
      return this.contentFormat;
   }

   public Integer getStatus() {
      return this.status;
   }

   public String getTypeId() {
      return this.typeId;
   }

   public void setId(final String id) {
      this.id = id;
   }

   @JsonProperty("title")
   public void setTitle(final String title) {
      this.title = title;
   }

   public void setProperty(final Integer property) {
      this.property = property;
   }

   public void setKeyword(final String keyword) {
      this.keyword = keyword;
   }

   public void setDescription(final String description) {
      this.description = description;
   }

   public void setContent(final String content) {
      this.content = content;
   }

   public void setContentFormat(final Integer contentFormat) {
      this.contentFormat = contentFormat;
   }

   public void setStatus(final Integer status) {
      this.status = status;
   }

   public void setTypeId(final String typeId) {
      this.typeId = typeId;
   }
}
