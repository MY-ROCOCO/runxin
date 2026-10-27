package com.rxinyun.admin.system.modular.role.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class SysRoleEditParam {
   @Schema(
      description = "id",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "id不能为空"
   )
   private String id;
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
      description = "组织id"
   )
   private String orgId;
   @Schema(
      description = "扩展JSON"
   )
   private String extJson;

   public String getId() {
      return this.id;
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

   public String getOrgId() {
      return this.orgId;
   }

   public String getExtJson() {
      return this.extJson;
   }

   public void setId(final String id) {
      this.id = id;
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

   public void setOrgId(final String orgId) {
      this.orgId = orgId;
   }

   public void setExtJson(final String extJson) {
      this.extJson = extJson;
   }
}
