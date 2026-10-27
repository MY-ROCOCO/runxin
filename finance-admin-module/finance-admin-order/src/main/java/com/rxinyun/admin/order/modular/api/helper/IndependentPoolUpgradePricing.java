package com.rxinyun.admin.order.modular.api.helper;

import com.rxinyun.admin.order.modular.ecs.entity.IndependentOrderServerConfig;
import java.math.BigDecimal;

public class IndependentPoolUpgradePricing {
   private BigDecimal payPrice;
   private BigDecimal renewPrice;
   private BigDecimal totalPrice;
   private BigDecimal unitPrice;
   private Integer durationDays;
   private String unit;
   private Integer billingMode;
   private String originalOrderNo;
   private IndependentOrderServerConfig orderConfig;

   public BigDecimal getPayPrice() {
      return this.payPrice;
   }

   public BigDecimal getRenewPrice() {
      return this.renewPrice;
   }

   public BigDecimal getTotalPrice() {
      return this.totalPrice;
   }

   public BigDecimal getUnitPrice() {
      return this.unitPrice;
   }

   public Integer getDurationDays() {
      return this.durationDays;
   }

   public String getUnit() {
      return this.unit;
   }

   public Integer getBillingMode() {
      return this.billingMode;
   }

   public String getOriginalOrderNo() {
      return this.originalOrderNo;
   }

   public IndependentOrderServerConfig getOrderConfig() {
      return this.orderConfig;
   }

   public void setPayPrice(final BigDecimal payPrice) {
      this.payPrice = payPrice;
   }

   public void setRenewPrice(final BigDecimal renewPrice) {
      this.renewPrice = renewPrice;
   }

   public void setTotalPrice(final BigDecimal totalPrice) {
      this.totalPrice = totalPrice;
   }

   public void setUnitPrice(final BigDecimal unitPrice) {
      this.unitPrice = unitPrice;
   }

   public void setDurationDays(final Integer durationDays) {
      this.durationDays = durationDays;
   }

   public void setUnit(final String unit) {
      this.unit = unit;
   }

   public void setBillingMode(final Integer billingMode) {
      this.billingMode = billingMode;
   }

   public void setOriginalOrderNo(final String originalOrderNo) {
      this.originalOrderNo = originalOrderNo;
   }

   public void setOrderConfig(final IndependentOrderServerConfig orderConfig) {
      this.orderConfig = orderConfig;
   }
}
