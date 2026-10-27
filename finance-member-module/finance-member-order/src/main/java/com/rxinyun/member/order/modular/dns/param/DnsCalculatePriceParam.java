package com.rxinyun.member.order.modular.dns.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class DnsCalculatePriceParam {
   @NotBlank(
      message = "套餐ID不能为空"
   )
   @Schema(
      description = "DNS套餐ID",
      requiredMode = RequiredMode.REQUIRED
   )
   private String packageId;
   @NotNull(
      message = "域名配额不能为空"
   )
   @Min(
      value = 1L,
      message = "域名配额必须大于0"
   )
   @Schema(
      description = "购买的域名配额",
      requiredMode = RequiredMode.REQUIRED
   )
   private Integer domainQuota;
   @NotNull(
      message = "周期月数不能为空"
   )
   @Min(
      value = 1L,
      message = "周期月数必须大于0"
   )
   @Schema(
      description = "购买周期月数",
      requiredMode = RequiredMode.REQUIRED
   )
   private Integer periodMonths;
   @Schema(
      description = "优惠券编码"
   )
   private String couponCode;

   public String getPackageId() {
      return this.packageId;
   }

   public Integer getDomainQuota() {
      return this.domainQuota;
   }

   public Integer getPeriodMonths() {
      return this.periodMonths;
   }

   public String getCouponCode() {
      return this.couponCode;
   }

   public void setPackageId(final String packageId) {
      this.packageId = packageId;
   }

   public void setDomainQuota(final Integer domainQuota) {
      this.domainQuota = domainQuota;
   }

   public void setPeriodMonths(final Integer periodMonths) {
      this.periodMonths = periodMonths;
   }

   public void setCouponCode(final String couponCode) {
      this.couponCode = couponCode;
   }
}
