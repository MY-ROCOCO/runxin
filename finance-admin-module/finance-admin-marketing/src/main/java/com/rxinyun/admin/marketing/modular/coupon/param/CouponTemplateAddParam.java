package com.rxinyun.admin.marketing.modular.coupon.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

@Schema(
   description = "优惠券模板新增参数"
)
public class CouponTemplateAddParam {
   @NotBlank(
      message = "优惠券名称不能为空"
   )
   @Schema(
      description = "优惠券名称",
      requiredMode = RequiredMode.REQUIRED,
      example = "新人首单立减券"
   )
   private String couponName;
   @Schema(
      description = "优惠券描述",
      example = "新会员首单可用，满100减20"
   )
   private String couponDescription;
   @NotNull(
      message = "优惠券类型不能为空"
   )
   @Schema(
      description = "优惠券类型：1固定金额券 2折扣券 3满减券",
      requiredMode = RequiredMode.REQUIRED,
      example = "3"
   )
   private Integer couponType;
   @Schema(
      description = "使用门槛金额",
      example = "100"
   )
   private BigDecimal thresholdAmount;
   @Schema(
      description = "固定减免金额，固定金额券/满减券时使用",
      example = "20"
   )
   private BigDecimal reduceAmount;
   @Schema(
      description = "折扣率，折扣券时使用，例如 0.9 表示 9 折",
      example = "0.9"
   )
   private BigDecimal discountRate;
   @Schema(
      description = "封顶优惠金额，折扣券可选",
      example = "200"
   )
   private BigDecimal maxDiscountAmount;
   @Schema(
      description = "适用产品类型，逗号分隔：1云服务器 2独立服务器 3域名 4DNS云解析 5虚拟主机",
      example = "1,5"
   )
   private String applyProductTypes;
   @Schema(
      description = "适用云服务器线路ID，逗号分隔",
      example = "1001,1002"
   )
   private String applyCloudLineIds;
   @Schema(
      description = "适用独立服务器线路ID，逗号分隔",
      example = "2001,2002"
   )
   private String applyIndependentLineIds;
   @Schema(
      description = "适用订单类型，逗号分隔：1新购 2升级 3续费",
      example = "1,2,3"
   )
   private String applyOrderTypes;
   @NotNull(
      message = "状态不能为空"
   )
   @Schema(
      description = "状态：0禁用 1启用",
      requiredMode = RequiredMode.REQUIRED,
      example = "1"
   )
   private Integer status;
   @Schema(
      description = "是否允许前台领取：0否 1是",
      example = "1"
   )
   private Integer claimEnabled;
   @Min(
      value = 0L,
      message = "计划发放总量不能小于0"
   )
   @Schema(
      description = "计划发放总量，0 表示不限制",
      example = "1000"
   )
   private Integer issueTotalCount;
   @NotNull(
      message = "单张券可使用次数不能为空"
   )
   @Min(
      value = 1L,
      message = "单张券可使用次数必须大于0"
   )
   @Schema(
      description = "单张券可使用次数",
      requiredMode = RequiredMode.REQUIRED,
      example = "1"
   )
   private Integer useLimit;
   @Schema(
      description = "开始时间",
      example = "2026-04-08T00:00:00"
   )
   private Date startTime;
   @Schema(
      description = "结束时间",
      example = "2026-04-30T23:59:59"
   )
   private Date endTime;
   @Schema(
      description = "排序码",
      example = "100"
   )
   private Integer sortCode;
   @Schema(
      description = "扩展字段，JSON 字符串",
      example = "{\"remark\":\"仅活动期可用\"}"
   )
   private String extJson;

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
