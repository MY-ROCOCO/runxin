package com.rxinyun.member.production.modular.independent.region.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class RegionDTO {
   @Schema(
      description = "地域ID"
   )
   private String id;
   @NotBlank(
      message = "地域名称不能为空"
   )
   @Schema(
      description = "地域名称"
   )
   private String name;
   @Schema(
      description = "地域描述"
   )
   private String description;
   @Schema(
      description = "地域发布状态,0为未发布,1为发布"
   )
   private Integer status;
   @NotNull(
      message = "地域排序字段不允许为空"
   )
   @Schema(
      description = "地域排序字段"
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
