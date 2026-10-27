package com.rxinyun.member.payments.modular.payments.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import java.io.Serializable;
import java.math.BigDecimal;

public class OrderServerAddParam implements Serializable {
   @Schema(
      description = "订单编号"
   )
   private String orderNo;
   @Schema(
      description = "服务器ID"
   )
   private String serverId;
   @Schema(
      description = "线路ID"
   )
   private String lineId;
   @Schema(
      description = "CPU配置"
   )
   private Integer cpu;
   @Schema(
      description = "内存配置"
   )
   private Integer ram;
   @Schema(
      description = "核心价格"
   )
   private BigDecimal lineSkuPrice;
   @Schema(
      description = "硬盘配置"
   )
   private Integer harddisks;
   @Schema(
      description = "硬盘价格"
   )
   private BigDecimal harddisksPrice;
   @Schema(
      description = "带宽配置"
   )
   private Integer bandwidth;
   @Schema(
      description = "带宽价格"
   )
   private BigDecimal bandwidthPrice;
   @Schema(
      description = "防御配置"
   )
   private Integer defense;
   @Schema(
      description = "防御价格"
   )
   private BigDecimal defensePrice;
   @Schema(
      description = "系统分类ID"
   )
   private String systemClassId;
   @Schema(
      description = "操作系统ID"
   )
   private String systemId;
   @Schema(
      description = "登录密码"
   )
   private String loginPassword;
   @Schema(
      description = "数量"
   )
   @Min(
      value = 1L,
      message = "购买数量必须大于0"
   )
   private Integer number;
   @Schema(
      description = "月份"
   )
   @Min(
      value = 1L,
      message = "购买时长必须大于0"
   )
   private Integer months;
   @Schema(
      description = "折扣价格"
   )
   private BigDecimal discountPrice;
   @Schema(
      description = "合计价格"
   )
   private BigDecimal serverTotalPrice;
   @Schema(
      description = "实付价格"
   )
   private BigDecimal payPrice;

   public String getOrderNo() {
      return this.orderNo;
   }

   public String getServerId() {
      return this.serverId;
   }

   public String getLineId() {
      return this.lineId;
   }

   public Integer getCpu() {
      return this.cpu;
   }

   public Integer getRam() {
      return this.ram;
   }

   public BigDecimal getLineSkuPrice() {
      return this.lineSkuPrice;
   }

   public Integer getHarddisks() {
      return this.harddisks;
   }

   public BigDecimal getHarddisksPrice() {
      return this.harddisksPrice;
   }

   public Integer getBandwidth() {
      return this.bandwidth;
   }

   public BigDecimal getBandwidthPrice() {
      return this.bandwidthPrice;
   }

   public Integer getDefense() {
      return this.defense;
   }

   public BigDecimal getDefensePrice() {
      return this.defensePrice;
   }

   public String getSystemClassId() {
      return this.systemClassId;
   }

   public String getSystemId() {
      return this.systemId;
   }

   public String getLoginPassword() {
      return this.loginPassword;
   }

   public Integer getNumber() {
      return this.number;
   }

   public Integer getMonths() {
      return this.months;
   }

   public BigDecimal getDiscountPrice() {
      return this.discountPrice;
   }

   public BigDecimal getServerTotalPrice() {
      return this.serverTotalPrice;
   }

   public BigDecimal getPayPrice() {
      return this.payPrice;
   }

   public void setOrderNo(final String orderNo) {
      this.orderNo = orderNo;
   }

   public void setServerId(final String serverId) {
      this.serverId = serverId;
   }

   public void setLineId(final String lineId) {
      this.lineId = lineId;
   }

   public void setCpu(final Integer cpu) {
      this.cpu = cpu;
   }

   public void setRam(final Integer ram) {
      this.ram = ram;
   }

   public void setLineSkuPrice(final BigDecimal lineSkuPrice) {
      this.lineSkuPrice = lineSkuPrice;
   }

   public void setHarddisks(final Integer harddisks) {
      this.harddisks = harddisks;
   }

   public void setHarddisksPrice(final BigDecimal harddisksPrice) {
      this.harddisksPrice = harddisksPrice;
   }

   public void setBandwidth(final Integer bandwidth) {
      this.bandwidth = bandwidth;
   }

   public void setBandwidthPrice(final BigDecimal bandwidthPrice) {
      this.bandwidthPrice = bandwidthPrice;
   }

   public void setDefense(final Integer defense) {
      this.defense = defense;
   }

   public void setDefensePrice(final BigDecimal defensePrice) {
      this.defensePrice = defensePrice;
   }

   public void setSystemClassId(final String systemClassId) {
      this.systemClassId = systemClassId;
   }

   public void setSystemId(final String systemId) {
      this.systemId = systemId;
   }

   public void setLoginPassword(final String loginPassword) {
      this.loginPassword = loginPassword;
   }

   public void setNumber(final Integer number) {
      this.number = number;
   }

   public void setMonths(final Integer months) {
      this.months = months;
   }

   public void setDiscountPrice(final BigDecimal discountPrice) {
      this.discountPrice = discountPrice;
   }

   public void setServerTotalPrice(final BigDecimal serverTotalPrice) {
      this.serverTotalPrice = serverTotalPrice;
   }

   public void setPayPrice(final BigDecimal payPrice) {
      this.payPrice = payPrice;
   }
}
