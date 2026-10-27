package com.rxinyun.member.order.modular.dns.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.Date;

public class DnsCalculatePriceResponse {
   @Schema(
      description = "DNS套餐实例ID"
   )
   private String instanceId;
   @Schema(
      description = "DNS套餐ID"
   )
   private String packageId;
   @Schema(
      description = "DNS套餐名称"
   )
   private String packageName;
   @Schema(
      description = "线路分组ID"
   )
   private String lineGroupId;
   @Schema(
      description = "单域名月价格"
   )
   private BigDecimal singleDomainPrice;
   @Schema(
      description = "最低TTL"
   )
   private Integer minTtl;
   @Schema(
      description = "订单完成后的域名总配额"
   )
   private Integer domainQuota;
   @Schema(
      description = "新增域名配额"
   )
   private Integer upgradeDomainQuota;
   @Schema(
      description = "周期月数"
   )
   private Integer periodMonths;
   @Schema(
      description = "升级时剩余天数"
   )
   private Integer remainingDays;
   @Schema(
      description = "实例到期时间"
   )
   private Date nextDueTime;
   @Schema(
      description = "订单原价"
   )
   private BigDecimal totalPrice;
   @Schema(
      description = "优惠券抵扣金额"
   )
   private BigDecimal couponPrice;
   @Schema(
      description = "订单实付金额"
   )
   private BigDecimal payPrice;

   public String getInstanceId() {
      return this.instanceId;
   }

   public String getPackageId() {
      return this.packageId;
   }

   public String getPackageName() {
      return this.packageName;
   }

   public String getLineGroupId() {
      return this.lineGroupId;
   }

   public BigDecimal getSingleDomainPrice() {
      return this.singleDomainPrice;
   }

   public Integer getMinTtl() {
      return this.minTtl;
   }

   public Integer getDomainQuota() {
      return this.domainQuota;
   }

   public Integer getUpgradeDomainQuota() {
      return this.upgradeDomainQuota;
   }

   public Integer getPeriodMonths() {
      return this.periodMonths;
   }

   public Integer getRemainingDays() {
      return this.remainingDays;
   }

   public Date getNextDueTime() {
      return this.nextDueTime;
   }

   public BigDecimal getTotalPrice() {
      return this.totalPrice;
   }

   public BigDecimal getCouponPrice() {
      return this.couponPrice;
   }

   public BigDecimal getPayPrice() {
      return this.payPrice;
   }

   public void setInstanceId(final String instanceId) {
      this.instanceId = instanceId;
   }

   public void setPackageId(final String packageId) {
      this.packageId = packageId;
   }

   public void setPackageName(final String packageName) {
      this.packageName = packageName;
   }

   public void setLineGroupId(final String lineGroupId) {
      this.lineGroupId = lineGroupId;
   }

   public void setSingleDomainPrice(final BigDecimal singleDomainPrice) {
      this.singleDomainPrice = singleDomainPrice;
   }

   public void setMinTtl(final Integer minTtl) {
      this.minTtl = minTtl;
   }

   public void setDomainQuota(final Integer domainQuota) {
      this.domainQuota = domainQuota;
   }

   public void setUpgradeDomainQuota(final Integer upgradeDomainQuota) {
      this.upgradeDomainQuota = upgradeDomainQuota;
   }

   public void setPeriodMonths(final Integer periodMonths) {
      this.periodMonths = periodMonths;
   }

   public void setRemainingDays(final Integer remainingDays) {
      this.remainingDays = remainingDays;
   }

   public void setNextDueTime(final Date nextDueTime) {
      this.nextDueTime = nextDueTime;
   }

   public void setTotalPrice(final BigDecimal totalPrice) {
      this.totalPrice = totalPrice;
   }

   public void setCouponPrice(final BigDecimal couponPrice) {
      this.couponPrice = couponPrice;
   }

   public void setPayPrice(final BigDecimal payPrice) {
      this.payPrice = payPrice;
   }
}
