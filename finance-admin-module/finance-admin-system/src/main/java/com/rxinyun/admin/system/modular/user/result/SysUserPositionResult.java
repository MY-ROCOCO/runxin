package com.rxinyun.admin.system.modular.user.result;

import io.swagger.v3.oas.annotations.media.Schema;

public class SysUserPositionResult {
   @Schema(
      description = "组织id"
   )
   private String orgId;
   @Schema(
      description = "组织名称"
   )
   private String orgName;
   @Schema(
      description = "职位id"
   )
   private String positionId;
   @Schema(
      description = "职位名称"
   )
   private String positionName;
   @Schema(
      description = "组织分类"
   )
   private String category;
   @Schema(
      description = "职位类型"
   )
   private String type;

   public String getOrgId() {
      return this.orgId;
   }

   public String getOrgName() {
      return this.orgName;
   }

   public String getPositionId() {
      return this.positionId;
   }

   public String getPositionName() {
      return this.positionName;
   }

   public String getCategory() {
      return this.category;
   }

   public String getType() {
      return this.type;
   }

   public void setOrgId(final String orgId) {
      this.orgId = orgId;
   }

   public void setOrgName(final String orgName) {
      this.orgName = orgName;
   }

   public void setPositionId(final String positionId) {
      this.positionId = positionId;
   }

   public void setPositionName(final String positionName) {
      this.positionName = positionName;
   }

   public void setCategory(final String category) {
      this.category = category;
   }

   public void setType(final String type) {
      this.type = type;
   }
}
