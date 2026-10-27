package com.rxinyun.marketing.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.math.BigDecimal;

public class CouponCalculateResponse implements Serializable {
   @Schema(
      description = "会员券ID"
   )
   private String memberCouponId;
   @Schema(
      description = "优惠券模板ID"
   )
   private String templateId;
   @Schema(
      description = "优惠券编码"
   )
   private String couponCode;
   @Schema(
      description = "本次优惠金额"
   )
   private BigDecimal couponPrice;
   @Schema(
      description = "优惠后应付金额"
   )
   private BigDecimal payPrice;
   @Schema(
      description = "本次使用次数"
   )
   private Integer useCount;
   @Schema(
      description = "剩余可用次数"
   )
   private Integer remainingUseCount;
   @Schema(
      description = "优惠券名称"
   )
   private String couponName;

   public String getMemberCouponId() {
      return this.memberCouponId;
   }

   public String getTemplateId() {
      return this.templateId;
   }

   public String getCouponCode() {
      return this.couponCode;
   }

   public BigDecimal getCouponPrice() {
      return this.couponPrice;
   }

   public BigDecimal getPayPrice() {
      return this.payPrice;
   }

   public Integer getUseCount() {
      return this.useCount;
   }

   public Integer getRemainingUseCount() {
      return this.remainingUseCount;
   }

   public String getCouponName() {
      return this.couponName;
   }

   public void setMemberCouponId(final String memberCouponId) {
      this.memberCouponId = memberCouponId;
   }

   public void setTemplateId(final String templateId) {
      this.templateId = templateId;
   }

   public void setCouponCode(final String couponCode) {
      this.couponCode = couponCode;
   }

   public void setCouponPrice(final BigDecimal couponPrice) {
      this.couponPrice = couponPrice;
   }

   public void setPayPrice(final BigDecimal payPrice) {
      this.payPrice = payPrice;
   }

   public void setUseCount(final Integer useCount) {
      this.useCount = useCount;
   }

   public void setRemainingUseCount(final Integer remainingUseCount) {
      this.remainingUseCount = remainingUseCount;
   }

   public void setCouponName(final String couponName) {
      this.couponName = couponName;
   }
}
