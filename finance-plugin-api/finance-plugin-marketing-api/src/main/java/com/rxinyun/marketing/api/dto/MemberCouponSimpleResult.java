package com.rxinyun.marketing.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class MemberCouponSimpleResult implements Serializable {
   @Schema(
      description = "会员券ID"
   )
   private String memberCouponId;
   @Schema(
      description = "优惠券模板ID"
   )
   private String templateId;
   @Schema(
      description = "优惠券名称"
   )
   private String couponName;
   @Schema(
      description = "优惠券编码"
   )
   private String couponCode;
   @Schema(
      description = "优惠券类型"
   )
   private Integer couponType;
   @Schema(
      description = "门槛金额"
   )
   private BigDecimal thresholdAmount;
   @Schema(
      description = "固定减免金额"
   )
   private BigDecimal reduceAmount;
   @Schema(
      description = "折扣率"
   )
   private BigDecimal discountRate;
   @Schema(
      description = "封顶优惠金额"
   )
   private BigDecimal maxDiscountAmount;
   @Schema(
      description = "预计优惠金额"
   )
   private BigDecimal estimatedCouponPrice;
   @Schema(
      description = "剩余可用次数"
   )
   private Integer remainingUseCount;
   @Schema(
      description = "过期时间"
   )
   private Date endTime;

   public String getMemberCouponId() {
      return this.memberCouponId;
   }

   public String getTemplateId() {
      return this.templateId;
   }

   public String getCouponName() {
      return this.couponName;
   }

   public String getCouponCode() {
      return this.couponCode;
   }

   public Integer getCouponType() {
      return this.couponType;
   }

   public BigDecimal getThresholdAmount() {
      return this.thresholdAmount;
   }

   public BigDecimal getReduceAmount() {
      return this.reduceAmount;
   }

   public BigDecimal getDiscountRate() {
      return this.discountRate;
   }

   public BigDecimal getMaxDiscountAmount() {
      return this.maxDiscountAmount;
   }

   public BigDecimal getEstimatedCouponPrice() {
      return this.estimatedCouponPrice;
   }

   public Integer getRemainingUseCount() {
      return this.remainingUseCount;
   }

   public Date getEndTime() {
      return this.endTime;
   }

   public void setMemberCouponId(final String memberCouponId) {
      this.memberCouponId = memberCouponId;
   }

   public void setTemplateId(final String templateId) {
      this.templateId = templateId;
   }

   public void setCouponName(final String couponName) {
      this.couponName = couponName;
   }

   public void setCouponCode(final String couponCode) {
      this.couponCode = couponCode;
   }

   public void setCouponType(final Integer couponType) {
      this.couponType = couponType;
   }

   public void setThresholdAmount(final BigDecimal thresholdAmount) {
      this.thresholdAmount = thresholdAmount;
   }

   public void setReduceAmount(final BigDecimal reduceAmount) {
      this.reduceAmount = reduceAmount;
   }

   public void setDiscountRate(final BigDecimal discountRate) {
      this.discountRate = discountRate;
   }

   public void setMaxDiscountAmount(final BigDecimal maxDiscountAmount) {
      this.maxDiscountAmount = maxDiscountAmount;
   }

   public void setEstimatedCouponPrice(final BigDecimal estimatedCouponPrice) {
      this.estimatedCouponPrice = estimatedCouponPrice;
   }

   public void setRemainingUseCount(final Integer remainingUseCount) {
      this.remainingUseCount = remainingUseCount;
   }

   public void setEndTime(final Date endTime) {
      this.endTime = endTime;
   }
}
