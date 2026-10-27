package com.rxinyun.admin.production.modular.api.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
   description = "云服务器地域信息"
)
public class CloudServerRegionResponse {
   @Schema(
      description = "地域ID"
   )
   private String id;
   @Schema(
      description = "地域名称"
   )
   private String regionName;
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

   public String getRegionName() {
      return this.regionName;
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

   public void setRegionName(final String regionName) {
      this.regionName = regionName;
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
