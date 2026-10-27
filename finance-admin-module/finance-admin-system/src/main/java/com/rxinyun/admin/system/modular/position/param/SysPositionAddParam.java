package com.rxinyun.admin.system.modular.position.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class SysPositionAddParam {
   @Schema(
      description = "组织id",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "orgId不能为空"
   )
   private String orgId;
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
      description = "扩展JSON"
   )
   private String extJson;

   public String getOrgId() {
      return this.orgId;
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

   public String getExtJson() {
      return this.extJson;
   }

   public void setOrgId(final String orgId) {
      this.orgId = orgId;
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

   public void setExtJson(final String extJson) {
      this.extJson = extJson;
   }
}
