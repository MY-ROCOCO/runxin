package com.rxinyun.admin.production.modular.api.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
   description = "独立服务器地域信息"
)
public class IndependentRegionResponse {
   @Schema(
      description = "地域ID"
   )
   private String id;
   @Schema(
      description = "地域名称"
   )
   private String name;
   @Schema(
      description = "地域描述"
   )
   private String description;
   @Schema(
      description = "状态"
   )
   private Integer status;
   @Schema(
      description = "排序"
   )
   private Integer sort;

   public String getId() {
      return this.id;
   }

   public String getName() {
      return this.name;
   }

   public String getDescription() {
      return this.description;
   }

   public Integer getStatus() {
      return this.status;
   }

   public Integer getSort() {
      return this.sort;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setName(final String name) {
      this.name = name;
   }

   public void setDescription(final String description) {
      this.description = description;
   }

   public void setStatus(final Integer status) {
      this.status = status;
   }

   public void setSort(final Integer sort) {
      this.sort = sort;
   }
}
