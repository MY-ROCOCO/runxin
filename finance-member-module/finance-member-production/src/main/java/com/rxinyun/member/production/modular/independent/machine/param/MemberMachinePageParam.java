package com.rxinyun.member.production.modular.independent.machine.param;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

public class MemberMachinePageParam {
   @Schema(
      description = "当前页码"
   )
   private Integer current;
   @Schema(
      description = "每页条数"
   )
   private Integer size;
   @Schema(
      description = "排序字段，字段驼峰名称，如：userName"
   )
   private String sortField;
   @Schema(
      description = "排序方式，升序：ASCEND；降序：DESCEND"
   )
   private String sortOrder;
   @Schema(
      description = "关键字  （机型名称）"
   )
   private String keyword;
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
      description = "资源类型  0:自生产 1:资源池  分类字段"
   )
   private Integer type;
   @Schema(
      description = "自动化  0库存自动化 1无库存人工服务机型"
   )
   private Integer automation;
   @Schema(
      description = "月付价格"
   )
   private BigDecimal monthlyPrice;
   @Schema(
      description = "cpu型号"
   )
   private String cpuType;
   @Schema(
      description = "内存"
   )
   private String internalStorage;
   @Schema(
      description = "硬盘"
   )
   private String hardDisk;
   @Schema(
      description = "硬盘大小"
   )
   private String diskSize;
   @Schema(
      description = "硬盘数量"
   )
   private String diskNumber;
   @Schema(
      description = "硬盘型号"
   )
   private String diskType;
   @Schema(
      description = "赠送ip数量"
   )
   private Integer ipQuantity;
   @Schema(
      description = "赠送防御数量 G"
   )
   private Integer defense;
   @Schema(
      description = "赠送带宽 M"
   )
   private Integer bandwidth;
   @Schema(
      description = "带宽最小值 M"
   )
   private Integer bandwidthMin;
   @Schema(
      description = "带宽最大值 M"
   )
   private Integer bandwidthMax;
   @Schema(
      description = "ip最小值"
   )
   private Integer ipQuantityMin;
   @Schema(
      description = "ip最大值"
   )
   private Integer ipQuantityMax;
   @Schema(
      description = "防御峰值 [50，100，150]"
   )
   private String defenseMax;
   @Schema(
      description = "每月带宽价格/M"
   )
   private BigDecimal bandwidthPrice;
   @Schema(
      description = "每月防御价格/G"
   )
   private BigDecimal defensePrice;
   @Schema(
      description = "每月弹性ip价格/个"
   )
   private BigDecimal elasticityIpPrice;
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

   public Integer getCurrent() {
      return this.current;
   }

   public Integer getSize() {
      return this.size;
   }

   public String getSortField() {
      return this.sortField;
   }

   public String getSortOrder() {
      return this.sortOrder;
   }

   public String getKeyword() {
      return this.keyword;
   }

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

   public Integer getAutomation() {
      return this.automation;
   }

   public BigDecimal getMonthlyPrice() {
      return this.monthlyPrice;
   }

   public String getCpuType() {
      return this.cpuType;
   }

   public String getInternalStorage() {
      return this.internalStorage;
   }

   public String getHardDisk() {
      return this.hardDisk;
   }

   public String getDiskSize() {
      return this.diskSize;
   }

   public String getDiskNumber() {
      return this.diskNumber;
   }

   public String getDiskType() {
      return this.diskType;
   }

   public Integer getIpQuantity() {
      return this.ipQuantity;
   }

   public Integer getDefense() {
      return this.defense;
   }

   public Integer getBandwidth() {
      return this.bandwidth;
   }

   public Integer getBandwidthMin() {
      return this.bandwidthMin;
   }

   public Integer getBandwidthMax() {
      return this.bandwidthMax;
   }

   public Integer getIpQuantityMin() {
      return this.ipQuantityMin;
   }

   public Integer getIpQuantityMax() {
      return this.ipQuantityMax;
   }

   public String getDefenseMax() {
      return this.defenseMax;
   }

   public BigDecimal getBandwidthPrice() {
      return this.bandwidthPrice;
   }

   public BigDecimal getDefensePrice() {
      return this.defensePrice;
   }

   public BigDecimal getElasticityIpPrice() {
      return this.elasticityIpPrice;
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

   public void setCurrent(final Integer current) {
      this.current = current;
   }

   public void setSize(final Integer size) {
      this.size = size;
   }

   public void setSortField(final String sortField) {
      this.sortField = sortField;
   }

   public void setSortOrder(final String sortOrder) {
      this.sortOrder = sortOrder;
   }

   public void setKeyword(final String keyword) {
      this.keyword = keyword;
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

   public void setAutomation(final Integer automation) {
      this.automation = automation;
   }

   public void setMonthlyPrice(final BigDecimal monthlyPrice) {
      this.monthlyPrice = monthlyPrice;
   }

   public void setCpuType(final String cpuType) {
      this.cpuType = cpuType;
   }

   public void setInternalStorage(final String internalStorage) {
      this.internalStorage = internalStorage;
   }

   public void setHardDisk(final String hardDisk) {
      this.hardDisk = hardDisk;
   }

   public void setDiskSize(final String diskSize) {
      this.diskSize = diskSize;
   }

   public void setDiskNumber(final String diskNumber) {
      this.diskNumber = diskNumber;
   }

   public void setDiskType(final String diskType) {
      this.diskType = diskType;
   }

   public void setIpQuantity(final Integer ipQuantity) {
      this.ipQuantity = ipQuantity;
   }

   public void setDefense(final Integer defense) {
      this.defense = defense;
   }

   public void setBandwidth(final Integer bandwidth) {
      this.bandwidth = bandwidth;
   }

   public void setBandwidthMin(final Integer bandwidthMin) {
      this.bandwidthMin = bandwidthMin;
   }

   public void setBandwidthMax(final Integer bandwidthMax) {
      this.bandwidthMax = bandwidthMax;
   }

   public void setIpQuantityMin(final Integer ipQuantityMin) {
      this.ipQuantityMin = ipQuantityMin;
   }

   public void setIpQuantityMax(final Integer ipQuantityMax) {
      this.ipQuantityMax = ipQuantityMax;
   }

   public void setDefenseMax(final String defenseMax) {
      this.defenseMax = defenseMax;
   }

   public void setBandwidthPrice(final BigDecimal bandwidthPrice) {
      this.bandwidthPrice = bandwidthPrice;
   }

   public void setDefensePrice(final BigDecimal defensePrice) {
      this.defensePrice = defensePrice;
   }

   public void setElasticityIpPrice(final BigDecimal elasticityIpPrice) {
      this.elasticityIpPrice = elasticityIpPrice;
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
