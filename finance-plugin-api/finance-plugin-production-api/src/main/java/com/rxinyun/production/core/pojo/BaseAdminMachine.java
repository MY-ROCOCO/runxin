package com.rxinyun.production.core.pojo;

import com.rxinyun.common.pojo.CommonEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

public class BaseAdminMachine extends CommonEntity {
   @Schema(
      description = "机型ID"
   )
   private String id;
   @Schema(
      description = "机型名称"
   )
   private String name;
   @Schema(
      description = "机型库存"
   )
   private Integer inventory;
   @Schema(
      description = "资源类型  0:自生产 1:资源池"
   )
   private Integer type;
   @Schema(
      description = "资源池ID（type=1 或对接上游时关联 resource_pool.id）"
   )
   private String resourcePoolId;
   @Schema(
      description = "自动化  0库存自动化 1无库存人工服务机型"
   )
   private Integer automation;
   @Schema(
      description = "机型月付价格"
   )
   private BigDecimal machineMonthPrice;
   @Schema(
      description = "机型日付价格"
   )
   private BigDecimal machineDayPrice;
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
      description = "赠送ip数量"
   )
   private Integer ipFreeSize;
   @Schema(
      description = "赠送防御数量 G"
   )
   private Integer defenseFreeSize;
   @Schema(
      description = "赠送带宽M"
   )
   private Integer bandwidthFreeSize;
   @Schema(
      description = "带宽最小值 M"
   )
   private Integer bandwidthMinSize;
   @Schema(
      description = "带宽最大值 M"
   )
   private Integer bandwidthMaxSize;
   @Schema(
      description = "ip最小值"
   )
   private Integer ipMinSize;
   @Schema(
      description = "ip最大值"
   )
   private Integer ipMaxSize;
   @Schema(
      description = "防御峰值 [50，100，150]"
   )
   private String defenseMaxSize;
   @Schema(
      description = "每月带宽价格/M"
   )
   private BigDecimal bandwidthMonthPrice;
   @Schema(
      description = "每日带宽价格/M"
   )
   private BigDecimal bandwidthDayPrice;
   @Schema(
      description = "每月防御价格/G"
   )
   private BigDecimal defenseMonthPrice;
   @Schema(
      description = "每日防御价格/G"
   )
   private BigDecimal defenseDayPrice;
   @Schema(
      description = "每月弹性ip价格/个"
   )
   private BigDecimal ipMonthPrice;
   @Schema(
      description = "每日弹性ip价格/个"
   )
   private BigDecimal ipDayPrice;
   @Schema(
      description = "购买状态 0关闭  1开启"
   )
   private Integer status;
   @Schema(
      description = "升级 0不支持  1支持"
   )
   private Integer upgrades;
   @Schema(
      description = "权重"
   )
   private Integer weight;

   public String getId() {
      return this.id;
   }

   public String getName() {
      return this.name;
   }

   public Integer getInventory() {
      return this.inventory;
   }

   public Integer getType() {
      return this.type;
   }

   public String getResourcePoolId() {
      return this.resourcePoolId;
   }

   public Integer getAutomation() {
      return this.automation;
   }

   public BigDecimal getMachineMonthPrice() {
      return this.machineMonthPrice;
   }

   public BigDecimal getMachineDayPrice() {
      return this.machineDayPrice;
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

   public Integer getIpFreeSize() {
      return this.ipFreeSize;
   }

   public Integer getDefenseFreeSize() {
      return this.defenseFreeSize;
   }

   public Integer getBandwidthFreeSize() {
      return this.bandwidthFreeSize;
   }

   public Integer getBandwidthMinSize() {
      return this.bandwidthMinSize;
   }

   public Integer getBandwidthMaxSize() {
      return this.bandwidthMaxSize;
   }

   public Integer getIpMinSize() {
      return this.ipMinSize;
   }

   public Integer getIpMaxSize() {
      return this.ipMaxSize;
   }

   public String getDefenseMaxSize() {
      return this.defenseMaxSize;
   }

   public BigDecimal getBandwidthMonthPrice() {
      return this.bandwidthMonthPrice;
   }

   public BigDecimal getBandwidthDayPrice() {
      return this.bandwidthDayPrice;
   }

   public BigDecimal getDefenseMonthPrice() {
      return this.defenseMonthPrice;
   }

   public BigDecimal getDefenseDayPrice() {
      return this.defenseDayPrice;
   }

   public BigDecimal getIpMonthPrice() {
      return this.ipMonthPrice;
   }

   public BigDecimal getIpDayPrice() {
      return this.ipDayPrice;
   }

   public Integer getStatus() {
      return this.status;
   }

   public Integer getUpgrades() {
      return this.upgrades;
   }

   public Integer getWeight() {
      return this.weight;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setName(final String name) {
      this.name = name;
   }

   public void setInventory(final Integer inventory) {
      this.inventory = inventory;
   }

   public void setType(final Integer type) {
      this.type = type;
   }

   public void setResourcePoolId(final String resourcePoolId) {
      this.resourcePoolId = resourcePoolId;
   }

   public void setAutomation(final Integer automation) {
      this.automation = automation;
   }

   public void setMachineMonthPrice(final BigDecimal machineMonthPrice) {
      this.machineMonthPrice = machineMonthPrice;
   }

   public void setMachineDayPrice(final BigDecimal machineDayPrice) {
      this.machineDayPrice = machineDayPrice;
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

   public void setIpFreeSize(final Integer ipFreeSize) {
      this.ipFreeSize = ipFreeSize;
   }

   public void setDefenseFreeSize(final Integer defenseFreeSize) {
      this.defenseFreeSize = defenseFreeSize;
   }

   public void setBandwidthFreeSize(final Integer bandwidthFreeSize) {
      this.bandwidthFreeSize = bandwidthFreeSize;
   }

   public void setBandwidthMinSize(final Integer bandwidthMinSize) {
      this.bandwidthMinSize = bandwidthMinSize;
   }

   public void setBandwidthMaxSize(final Integer bandwidthMaxSize) {
      this.bandwidthMaxSize = bandwidthMaxSize;
   }

   public void setIpMinSize(final Integer ipMinSize) {
      this.ipMinSize = ipMinSize;
   }

   public void setIpMaxSize(final Integer ipMaxSize) {
      this.ipMaxSize = ipMaxSize;
   }

   public void setDefenseMaxSize(final String defenseMaxSize) {
      this.defenseMaxSize = defenseMaxSize;
   }

   public void setBandwidthMonthPrice(final BigDecimal bandwidthMonthPrice) {
      this.bandwidthMonthPrice = bandwidthMonthPrice;
   }

   public void setBandwidthDayPrice(final BigDecimal bandwidthDayPrice) {
      this.bandwidthDayPrice = bandwidthDayPrice;
   }

   public void setDefenseMonthPrice(final BigDecimal defenseMonthPrice) {
      this.defenseMonthPrice = defenseMonthPrice;
   }

   public void setDefenseDayPrice(final BigDecimal defenseDayPrice) {
      this.defenseDayPrice = defenseDayPrice;
   }

   public void setIpMonthPrice(final BigDecimal ipMonthPrice) {
      this.ipMonthPrice = ipMonthPrice;
   }

   public void setIpDayPrice(final BigDecimal ipDayPrice) {
      this.ipDayPrice = ipDayPrice;
   }

   public void setStatus(final Integer status) {
      this.status = status;
   }

   public void setUpgrades(final Integer upgrades) {
      this.upgrades = upgrades;
   }

   public void setWeight(final Integer weight) {
      this.weight = weight;
   }
}
