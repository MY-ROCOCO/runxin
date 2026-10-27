package com.rxinyun.admin.marketing.modular.coupon.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.rxinyun.common.pojo.CommonEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.Date;

@TableName(
   value = "COUPON_TEMPLATE",
   autoResultMap = true
)
public class CouponTemplate extends CommonEntity {
   @Schema(
      description = "主键"
   )
   private String id;
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
      description = "适用独立服务器线路ID，逗号分隔"
   )
   private String applyIndependentLineIds;
   @Schema(
      description = "适用订单类型，逗号分隔"
   )
   private String applyOrderTypes;
   @Schema(
      description = "状态"
   )
   private Integer status;
   @Schema(
      description = "是否允许前台领取：0否 1是"
   )
   private Integer claimEnabled;
   @Schema(
      description = "计划发放总量"
   )
   private Integer issueTotalCount;
   @Schema(
      description = "已发放数量"
   )
   private Integer issuedCount;
   @Schema(
      description = "单张券可使用次数"
   )
   private Integer useLimit;
   @Schema(
      description = "开始时间"
   )
   private Date startTime;
   @Schema(
      description = "结束时间"
   )
   private Date endTime;
   @Schema(
      description = "排序码"
   )
   private Integer sortCode;
   @Schema(
      description = "扩展字段"
   )
   private String extJson;

   public String getId() {
      return this.id;
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

   public String getApplyIndependentLineIds() {
      return this.applyIndependentLineIds;
   }

   public String getApplyOrderTypes() {
      return this.applyOrderTypes;
   }

   public Integer getStatus() {
      return this.status;
   }

   public Integer getClaimEnabled() {
      return this.claimEnabled;
   }

   public Integer getIssueTotalCount() {
      return this.issueTotalCount;
   }

   public Integer getIssuedCount() {
      return this.issuedCount;
   }

   public Integer getUseLimit() {
      return this.useLimit;
   }

   public Date getStartTime() {
      return this.startTime;
   }

   public Date getEndTime() {
      return this.endTime;
   }

   public Integer getSortCode() {
      return this.sortCode;
   }

   public String getExtJson() {
      return this.extJson;
   }

   public void setId(final String id) {
      this.id = id;
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

   public void setApplyIndependentLineIds(final String applyIndependentLineIds) {
      this.applyIndependentLineIds = applyIndependentLineIds;
   }

   public void setApplyOrderTypes(final String applyOrderTypes) {
      this.applyOrderTypes = applyOrderTypes;
   }

   public void setStatus(final Integer status) {
      this.status = status;
   }

   public void setClaimEnabled(final Integer claimEnabled) {
      this.claimEnabled = claimEnabled;
   }

   public void setIssueTotalCount(final Integer issueTotalCount) {
      this.issueTotalCount = issueTotalCount;
   }

   public void setIssuedCount(final Integer issuedCount) {
      this.issuedCount = issuedCount;
   }

   public void setUseLimit(final Integer useLimit) {
      this.useLimit = useLimit;
   }

   public void setStartTime(final Date startTime) {
      this.startTime = startTime;
   }

   public void setEndTime(final Date endTime) {
      this.endTime = endTime;
   }

   public void setSortCode(final Integer sortCode) {
      this.sortCode = sortCode;
   }

   public void setExtJson(final String extJson) {
      this.extJson = extJson;
   }
}
