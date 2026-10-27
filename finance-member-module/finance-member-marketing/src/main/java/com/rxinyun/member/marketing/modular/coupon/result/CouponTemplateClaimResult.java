package com.rxinyun.member.marketing.modular.coupon.result;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.Date;

@Schema(
   description = "会员端可领取优惠券模板结果"
)
public class CouponTemplateClaimResult {
   @Schema(
      description = "模板ID"
   )
   private String templateId;
   @Schema(
      description = "优惠券名称"
   )
   private String couponName;
   @Schema(
      description = "优惠券描述"
   )
   private String couponDescription;
   @Schema(
      description = "优惠券类型"
   )
   private Integer couponType;
   @Schema(
      description = "使用门槛金额"
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
      description = "适用产品类型"
   )
   private String applyProductTypes;
   @Schema(
      description = "适用云服务器线路ID，逗号分隔"
   )
   private String applyCloudLineIds;
   @Schema(
      description = "适用云服务器线路名称，逗号分隔"
   )
   private String applyCloudLineNames;
   @Schema(
      description = "适用独立服务器线路ID，逗号分隔"
   )
   private String applyIndependentLineIds;
   @Schema(
      description = "适用独立服务器线路名称，逗号分隔"
   )
   private String applyIndependentLineNames;
   @Schema(
      description = "适用订单类型，逗号分隔"
   )
   private String applyOrderTypes;
   @Schema(
      description = "开始时间"
   )
   private Date startTime;
   @Schema(
      description = "结束时间"
   )
   private Date endTime;
   @Schema(
      description = "剩余可领取数量，-1表示不限量"
   )
   private Integer remainingClaimCount;
   @Schema(
      description = "当前会员是否已领取"
   )
   private Boolean claimed;
   @Schema(
      description = "排序码"
   )
   private Integer sortCode;

   public String getTemplateId() {
      return this.templateId;
   }

   public String getCouponName() {
      return this.couponName;
   }

   public String getCouponDescription() {
      return this.couponDescription;
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

   public String getApplyProductTypes() {
      return this.applyProductTypes;
   }

   public String getApplyCloudLineIds() {
      return this.applyCloudLineIds;
   }

   public String getApplyCloudLineNames() {
      return this.applyCloudLineNames;
   }

   public String getApplyIndependentLineIds() {
      return this.applyIndependentLineIds;
   }

   public String getApplyIndependentLineNames() {
      return this.applyIndependentLineNames;
   }

   public String getApplyOrderTypes() {
      return this.applyOrderTypes;
   }

   public Date getStartTime() {
      return this.startTime;
   }

   public Date getEndTime() {
      return this.endTime;
   }

   public Integer getRemainingClaimCount() {
      return this.remainingClaimCount;
   }

   public Boolean getClaimed() {
      return this.claimed;
   }

   public Integer getSortCode() {
      return this.sortCode;
   }

   public void setTemplateId(final String templateId) {
      this.templateId = templateId;
   }

   public void setCouponName(final String couponName) {
      this.couponName = couponName;
   }

   public void setCouponDescription(final String couponDescription) {
      this.couponDescription = couponDescription;
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

   public void setApplyProductTypes(final String applyProductTypes) {
      this.applyProductTypes = applyProductTypes;
   }

   public void setApplyCloudLineIds(final String applyCloudLineIds) {
      this.applyCloudLineIds = applyCloudLineIds;
   }

   public void setApplyCloudLineNames(final String applyCloudLineNames) {
      this.applyCloudLineNames = applyCloudLineNames;
   }

   public void setApplyIndependentLineIds(final String applyIndependentLineIds) {
      this.applyIndependentLineIds = applyIndependentLineIds;
   }

   public void setApplyIndependentLineNames(final String applyIndependentLineNames) {
      this.applyIndependentLineNames = applyIndependentLineNames;
   }

   public void setApplyOrderTypes(final String applyOrderTypes) {
      this.applyOrderTypes = applyOrderTypes;
   }

   public void setStartTime(final Date startTime) {
      this.startTime = startTime;
   }

   public void setEndTime(final Date endTime) {
      this.endTime = endTime;
   }

   public void setRemainingClaimCount(final Integer remainingClaimCount) {
      this.remainingClaimCount = remainingClaimCount;
   }

   public void setClaimed(final Boolean claimed) {
      this.claimed = claimed;
   }

   public void setSortCode(final Integer sortCode) {
      this.sortCode = sortCode;
   }
}
