package com.rxinyun.admin.order.modular.ecs.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.Map;

@Schema(
   description = "独立服务器订单配置"
)
public class IndependentOrderServerConfigResponse {
   @Schema(
      description = "主键ID"
   )
   private String id;
   @Schema(
      description = "订单编号"
   )
   private String orderNo;
   @Schema(
      description = "地域ID"
   )
   private String regionId;
   @Schema(
      description = "线路ID"
   )
   private String lineId;
   @Schema(
      description = "线路名称"
   )
   private String lineName;
   @Schema(
      description = "机型ID"
   )
   private String machineId;
   @Schema(
      description = "机型名称"
   )
   private String machineName;
   @Schema(
      description = "资源类型  0:自生产 1:资源池"
   )
   private Integer type;
   @Schema(
      description = "自动化  0库存自动化 1无库存人工服务机型"
   )
   private Integer automation;
   @Schema(
      description = "机型月付价格"
   )
   private BigDecimal machineMonthPrice;
   @Schema(
      description = "cpu规格信息"
   )
   private String cpuInfo;
   @Schema(
      description = "内存规格信息"
   )
   private String ramInfo;
   @Schema(
      description = "硬盘规格信息"
   )
   private String disksInfo;
   @Schema(
      description = "ip数量"
   )
   private Integer ipSize;
   @Schema(
      description = "赠送ip数量"
   )
   private Integer ipFreeSize;
   @Schema(
      description = "防御数量 G"
   )
   private Integer defense;
   @Schema(
      description = "赠送防御数量 G"
   )
   private Integer defenseFreeSize;
   @Schema(
      description = "带宽M"
   )
   private Integer bandwidth;
   @Schema(
      description = "赠送带宽M"
   )
   private Integer bandwidthFreeSize;
   @Schema(
      description = "每月带宽价格/M"
   )
   private BigDecimal bandwidthMonthPrice;
   @Schema(
      description = "每月防御价格/G"
   )
   private BigDecimal defenseMonthPrice;
   @Schema(
      description = "每月弹性ip价格/个"
   )
   private BigDecimal ipMonthPrice;
   @Schema(
      description = "升级 0不支持  1支持"
   )
   private Integer upgrades;
   @Schema(
      description = "单位 时 s，天 d，月 m，年 y"
   )
   @NotBlank(
      message = "单位不能为空"
   )
   private String unit;
   @Schema(
      description = "购买时长"
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
      description = "合计"
   )
   private BigDecimal totalPrice;
   @Schema(
      description = "实付金额"
   )
   private BigDecimal payPrice;
   @Schema(
      description = "备注"
   )
   private String remark;
   @Schema(
      description = "系统分类ID"
   )
   private String systemClassId;
   @Schema(
      description = "系统分类名称"
   )
   private String systemClass;
   @Schema(
      description = "操作系统ID"
   )
   private String systemId;
   @Schema(
      description = "操作系统名称"
   )
   private String systemName;
   @Schema(
      description = "登录密码"
   )
   private String loginPassword;
   @Schema(
      description = "续费价格"
   )
   private BigDecimal renewPrice;
   @Schema(
      description = "服务器列表"
   )
   private Map<String, String> serverMap;

   public String getId() {
      return this.id;
   }

   public String getOrderNo() {
      return this.orderNo;
   }

   public String getRegionId() {
      return this.regionId;
   }

   public String getLineId() {
      return this.lineId;
   }

   public String getLineName() {
      return this.lineName;
   }

   public String getMachineId() {
      return this.machineId;
   }

   public String getMachineName() {
      return this.machineName;
   }

   public Integer getType() {
      return this.type;
   }

   public Integer getAutomation() {
      return this.automation;
   }

   public BigDecimal getMachineMonthPrice() {
      return this.machineMonthPrice;
   }

   public String getCpuInfo() {
      return this.cpuInfo;
   }

   public String getRamInfo() {
      return this.ramInfo;
   }

   public String getDisksInfo() {
      return this.disksInfo;
   }

   public Integer getIpSize() {
      return this.ipSize;
   }

   public Integer getIpFreeSize() {
      return this.ipFreeSize;
   }

   public Integer getDefense() {
      return this.defense;
   }

   public Integer getDefenseFreeSize() {
      return this.defenseFreeSize;
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

   public BigDecimal getDefenseMonthPrice() {
      return this.defenseMonthPrice;
   }

   public BigDecimal getIpMonthPrice() {
      return this.ipMonthPrice;
   }

   public Integer getUpgrades() {
      return this.upgrades;
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

   public BigDecimal getRenewPrice() {
      return this.renewPrice;
   }

   public Map<String, String> getServerMap() {
      return this.serverMap;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setOrderNo(final String orderNo) {
      this.orderNo = orderNo;
   }

   public void setRegionId(final String regionId) {
      this.regionId = regionId;
   }

   public void setLineId(final String lineId) {
      this.lineId = lineId;
   }

   public void setLineName(final String lineName) {
      this.lineName = lineName;
   }

   public void setMachineId(final String machineId) {
      this.machineId = machineId;
   }

   public void setMachineName(final String machineName) {
      this.machineName = machineName;
   }

   public void setType(final Integer type) {
      this.type = type;
   }

   public void setAutomation(final Integer automation) {
      this.automation = automation;
   }

   public void setMachineMonthPrice(final BigDecimal machineMonthPrice) {
      this.machineMonthPrice = machineMonthPrice;
   }

   public void setCpuInfo(final String cpuInfo) {
      this.cpuInfo = cpuInfo;
   }

   public void setRamInfo(final String ramInfo) {
      this.ramInfo = ramInfo;
   }

   public void setDisksInfo(final String disksInfo) {
      this.disksInfo = disksInfo;
   }

   public void setIpSize(final Integer ipSize) {
      this.ipSize = ipSize;
   }

   public void setIpFreeSize(final Integer ipFreeSize) {
      this.ipFreeSize = ipFreeSize;
   }

   public void setDefense(final Integer defense) {
      this.defense = defense;
   }

   public void setDefenseFreeSize(final Integer defenseFreeSize) {
      this.defenseFreeSize = defenseFreeSize;
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

   public void setDefenseMonthPrice(final BigDecimal defenseMonthPrice) {
      this.defenseMonthPrice = defenseMonthPrice;
   }

   public void setIpMonthPrice(final BigDecimal ipMonthPrice) {
      this.ipMonthPrice = ipMonthPrice;
   }

   public void setUpgrades(final Integer upgrades) {
      this.upgrades = upgrades;
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

   public void setRenewPrice(final BigDecimal renewPrice) {
      this.renewPrice = renewPrice;
   }

   public void setServerMap(final Map<String, String> serverMap) {
      this.serverMap = serverMap;
   }
}
