package com.rxinyun.member.order.modular.independent.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class IndependentCalculatePriceResponse implements Serializable {
   @Schema(
      description = "计费方式：1：包年包月:2：按量计费 "
   )
   @NotNull(
      message = "计费方式不能为空"
   )
   private Integer billingMode;
   @Schema(
      description = "机型ID"
   )
   private String machineId;
   @Schema(
      description = "机型名称"
   )
   private String machineName;
   @Schema(
      description = "机型月单价"
   )
   private BigDecimal machineMonthPrice;
   @Schema(
      description = "带宽配置"
   )
   private Integer bandwidth;
   @Schema(
      description = "带宽赠送值"
   )
   private Integer bandwidthFreeSize;
   @Schema(
      description = "带宽配置月单价"
   )
   private BigDecimal bandwidthMonthPrice;
   @Schema(
      description = "防御配置"
   )
   private Integer defense;
   @Schema(
      description = "赠送防御"
   )
   private Integer defenseFreeSize;
   @Schema(
      description = "防御配置月单价"
   )
   private BigDecimal defenseMonthPrice;
   @Schema(
      description = "IP数量"
   )
   private Integer ipSize;
   @Schema(
      description = "赠送IP"
   )
   private Integer ipFreeSize;
   @Schema(
      description = "IP配置月单价"
   )
   private BigDecimal ipMonthPrice;
   @Schema(
      description = "单位 时 s，天 d，月 m，年 y"
   )
   @NotBlank(
      message = "单位不能为空"
   )
   private String unit;
   @Schema(
      description = "购买时长(月)"
   )
   private Integer duration;
   @Schema(
      description = "购买数量"
   )
   private Integer number;
   @Schema(
      description = "单价"
   )
   private BigDecimal unitPrice;
   @Schema(
      description = "总价"
   )
   private BigDecimal totalPrice;
   @Schema(
      description = "折扣价格"
   )
   private BigDecimal discountPrice;
   @Schema(
      description = "优惠价格"
   )
   private BigDecimal couponPrice;
   @Schema(
      description = "实付价格"
   )
   private BigDecimal payPrice;
   @Schema(
      description = "提示信息"
   )
   private String tips;
   @Schema(
      description = "原订单ID（续费或升级时关联）"
   )
   private String originalOrderNo;
   @Schema(
      description = "续费价格"
   )
   private BigDecimal renewPrice;
   @Schema(
      description = "续费明细（按台）"
   )
   private List<IndependentRenewItemResponse> items;

   public Integer getBillingMode() {
      return this.billingMode;
   }

   public String getMachineId() {
      return this.machineId;
   }

   public String getMachineName() {
      return this.machineName;
   }

   public BigDecimal getMachineMonthPrice() {
      return this.machineMonthPrice;
   }

   public Integer getBandwidth() {
      return this.bandwidth;
   }

   public Integer getBandwidthFreeSize() {
      return this.bandwidthFreeSize;
   }

   public BigDecimal getBandwidthMonthPrice() {
      return this.bandwidthMonthPrice;
   }

   public Integer getDefense() {
      return this.defense;
   }

   public Integer getDefenseFreeSize() {
      return this.defenseFreeSize;
   }

   public BigDecimal getDefenseMonthPrice() {
      return this.defenseMonthPrice;
   }

   public Integer getIpSize() {
      return this.ipSize;
   }

   public Integer getIpFreeSize() {
      return this.ipFreeSize;
   }

   public BigDecimal getIpMonthPrice() {
      return this.ipMonthPrice;
   }

   public String getUnit() {
      return this.unit;
   }

   public Integer getDuration() {
      return this.duration;
   }

   public Integer getNumber() {
      return this.number;
   }

   public BigDecimal getUnitPrice() {
      return this.unitPrice;
   }

   public BigDecimal getTotalPrice() {
      return this.totalPrice;
   }

   public BigDecimal getDiscountPrice() {
      return this.discountPrice;
   }

   public BigDecimal getCouponPrice() {
      return this.couponPrice;
   }

   public BigDecimal getPayPrice() {
      return this.payPrice;
   }

   public String getTips() {
      return this.tips;
   }

   public String getOriginalOrderNo() {
      return this.originalOrderNo;
   }

   public BigDecimal getRenewPrice() {
      return this.renewPrice;
   }

   public List<IndependentRenewItemResponse> getItems() {
      return this.items;
   }

   public void setBillingMode(final Integer billingMode) {
      this.billingMode = billingMode;
   }

   public void setMachineId(final String machineId) {
      this.machineId = machineId;
   }

   public void setMachineName(final String machineName) {
      this.machineName = machineName;
   }

   public void setMachineMonthPrice(final BigDecimal machineMonthPrice) {
      this.machineMonthPrice = machineMonthPrice;
   }

   public void setBandwidth(final Integer bandwidth) {
      this.bandwidth = bandwidth;
   }

   public void setBandwidthFreeSize(final Integer bandwidthFreeSize) {
      this.bandwidthFreeSize = bandwidthFreeSize;
   }

   public void setBandwidthMonthPrice(final BigDecimal bandwidthMonthPrice) {
      this.bandwidthMonthPrice = bandwidthMonthPrice;
   }

   public void setDefense(final Integer defense) {
      this.defense = defense;
   }

   public void setDefenseFreeSize(final Integer defenseFreeSize) {
      this.defenseFreeSize = defenseFreeSize;
   }

   public void setDefenseMonthPrice(final BigDecimal defenseMonthPrice) {
      this.defenseMonthPrice = defenseMonthPrice;
   }

   public void setIpSize(final Integer ipSize) {
      this.ipSize = ipSize;
   }

   public void setIpFreeSize(final Integer ipFreeSize) {
      this.ipFreeSize = ipFreeSize;
   }

   public void setIpMonthPrice(final BigDecimal ipMonthPrice) {
      this.ipMonthPrice = ipMonthPrice;
   }

   public void setUnit(final String unit) {
      this.unit = unit;
   }

   public void setDuration(final Integer duration) {
      this.duration = duration;
   }

   public void setNumber(final Integer number) {
      this.number = number;
   }

   public void setUnitPrice(final BigDecimal unitPrice) {
      this.unitPrice = unitPrice;
   }

   public void setTotalPrice(final BigDecimal totalPrice) {
      this.totalPrice = totalPrice;
   }

   public void setDiscountPrice(final BigDecimal discountPrice) {
      this.discountPrice = discountPrice;
   }

   public void setCouponPrice(final BigDecimal couponPrice) {
      this.couponPrice = couponPrice;
   }

   public void setPayPrice(final BigDecimal payPrice) {
      this.payPrice = payPrice;
   }

   public void setTips(final String tips) {
      this.tips = tips;
   }

   public void setOriginalOrderNo(final String originalOrderNo) {
      this.originalOrderNo = originalOrderNo;
   }

   public void setRenewPrice(final BigDecimal renewPrice) {
      this.renewPrice = renewPrice;
   }

   public void setItems(final List<IndependentRenewItemResponse> items) {
      this.items = items;
   }
}
