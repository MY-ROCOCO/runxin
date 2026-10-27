package com.rxinyun.admin.production.modular.esc.region.param;

import io.swagger.v3.oas.annotations.media.Schema;

public class ServerRegionEditParam {
   @Schema(
      description = "地域标识"
   )
   private String id;
   @Schema(
      description = "地域名称"
   )
   private String regionName;
   @Schema(
      description = "排序"
   )
   private Integer sortId;
   @Schema(
      description = "备注"
   )
   private String remark;
   @Schema(
      description = "状态"
   )
   private Integer status;

   public String getId() {
      return this.id;
   }

   public String getRegionName() {
      return this.regionName;
   }

   public Integer getSortId() {
      return this.sortId;
   }

   public String getRemark() {
      return this.remark;
   }

   public Integer getStatus() {
      return this.status;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setRegionName(final String regionName) {
      this.regionName = regionName;
   }

   public void setSortId(final Integer sortId) {
      this.sortId = sortId;
   }

   public void setRemark(final String remark) {
      this.remark = remark;
   }

   public void setStatus(final Integer status) {
      this.status = status;
   }
}
