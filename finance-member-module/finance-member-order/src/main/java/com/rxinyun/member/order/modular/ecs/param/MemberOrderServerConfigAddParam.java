package com.rxinyun.member.order.modular.ecs.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class MemberOrderServerConfigAddParam implements Serializable {
   @Schema(
      description = "订单编号"
   )
   private String orderNo;
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
      description = "系统盘"
   )
   private Integer systemDisk;
   @Schema(
      description = "数据硬盘配置"
   )
   private List<Integer> dataDisks;
   @Schema(
      description = "带宽配置"
   )
   private Integer bandwidth;
   @Schema(
      description = "防御配置"
   )
   private Integer defense;
   @Schema(
      description = "核心配置日单价"
   )
   private BigDecimal coreDayPrice;
   @Schema(
      description = "核心配置月单价"
   )
   private BigDecimal coreMonthPrice;
   @Schema(
      description = "系统盘配置日单价"
   )
   private BigDecimal systemDiskDayPrice;
   @Schema(
      description = "系统盘配置月单价"
   )
   private BigDecimal systemDiskMonthPrice;
   @Schema(
      description = "系统盘赠送大小"
   )
   private Integer systemDiskFreeSize;
   @Schema(
      description = "数据硬盘配置日单价"
   )
   private BigDecimal dataDisksDayPrice;
   @Schema(
      description = "数据硬盘配置月单价"
   )
   private BigDecimal dataDisksMonthPrice;
   @Schema(
      description = "数据盘赠送大小"
   )
   private Integer dataDisksFreeSize;
   @Schema(
      description = "带宽配置日单价"
   )
   private BigDecimal bandwidthDayPrice;
   @Schema(
      description = "带宽配置月单价"
   )
   private BigDecimal bandwidthMonthPrice;
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
      description = "防御配置日单价"
   )
   private BigDecimal defenseDayPrice;
   @Schema(
      description = "防御配置月单价"
   )
   private BigDecimal defenseMonthPrice;
   @Schema(
      description = "赠送防御"
   )
   private Integer defenseFreeSize;
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
      description = "单价"
   )
   private BigDecimal unitPrice;
   @Schema(
      description = "购买数量"
   )
   private Integer number;
   @Schema(
      description = "总价"
   )
   private BigDecimal totalPrice;
   @Schema(
      description = "实付金额"
   )
   private BigDecimal payPrice;
   @Schema(
      description = "续费价格"
   )
   private BigDecimal renewPrice;
   @Schema(
      description = "备注"
   )
   private String remark;
   @Schema(
      description = "操作系统分类ID"
   )
   private String systemClassId;
   @Schema(
      description = "操作系统分类名称"
   )
   private String systemClass;
   @Schema(
      description = "操作系统ID"
   )
   private String systemId;
   @Schema(
      description = "操作系统"
   )
   private String systemName;
   @Schema(
      description = "登录密码"
   )
   private String loginPassword;

   public String getOrderNo() {
      return this.orderNo;
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

   public Integer getSystemDisk() {
      return this.systemDisk;
   }

   public List<Integer> getDataDisks() {
      return this.dataDisks;
   }

   public Integer getBandwidth() {
      return this.bandwidth;
   }

   public Integer getDefense() {
      return this.defense;
   }

   public BigDecimal getCoreDayPrice() {
      return this.coreDayPrice;
   }

   public BigDecimal getCoreMonthPrice() {
      return this.coreMonthPrice;
   }

   public BigDecimal getSystemDiskDayPrice() {
      return this.systemDiskDayPrice;
   }

   public BigDecimal getSystemDiskMonthPrice() {
      return this.systemDiskMonthPrice;
   }

   public Integer getSystemDiskFreeSize() {
      return this.systemDiskFreeSize;
   }

   public BigDecimal getDataDisksDayPrice() {
      return this.dataDisksDayPrice;
   }

   public BigDecimal getDataDisksMonthPrice() {
      return this.dataDisksMonthPrice;
   }

   public Integer getDataDisksFreeSize() {
      return this.dataDisksFreeSize;
   }

   public BigDecimal getBandwidthDayPrice() {
      return this.bandwidthDayPrice;
   }

   public BigDecimal getBandwidthMonthPrice() {
      return this.bandwidthMonthPrice;
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

   public BigDecimal getDefenseDayPrice() {
      return this.defenseDayPrice;
   }

   public BigDecimal getDefenseMonthPrice() {
      return this.defenseMonthPrice;
   }

   public Integer getDefenseFreeSize() {
      return this.defenseFreeSize;
   }

   public String getUnit() {
      return this.unit;
   }

   public Integer getDuration() {
      return this.duration;
   }

   public BigDecimal getUnitPrice() {
      return this.unitPrice;
   }

   public Integer getNumber() {
      return this.number;
   }

   public BigDecimal getTotalPrice() {
      return this.totalPrice;
   }

   public BigDecimal getPayPrice() {
      return this.payPrice;
   }

   public BigDecimal getRenewPrice() {
      return this.renewPrice;
   }

   public String getRemark() {
      return this.remark;
   }

   public String getSystemClassId() {
      return this.systemClassId;
   }

   public String getSystemClass() {
      return this.systemClass;
   }

   public String getSystemId() {
      return this.systemId;
   }

   public String getSystemName() {
      return this.systemName;
   }

   public String getLoginPassword() {
      return this.loginPassword;
   }

   public void setOrderNo(final String orderNo) {
      this.orderNo = orderNo;
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

   public void setSystemDisk(final Integer systemDisk) {
      this.systemDisk = systemDisk;
   }

   public void setDataDisks(final List<Integer> dataDisks) {
      this.dataDisks = dataDisks;
   }

   public void setBandwidth(final Integer bandwidth) {
      this.bandwidth = bandwidth;
   }

   public void setDefense(final Integer defense) {
      this.defense = defense;
   }

   public void setCoreDayPrice(final BigDecimal coreDayPrice) {
      this.coreDayPrice = coreDayPrice;
   }

   public void setCoreMonthPrice(final BigDecimal coreMonthPrice) {
      this.coreMonthPrice = coreMonthPrice;
   }

   public void setSystemDiskDayPrice(final BigDecimal systemDiskDayPrice) {
      this.systemDiskDayPrice = systemDiskDayPrice;
   }

   public void setSystemDiskMonthPrice(final BigDecimal systemDiskMonthPrice) {
      this.systemDiskMonthPrice = systemDiskMonthPrice;
   }

   public void setSystemDiskFreeSize(final Integer systemDiskFreeSize) {
      this.systemDiskFreeSize = systemDiskFreeSize;
   }

   public void setDataDisksDayPrice(final BigDecimal dataDisksDayPrice) {
      this.dataDisksDayPrice = dataDisksDayPrice;
   }

   public void setDataDisksMonthPrice(final BigDecimal dataDisksMonthPrice) {
      this.dataDisksMonthPrice = dataDisksMonthPrice;
   }

   public void setDataDisksFreeSize(final Integer dataDisksFreeSize) {
      this.dataDisksFreeSize = dataDisksFreeSize;
   }

   public void setBandwidthDayPrice(final BigDecimal bandwidthDayPrice) {
      this.bandwidthDayPrice = bandwidthDayPrice;
   }

   public void setBandwidthMonthPrice(final BigDecimal bandwidthMonthPrice) {
      this.bandwidthMonthPrice = bandwidthMonthPrice;
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

   public void setDefenseDayPrice(final BigDecimal defenseDayPrice) {
      this.defenseDayPrice = defenseDayPrice;
   }

   public void setDefenseMonthPrice(final BigDecimal defenseMonthPrice) {
      this.defenseMonthPrice = defenseMonthPrice;
   }

   public void setDefenseFreeSize(final Integer defenseFreeSize) {
      this.defenseFreeSize = defenseFreeSize;
   }

   public void setUnit(final String unit) {
      this.unit = unit;
   }

   public void setDuration(final Integer duration) {
      this.duration = duration;
   }

   public void setUnitPrice(final BigDecimal unitPrice) {
      this.unitPrice = unitPrice;
   }

   public void setNumber(final Integer number) {
      this.number = number;
   }

   public void setTotalPrice(final BigDecimal totalPrice) {
      this.totalPrice = totalPrice;
   }

   public void setPayPrice(final BigDecimal payPrice) {
      this.payPrice = payPrice;
   }

   public void setRenewPrice(final BigDecimal renewPrice) {
      this.renewPrice = renewPrice;
   }

   public void setRemark(final String remark) {
      this.remark = remark;
   }

   public void setSystemClassId(final String systemClassId) {
      this.systemClassId = systemClassId;
   }

   public void setSystemClass(final String systemClass) {
      this.systemClass = systemClass;
   }

   public void setSystemId(final String systemId) {
      this.systemId = systemId;
   }

   public void setSystemName(final String systemName) {
      this.systemName = systemName;
   }

   public void setLoginPassword(final String loginPassword) {
      this.loginPassword = loginPassword;
   }
}
