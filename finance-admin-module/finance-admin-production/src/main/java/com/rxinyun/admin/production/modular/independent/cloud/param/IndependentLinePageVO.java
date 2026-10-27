package com.rxinyun.admin.production.modular.independent.cloud.param;

import com.rxinyun.admin.production.modular.independent.cloud.entity.IndependentLine;
import io.swagger.v3.oas.annotations.media.Schema;

public class IndependentLinePageVO extends IndependentLine {
   @Schema(
      description = "地域名字"
   )
   private String regionName;
   @Schema(
      description = "折扣模板名称"
   )
   private String discountTemplateName;
   @Schema(
      description = "资源类型: 0 自生产 1 资源池"
   )
   private Integer type;
   @Schema(
      description = "资源池ID（当线路来自资源池时，关联resource_pool.id）"
   )
   private String resourcePoolId;
   @Schema(
      description = "资源池类型（当线路来自资源池时显示）"
   )
   private String resourcePoolType;
   @Schema(
      description = "资源池名称（当线路来自资源池时显示）"
   )
   private String resourcePoolName;

   public String getRegionName() {
      return this.regionName;
   }

   public String getDiscountTemplateName() {
      return this.discountTemplateName;
   }

   @Override
   public Integer getType() {
      return this.type;
   }

   @Override
   public String getResourcePoolId() {
      return this.resourcePoolId;
   }

   public String getResourcePoolType() {
      return this.resourcePoolType;
   }

   public String getResourcePoolName() {
      return this.resourcePoolName;
   }

   public void setRegionName(final String regionName) {
      this.regionName = regionName;
   }

   public void setDiscountTemplateName(final String discountTemplateName) {
      this.discountTemplateName = discountTemplateName;
   }

   @Override
   public void setType(final Integer type) {
      this.type = type;
   }

   @Override
   public void setResourcePoolId(final String resourcePoolId) {
      this.resourcePoolId = resourcePoolId;
   }

   public void setResourcePoolType(final String resourcePoolType) {
      this.resourcePoolType = resourcePoolType;
   }

   public void setResourcePoolName(final String resourcePoolName) {
      this.resourcePoolName = resourcePoolName;
   }
}
