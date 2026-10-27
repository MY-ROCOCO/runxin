package com.rxinyun.admin.production.modular.vhost.line.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public class VhostLineAddParam {
   @Schema(
      description = "地域ID"
   )
   @NotBlank(
      message = "地域不能为空"
   )
   private String regionId;
   @Schema(
      description = "线路名称"
   )
   @NotBlank(
      message = "线路名称不能为空"
   )
   private String name;
   @Schema(
      description = "排序，默认0"
   )
   private Integer sortId;
   @Schema(
      description = "状态：1启用 0禁用，默认1"
   )
   private Integer status;
   @Schema(
      description = "备注"
   )
   private String remark;

   public String getRegionId() {
      return this.regionId;
   }

   public String getName() {
      return this.name;
   }

   public Integer getSortId() {
      return this.sortId;
   }

   public Integer getStatus() {
      return this.status;
   }

   public String getRemark() {
      return this.remark;
   }

   public void setRegionId(final String regionId) {
      this.regionId = regionId;
   }

   public void setName(final String name) {
      this.name = name;
   }

   public void setSortId(final Integer sortId) {
      this.sortId = sortId;
   }

   public void setStatus(final Integer status) {
      this.status = status;
   }

   public void setRemark(final String remark) {
      this.remark = remark;
   }
}
