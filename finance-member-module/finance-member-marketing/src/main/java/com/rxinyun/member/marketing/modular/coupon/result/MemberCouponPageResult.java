package com.rxinyun.member.marketing.modular.coupon.result;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.Date;

@Schema(
   description = "会员优惠券分页结果"
)
public class MemberCouponPageResult {
   @Schema(
      description = "会员优惠券ID"
   )
   private String id;
   @Schema(
      description = "优惠券模板ID"
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
      description = "优惠券编码"
   )
   private String couponCode;
   @Schema(
      description = "优惠券类型：1固定金额券 2折扣券 3满减券"
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
      description = "适用产品类型，逗号分隔"
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
      description = "优惠券状态：0未使用 1已锁定 2已用尽 3已过期 4已禁用"
   )
   private Integer status;
   @Schema(
      description = "已使用次数"
   )
   private Integer usedCount;
   @Schema(
      description = "剩余可用次数"
   )
   private Integer remainingUseCount;
   @Schema(
      description = "锁定订单号"
   )
   private String lockOrderNo;
   @Schema(
      description = "生效时间"
   )
   private Date effectiveTime;
   @Schema(
      description = "过期时间"
   )
   private Date expireTime;
   @Schema(
      description = "最近使用时间"
   )
   private Date lastUseTime;
   @Schema(
      description = "创建时间"
   )
   private Date createTime;

   public String getId() {
      return this.id;
   }

   public String getTemplateId() {
      return this.templateId;
   }

   public String getCouponName() {
      return this.couponName;
   }

   public String getCouponDescription() {
      return this.couponDescription;
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

   public Integer getStatus() {
      return this.status;
   }

   public Integer getUsedCount() {
      return this.usedCount;
   }

   public Integer getRemainingUseCount() {
      return this.remainingUseCount;
   }

   public String getLockOrderNo() {
      return this.lockOrderNo;
   }

   public Date getEffectiveTime() {
      return this.effectiveTime;
   }

   public Date getExpireTime() {
      return this.expireTime;
   }

   public Date getLastUseTime() {
      return this.lastUseTime;
   }

   public Date getCreateTime() {
      return this.createTime;
   }

   public void setId(final String id) {
      this.id = id;
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

   public void setStatus(final Integer status) {
      this.status = status;
   }

   public void setUsedCount(final Integer usedCount) {
      this.usedCount = usedCount;
   }

   public void setRemainingUseCount(final Integer remainingUseCount) {
      this.remainingUseCount = remainingUseCount;
   }

   public void setLockOrderNo(final String lockOrderNo) {
      this.lockOrderNo = lockOrderNo;
   }

   public void setEffectiveTime(final Date effectiveTime) {
      this.effectiveTime = effectiveTime;
   }

   public void setExpireTime(final Date expireTime) {
      this.expireTime = expireTime;
   }

   public void setLastUseTime(final Date lastUseTime) {
      this.lastUseTime = lastUseTime;
   }

   public void setCreateTime(final Date createTime) {
      this.createTime = createTime;
   }
}
