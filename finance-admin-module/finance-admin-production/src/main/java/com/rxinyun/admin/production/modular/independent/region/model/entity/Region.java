package com.rxinyun.admin.production.modular.independent.region.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;

@TableName(
   value = "INDEPENDENT_REGION",
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
   @Schema(
      description = "资源类型: 0 自生产 1 资源池"
   )
   @TableField("TYPE")
   private Integer type;
   @Schema(
      description = "资源池ID（当地域来自资源池时，关联resource_pool.id）"
   )
   @TableField("RESOURCE_POOL_ID")
   private String resourcePoolId;

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

   public Integer getType() {
      return this.type;
   }

   public String getResourcePoolId() {
      return this.resourcePoolId;
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

   public void setType(final Integer type) {
      this.type = type;
   }

   public void setResourcePoolId(final String resourcePoolId) {
      this.resourcePoolId = resourcePoolId;
   }
}
