package com.rxinyun.admin.system.modular.org.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class SysOrgEditParam {
   @Schema(
      description = "id",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "id不能为空"
   )
   private String id;
   @Schema(
      description = "父id",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "parentId不能为空"
   )
   private String parentId;
   @Schema(
      description = "名称",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "name不能为空"
   )
   private String name;
   @Schema(
      description = "分类",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "category不能为空"
   )
   private String category;
   @Schema(
      description = "排序码",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotNull(
      message = "sortCode不能为空"
   )
   private Integer sortCode;
   @Schema(
      description = "主管id"
   )
   private String directorId;
   @Schema(
      description = "扩展JSON"
   )
   private String extJson;

   public String getId() {
      return this.id;
   }

   public String getParentId() {
      return this.parentId;
   }

   public String getName() {
      return this.name;
   }

   public String getCategory() {
      return this.category;
   }

   public Integer getSortCode() {
      return this.sortCode;
   }

   public String getDirectorId() {
      return this.directorId;
   }

   public String getExtJson() {
      return this.extJson;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setParentId(final String parentId) {
      this.parentId = parentId;
   }

   public void setName(final String name) {
      this.name = name;
   }

   public void setCategory(final String category) {
      this.category = category;
   }

   public void setSortCode(final Integer sortCode) {
      this.sortCode = sortCode;
   }

   public void setDirectorId(final String directorId) {
      this.directorId = directorId;
   }

   public void setExtJson(final String extJson) {
      this.extJson = extJson;
   }
}
