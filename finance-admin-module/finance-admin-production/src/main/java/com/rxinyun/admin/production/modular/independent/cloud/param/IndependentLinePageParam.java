package com.rxinyun.admin.production.modular.independent.cloud.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public class IndependentLinePageParam {
   @Schema(
      description = "权重"
   )
   private Integer weight;
   @Schema(
      description = "当前页码"
   )
   private Integer current;
   @Schema(
      description = "每页条数"
   )
   private Integer size;
   @Schema(
      description = "排序字段，字段驼峰名称，如：userName"
   )
   private String sortField;
   @Schema(
      description = "排序方式，升序：ASCEND；降序：DESCEND"
   )
   private String sortOrder;
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
      description = "机房IP "
   )
   private String stashIp;
   @NotBlank
   @Schema(
      description = "线路状态  （可作为分页条件查询 0不可见，1可见）"
   )
   private Integer status;

   public Integer getWeight() {
      return this.weight;
   }

   public Integer getCurrent() {
      return this.current;
   }

   public Integer getSize() {
      return this.size;
   }

   public String getSortField() {
      return this.sortField;
   }

   public String getSortOrder() {
      return this.sortOrder;
   }

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

   public void setWeight(final Integer weight) {
      this.weight = weight;
   }

   public void setCurrent(final Integer current) {
      this.current = current;
   }

   public void setSize(final Integer size) {
      this.size = size;
   }

   public void setSortField(final String sortField) {
      this.sortField = sortField;
   }

   public void setSortOrder(final String sortOrder) {
      this.sortOrder = sortOrder;
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
}
