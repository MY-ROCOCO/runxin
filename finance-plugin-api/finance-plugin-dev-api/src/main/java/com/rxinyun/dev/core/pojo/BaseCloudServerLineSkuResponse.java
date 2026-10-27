package com.rxinyun.dev.core.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.math.BigDecimal;

@JsonInclude(Include.ALWAYS)
public class BaseCloudServerLineSkuResponse implements Serializable {
   @Schema(
      description = "SKU ID"
   )
   private String id;
   @Schema(
      description = "线路ID"
   )
   private String lineId;
   @Schema(
      description = "型号名称"
   )
   private String name;
   @Schema(
      description = "CPU核心数"
   )
   private Integer cpu;
   @Schema(
      description = "内存大小(单位: GB)"
   )
   private Integer ram;
   @Schema(
      description = "核心配置的价格/天"
   )
   private BigDecimal coreDayPrice;
   @Schema(
      description = "核心配置的价格/月"
   )
   private BigDecimal coreMonthPrice;
   @Schema(
      description = "状态"
   )
   private Integer status;
   @Schema(
      description = "备注"
   )
   private String remark;
   @Schema(
      description = "推荐状态"
   )
   private Integer recommendStatus;
   @Schema(
      description = "推荐内容"
   )
   private String recommendContent;
   @Schema(
      description = "排序"
   )
   private Integer sortId;
   @Schema(
      description = "资源类型: 0 自生产 1 资源池"
   )
   private Integer type;
   @Schema(
      description = "资源池ID（当SKU来自资源池时，关联resource_pool.id）"
   )
   private String resourcePoolId;

   public String getId() {
      return this.id;
   }

   public String getLineId() {
      return this.lineId;
   }

   public String getName() {
      return this.name;
   }

   public Integer getCpu() {
      return this.cpu;
   }

   public Integer getRam() {
      return this.ram;
   }

   public BigDecimal getCoreDayPrice() {
      return this.coreDayPrice;
   }

   public BigDecimal getCoreMonthPrice() {
      return this.coreMonthPrice;
   }

   public Integer getStatus() {
      return this.status;
   }

   public String getRemark() {
      return this.remark;
   }

   public Integer getRecommendStatus() {
      return this.recommendStatus;
   }

   public String getRecommendContent() {
      return this.recommendContent;
   }

   public Integer getSortId() {
      return this.sortId;
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

   public void setLineId(final String lineId) {
      this.lineId = lineId;
   }

   public void setName(final String name) {
      this.name = name;
   }

   public void setCpu(final Integer cpu) {
      this.cpu = cpu;
   }

   public void setRam(final Integer ram) {
      this.ram = ram;
   }

   public void setCoreDayPrice(final BigDecimal coreDayPrice) {
      this.coreDayPrice = coreDayPrice;
   }

   public void setCoreMonthPrice(final BigDecimal coreMonthPrice) {
      this.coreMonthPrice = coreMonthPrice;
   }

   public void setStatus(final Integer status) {
      this.status = status;
   }

   public void setRemark(final String remark) {
      this.remark = remark;
   }

   public void setRecommendStatus(final Integer recommendStatus) {
      this.recommendStatus = recommendStatus;
   }

   public void setRecommendContent(final String recommendContent) {
      this.recommendContent = recommendContent;
   }

   public void setSortId(final Integer sortId) {
      this.sortId = sortId;
   }

   public void setType(final Integer type) {
      this.type = type;
   }

   public void setResourcePoolId(final String resourcePoolId) {
      this.resourcePoolId = resourcePoolId;
   }
}
