package com.rxinyun.member.order.modular.dns.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class DnsUpgradeQuotaCalculatePriceParam {
   @NotBlank(
      message = "套餐实例ID不能为空"
   )
   @Schema(
      description = "DNS套餐实例ID",
      requiredMode = RequiredMode.REQUIRED
   )
   private String instanceId;
   @NotNull(
      message = "升级域名数量不能为空"
   )
   @Min(
      value = 1L,
      message = "升级域名数量必须大于0"
   )
   @Schema(
      description = "新增域名配额数量",
      requiredMode = RequiredMode.REQUIRED
   )
   private Integer upgradeDomainQuota;
   @Schema(
      description = "优惠券编码"
   )
   private String couponCode;

   public String getInstanceId() {
      return this.instanceId;
   }

   public Integer getUpgradeDomainQuota() {
      return this.upgradeDomainQuota;
   }

   public String getCouponCode() {
      return this.couponCode;
   }

   public void setInstanceId(final String instanceId) {
      this.instanceId = instanceId;
   }

   public void setUpgradeDomainQuota(final Integer upgradeDomainQuota) {
      this.upgradeDomainQuota = upgradeDomainQuota;
   }

   public void setCouponCode(final String couponCode) {
      this.couponCode = couponCode;
   }
}
