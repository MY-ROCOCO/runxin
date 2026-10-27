package com.rxinyun.member.production.modular.independent.region.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;

@TableName(
   value = "independent_region",
   autoResultMap = true
)
public class Region {
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
      description = "地域发布状态,0为未发布,1为发布"
   )
   private Integer status;
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
