package com.rxinyun.admin.production.modular.independent.cloud.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.rxinyun.common.pojo.CommonEntity;
import io.swagger.v3.oas.annotations.media.Schema;

@TableName(
   value = "INDEPENDENT_LINE",
   autoResultMap = true
)
public class IndependentLine extends CommonEntity {
   @Schema(
      description = "线路ID"
   )
   private String id;
   @Schema(
      description = "地域ID"
   )
   private String regionId;
   @Schema(
      description = "线路名称"
   )
   private String name;
   @Schema(
      description = "最大保留年限"
   )
   private Integer yearNumber;
   @Schema(
      description = "线路备注"
   )
   private String remark;
   @Schema(
      description = "机房IP"
   )
   private String stashIp;
   @Schema(
      description = "线路状态"
   )
   private Integer status;
   @Schema(
      description = "权重（优先级 无传入 默认为0）"
   )
   private Integer weight;
   @Schema(
      description = "折扣模板ID"
   )
   private String discountTemplateId;
   @Schema(
      description = "资源类型: 0 自生产 1 资源池"
   )
   @TableField("TYPE")
   private Integer type;
   @Schema(
      description = "资源池ID（当线路来自资源池时，关联resource_pool.id）"
   )
   @TableField("RESOURCE_POOL_ID")
   private String resourcePoolId;

   public String getId() {
      return this.id;
   }

   public String getRegionId() {
      return this.regionId;
   }

   public String getName() {
      return this.name;
   }

   public Integer getYearNumber() {
      return this.yearNumber;
   }

   public String getRemark() {
      return this.remark;
   }

   public String getStashIp() {
      return this.stashIp;
   }

   public Integer getStatus() {
      return this.status;
   }

   public Integer getWeight() {
      return this.weight;
   }

   public String getDiscountTemplateId() {
      return this.discountTemplateId;
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

   public void setRegionId(final String regionId) {
      this.regionId = regionId;
   }

   public void setName(final String name) {
      this.name = name;
   }

   public void setYearNumber(final Integer yearNumber) {
      this.yearNumber = yearNumber;
   }

   public void setRemark(final String remark) {
      this.remark = remark;
   }

   public void setStashIp(final String stashIp) {
      this.stashIp = stashIp;
   }

   public void setStatus(final Integer status) {
      this.status = status;
   }

   public void setWeight(final Integer weight) {
      this.weight = weight;
   }

   public void setDiscountTemplateId(final String discountTemplateId) {
      this.discountTemplateId = discountTemplateId;
   }

   public void setType(final Integer type) {
      this.type = type;
   }

   public void setResourcePoolId(final String resourcePoolId) {
      this.resourcePoolId = resourcePoolId;
   }
}
