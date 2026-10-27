package com.rxinyun.member.order.modular.ecs.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class MemberCalculatePriceResponse implements Serializable {
   @Schema(
      description = "计费方式：1：包年包月:2：按量计费 "
   )
   @NotNull(
      message = "计费方式不能为空"
   )
   private Integer billingMode;
   @Schema(
      description = "CPU配置"
   )
   private Integer cpu;
   @Schema(
      description = "内存配置"
   )
   private Integer ram;
   @Schema(
      description = "核心配置日单价"
   )
   private BigDecimal coreDayPrice;
   @Schema(
      description = "核心配置月单价"
   )
   private BigDecimal coreMonthPrice;
   @Schema(
      description = "系统盘"
   )
   private Integer systemDisk;
   @Schema(
      description = "系统盘赠送大小"
   )
   private Integer systemDiskFreeSize;
   @Schema(
      description = "系统盘配置日单价"
   )
   private BigDecimal systemDiskDayPrice;
   @Schema(
      description = "系统盘配置月单价"
   )
   private BigDecimal systemDiskMonthPrice;
   @Schema(
      description = "数据硬盘配置"
   )
   private List<Integer> dataDisks;
   @Schema(
      description = "数据盘赠送大小"
   )
   private Integer dataDisksFreeSize;
   @Schema(
      description = "数据硬盘配置日单价"
   )
   private BigDecimal dataDisksDayPrice;
   @Schema(
      description = "数据硬盘配置月单价"
   )
   private BigDecimal dataDisksMonthPrice;
   @Schema(
      description = "带宽配置"
   )
   private Integer bandwidth;
   @Schema(
      description = "带宽赠送值"
   )
   private Integer bandwidthFreeSize;
   @Schema(
      description = "IP数量"
   )
   private Integer ipNum;
   @Schema(
      description = "赠送IP数量"
   )
   private Integer ipFreeSize;
   @Schema(
      description = "IP月单价"
   )
   private BigDecimal ipMonthPrice;
   @Schema(
      description = "带宽配置日单价"
   )
   private BigDecimal bandwidthDayPrice;
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
      description = "防御配置日单价"
   )
   private BigDecimal defenseDayPrice;
   @Schema(
      description = "防御配置月单价"
   )
   private BigDecimal defenseMonthPrice;
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
   private List<MemberRenewItemResponse> items;

   public Integer getBillingMode() {
      return this.billingMode;
   }

   public Integer getCpu() {
      return this.cpu;
   }

   public Integer getRam() {
      return this.ram;
   }

   public BigDecimal getCoreDayPrice() {
      return this.coreDayPrice;
   }

   public BigDecimal getCoreMonthPrice() {
      return this.coreMonthPrice;
   }

   public Integer getSystemDisk() {
      return this.systemDisk;
   }

   public Integer getSystemDiskFreeSize() {
      return this.systemDiskFreeSize;
   }

   public BigDecimal getSystemDiskDayPrice() {
      return this.systemDiskDayPrice;
   }

   public BigDecimal getSystemDiskMonthPrice() {
      return this.systemDiskMonthPrice;
   }

   public List<Integer> getDataDisks() {
      return this.dataDisks;
   }

   public Integer getDataDisksFreeSize() {
      return this.dataDisksFreeSize;
   }

   public BigDecimal getDataDisksDayPrice() {
      return this.dataDisksDayPrice;
   }

   public BigDecimal getDataDisksMonthPrice() {
      return this.dataDisksMonthPrice;
   }

   public Integer getBandwidth() {
      return this.bandwidth;
   }

   public Integer getBandwidthFreeSize() {
      return this.bandwidthFreeSize;
   }

   public Integer getIpNum() {
      return this.ipNum;
   }

   public Integer getIpFreeSize() {
      return this.ipFreeSize;
   }

   public BigDecimal getIpMonthPrice() {
      return this.ipMonthPrice;
   }

   public BigDecimal getBandwidthDayPrice() {
      return this.bandwidthDayPrice;
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

   public BigDecimal getDefenseDayPrice() {
      return this.defenseDayPrice;
   }

   public BigDecimal getDefenseMonthPrice() {
      return this.defenseMonthPrice;
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

   public List<MemberRenewItemResponse> getItems() {
      return this.items;
   }

   public void setBillingMode(final Integer billingMode) {
      this.billingMode = billingMode;
   }

   public void setCpu(final Integer cpu) {
      this.cpu = cpu;
   }

   public void setRam(final Integer ram) {
      this.ram = ram;
   }

   public void setCoreDayPrice(final BigDecimal coreDayPrice) {
      this.coreDayPrice = coreDayPrice;
   }

   public void setCoreMonthPrice(final BigDecimal coreMonthPrice) {
      this.coreMonthPrice = coreMonthPrice;
   }

   public void setSystemDisk(final Integer systemDisk) {
      this.systemDisk = systemDisk;
   }

   public void setSystemDiskFreeSize(final Integer systemDiskFreeSize) {
      this.systemDiskFreeSize = systemDiskFreeSize;
   }

   public void setSystemDiskDayPrice(final BigDecimal systemDiskDayPrice) {
      this.systemDiskDayPrice = systemDiskDayPrice;
   }

   public void setSystemDiskMonthPrice(final BigDecimal systemDiskMonthPrice) {
      this.systemDiskMonthPrice = systemDiskMonthPrice;
   }

   public void setDataDisks(final List<Integer> dataDisks) {
      this.dataDisks = dataDisks;
   }

   public void setDataDisksFreeSize(final Integer dataDisksFreeSize) {
      this.dataDisksFreeSize = dataDisksFreeSize;
   }

   public void setDataDisksDayPrice(final BigDecimal dataDisksDayPrice) {
      this.dataDisksDayPrice = dataDisksDayPrice;
   }

   public void setDataDisksMonthPrice(final BigDecimal dataDisksMonthPrice) {
      this.dataDisksMonthPrice = dataDisksMonthPrice;
   }

   public void setBandwidth(final Integer bandwidth) {
      this.bandwidth = bandwidth;
   }

   public void setBandwidthFreeSize(final Integer bandwidthFreeSize) {
      this.bandwidthFreeSize = bandwidthFreeSize;
   }

   public void setIpNum(final Integer ipNum) {
      this.ipNum = ipNum;
   }

   public void setIpFreeSize(final Integer ipFreeSize) {
      this.ipFreeSize = ipFreeSize;
   }

   public void setIpMonthPrice(final BigDecimal ipMonthPrice) {
      this.ipMonthPrice = ipMonthPrice;
   }

   public void setBandwidthDayPrice(final BigDecimal bandwidthDayPrice) {
      this.bandwidthDayPrice = bandwidthDayPrice;
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

   public void setDefenseDayPrice(final BigDecimal defenseDayPrice) {
      this.defenseDayPrice = defenseDayPrice;
   }

   public void setDefenseMonthPrice(final BigDecimal defenseMonthPrice) {
      this.defenseMonthPrice = defenseMonthPrice;
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

   public void setItems(final List<MemberRenewItemResponse> items) {
      this.items = items;
   }
}
