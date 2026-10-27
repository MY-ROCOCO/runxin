package com.rxinyun.admin.production.modular.esc.cloud.param;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class ServerDiscountsEditParam implements Serializable {
   @Schema(
      description = "折扣ID，主键"
   )
   private String id;
   @Schema(
      description = "线路ID"
   )
   private String lineId;
   @Schema(
      description = "购买时长（月）"
   )
   private Integer durationMonths;
   @Schema(
      description = "折扣率（例如，0.95表示95折）"
   )
   private BigDecimal discountRate;
   @Schema(
      description = "适用产品类型"
   )
   private String productType;
   @Schema(
      description = "适用用户类型"
   )
   private String userType;
   @Schema(
      description = "折扣开始日期"
   )
   private Date startDate;
   @Schema(
      description = "折扣结束日期"
   )
   private Date endDate;
   @Schema(
      description = "折扣描述"
   )
   private String description;

   public String getId() {
      return this.id;
   }

   public String getLineId() {
      return this.lineId;
   }

   public Integer getDurationMonths() {
      return this.durationMonths;
   }

   public BigDecimal getDiscountRate() {
      return this.discountRate;
   }

   public String getProductType() {
      return this.productType;
   }

   public String getUserType() {
      return this.userType;
   }

   public Date getStartDate() {
      return this.startDate;
   }

   public Date getEndDate() {
      return this.endDate;
   }

   public String getDescription() {
      return this.description;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setLineId(final String lineId) {
      this.lineId = lineId;
   }

   public void setDurationMonths(final Integer durationMonths) {
      this.durationMonths = durationMonths;
   }

   public void setDiscountRate(final BigDecimal discountRate) {
      this.discountRate = discountRate;
   }

   public void setProductType(final String productType) {
      this.productType = productType;
   }

   public void setUserType(final String userType) {
      this.userType = userType;
   }

   public void setStartDate(final Date startDate) {
      this.startDate = startDate;
   }

   public void setEndDate(final Date endDate) {
      this.endDate = endDate;
   }

   public void setDescription(final String description) {
      this.description = description;
   }
}
