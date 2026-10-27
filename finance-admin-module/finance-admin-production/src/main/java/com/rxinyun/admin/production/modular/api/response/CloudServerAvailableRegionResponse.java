package com.rxinyun.admin.production.modular.api.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
   description = "云服务器可用区信息"
)
public class CloudServerAvailableRegionResponse {
   @Schema(
      description = "可用区ID"
   )
   private String id;
   @Schema(
      description = "地域ID"
   )
   private String regionId;
   @Schema(
      description = "可用区名称"
   )
   private String availableName;
   @Schema(
      description = "备注"
   )
   private String remark;
   @Schema(
      description = "状态"
   )
   private Integer status;
   @Schema(
      description = "排序"
   )
   private Integer sortId;

   public String getId() {
      return this.id;
   }

   public String getRegionId() {
      return this.regionId;
   }

   public String getAvailableName() {
      return this.availableName;
   }

   public String getRemark() {
      return this.remark;
   }

   public Integer getStatus() {
      return this.status;
   }

   public Integer getSortId() {
      return this.sortId;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setRegionId(final String regionId) {
      this.regionId = regionId;
   }

   public void setAvailableName(final String availableName) {
      this.availableName = availableName;
   }

   public void setRemark(final String remark) {
      this.remark = remark;
   }

   public void setStatus(final Integer status) {
      this.status = status;
   }

   public void setSortId(final Integer sortId) {
      this.sortId = sortId;
   }
}
