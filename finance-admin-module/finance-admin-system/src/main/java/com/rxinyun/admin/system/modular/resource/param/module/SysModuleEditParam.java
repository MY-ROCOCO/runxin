package com.rxinyun.admin.system.modular.resource.param.module;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class SysModuleEditParam {
   @Schema(
      description = "id",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "id不能为空"
   )
   private String id;
   @Schema(
      description = "标题",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "title不能为空"
   )
   private String title;
   @Schema(
      description = "图标",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "icon不能为空"
   )
   private String icon;
   @Schema(
      description = "颜色",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "color不能为空"
   )
   private String color;
   @Schema(
      description = "排序码",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotNull(
      message = "sortCode不能为空"
   )
   private Integer sortCode;
   @Schema(
      description = "扩展信息"
   )
   private String extJson;

   public String getId() {
      return this.id;
   }

   public String getTitle() {
      return this.title;
   }

   public String getIcon() {
      return this.icon;
   }

   public String getColor() {
      return this.color;
   }

   public Integer getSortCode() {
      return this.sortCode;
   }

   public String getExtJson() {
      return this.extJson;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setTitle(final String title) {
      this.title = title;
   }

   public void setIcon(final String icon) {
      this.icon = icon;
   }

   public void setColor(final String color) {
      this.color = color;
   }

   public void setSortCode(final Integer sortCode) {
      this.sortCode = sortCode;
   }

   public void setExtJson(final String extJson) {
      this.extJson = extJson;
   }
}
