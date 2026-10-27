package com.rxinyun.admin.production.modular.esc.region.param;

import io.swagger.v3.oas.annotations.media.Schema;

public class ServerRegionAddParam {
   @Schema(
      description = "ID"
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
      description = "状态：1 显示, 0 隐藏"
   )
   private Integer status;
   @Schema(
      description = "资源类型: 0 自生产 1 资源池"
   )
   private Integer type;
   @Schema(
      description = "资源池ID（当地域来自资源池时，关联resource_pool.id）"
   )
   private String resourcePoolId;

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

   public Integer getType() {
      return this.type;
   }

   public String getResourcePoolId() {
      return this.resourcePoolId;
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

   public void setType(final Integer type) {
      this.type = type;
   }

   public void setResourcePoolId(final String resourcePoolId) {
      this.resourcePoolId = resourcePoolId;
   }
}
