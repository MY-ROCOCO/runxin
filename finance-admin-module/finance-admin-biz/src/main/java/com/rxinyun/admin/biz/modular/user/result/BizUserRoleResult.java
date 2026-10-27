package com.rxinyun.admin.biz.modular.user.result;

import io.swagger.v3.oas.annotations.media.Schema;

public class BizUserRoleResult {
   @Schema(
      description = "id"
   )
   private String id;
   @Schema(
      description = "机构id"
   )
   private String orgId;
   @Schema(
      description = "名称"
   )
   private String name;
   @Schema(
      description = "分类"
   )
   private String category;
   @Schema(
      description = "排序码"
   )
   private Integer sortCode;

   public String getId() {
      return this.id;
   }

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

   public void setId(final String id) {
      this.id = id;
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
}
