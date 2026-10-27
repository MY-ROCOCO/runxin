package com.rxinyun.admin.production.modular.independent.cloud.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public class IndependentLineEditParam {
   @Schema(
      description = "线路ID"
   )
   private String id;
   @Schema(
      description = "地域ID"
   )
   private String regionId;
   @NotBlank
   @Schema(
      description = "线路名称"
   )
   private String name;
   @NotBlank
   @Schema(
      description = "最大保留年限 大于0"
   )
   private Integer yearNumber;
   @NotBlank
   @Schema(
      description = "线路备注"
   )
   private String remark;
   @NotBlank
   @Schema(
      description = "机房IP"
   )
   private String stashIp;
   @NotBlank
   @Schema(
      description = "线路状态 0不可见  1可见"
   )
   private Integer status;
   @Schema(
      description = "权重"
   )
   private Integer weight;
   @Schema(
      description = "折扣模板ID"
   )
   private String discountTemplateId;

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
}
