package com.rxinyun.member.order.modular.vhost.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.List;

public class VhostCalculatePriceResponse {
   @Schema(
      description = "订单类型：1新购 2升级 3续费"
   )
   private Integer orderType;
   @Schema(
      description = "虚拟主机实例ID"
   )
   private String hostId;
   @Schema(
      description = "版本ID"
   )
   private String versionId;
   @Schema(
      description = "版本名称"
   )
   private String versionName;
   @Schema(
      description = "地域ID"
   )
   private String regionId;
   @Schema(
      description = "地域名称"
   )
   private String regionName;
   @Schema(
      description = "线路ID"
   )
   private String lineId;
   @Schema(
      description = "线路名称"
   )
   private String lineName;
   @Schema(
      description = "购买或续费月数"
   )
   private Integer months;
   @Schema(
      description = "购买数量，续费和升级固定为1"
   )
   private Integer number;
   @Schema(
      description = "升级时剩余天数"
   )
   private Integer remainingDays;
   @Schema(
      description = "购买流量或升级后的总流量，单位G"
   )
   private Integer trafficSize;
   @Schema(
      description = "本次升级购买的流量，单位G"
   )
   private Integer upgradeTraffic;
   @Schema(
      description = "赠送流量，单位G"
   )
   private Integer trafficFreeSize;
   @Schema(
      description = "计费流量，单位G"
   )
   private Integer billableTraffic;
   @Schema(
      description = "版本月价格"
   )
   private BigDecimal monthPrice;
   @Schema(
      description = "续费月价格"
   )
   private BigDecimal renewPrice;
   @Schema(
      description = "流量单价，元/G"
   )
   private BigDecimal trafficPrice;
   @Schema(
      description = "流量日单价，元/G，月单价除以30"
   )
   private BigDecimal trafficDayPrice;
   @Schema(
      description = "月费"
   )
   private BigDecimal monthAmount;
   @Schema(
      description = "流量费"
   )
   private BigDecimal trafficAmount;
   @Schema(
      description = "折扣和优惠券前原价"
   )
   private BigDecimal totalPrice;
   @Schema(
      description = "折扣模板ID"
   )
   private String discountTemplateId;
   @Schema(
      description = "会员组折扣系数，1表示无折扣"
   )
   private BigDecimal discountRate;
   @Schema(
      description = "会员组折扣金额"
   )
   private BigDecimal discountPrice;
   @Schema(
      description = "优惠券抵扣金额"
   )
   private BigDecimal couponPrice;
   @Schema(
      description = "实付金额"
   )
   private BigDecimal payPrice;
   @Schema(
      description = "套餐支持的PHP版本"
   )
   private String phpVersions;
   @Schema(
      description = "下单选择的PHP版本"
   )
   private String phpVersion;
   @Schema(
      description = "绑定域名，数量与购买台数一致"
   )
   private List<String> domains;
   @Schema(
      description = "支持的MySQL版本"
   )
   private String mysqlVersions;

   public Integer getOrderType() {
      return this.orderType;
   }

   public String getHostId() {
      return this.hostId;
   }

   public String getVersionId() {
      return this.versionId;
   }

   public String getVersionName() {
      return this.versionName;
   }

   public String getRegionId() {
      return this.regionId;
   }

   public String getRegionName() {
      return this.regionName;
   }

   public String getLineId() {
      return this.lineId;
   }

   public String getLineName() {
      return this.lineName;
   }

   public Integer getMonths() {
      return this.months;
   }

   public Integer getNumber() {
      return this.number;
   }

   public Integer getRemainingDays() {
      return this.remainingDays;
   }

   public Integer getTrafficSize() {
      return this.trafficSize;
   }

   public Integer getUpgradeTraffic() {
      return this.upgradeTraffic;
   }

   public Integer getTrafficFreeSize() {
      return this.trafficFreeSize;
   }

   public Integer getBillableTraffic() {
      return this.billableTraffic;
   }

   public BigDecimal getMonthPrice() {
      return this.monthPrice;
   }

   public BigDecimal getRenewPrice() {
      return this.renewPrice;
   }

   public BigDecimal getTrafficPrice() {
      return this.trafficPrice;
   }

   public BigDecimal getTrafficDayPrice() {
      return this.trafficDayPrice;
   }

   public BigDecimal getMonthAmount() {
      return this.monthAmount;
   }

   public BigDecimal getTrafficAmount() {
      return this.trafficAmount;
   }

   public BigDecimal getTotalPrice() {
      return this.totalPrice;
   }

   public String getDiscountTemplateId() {
      return this.discountTemplateId;
   }

   public BigDecimal getDiscountRate() {
      return this.discountRate;
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

   public String getPhpVersions() {
      return this.phpVersions;
   }

   public String getPhpVersion() {
      return this.phpVersion;
   }

   public List<String> getDomains() {
      return this.domains;
   }

   public String getMysqlVersions() {
      return this.mysqlVersions;
   }

   public void setOrderType(final Integer orderType) {
      this.orderType = orderType;
   }

   public void setHostId(final String hostId) {
      this.hostId = hostId;
   }

   public void setVersionId(final String versionId) {
      this.versionId = versionId;
   }

   public void setVersionName(final String versionName) {
      this.versionName = versionName;
   }

   public void setRegionId(final String regionId) {
      this.regionId = regionId;
   }

   public void setRegionName(final String regionName) {
      this.regionName = regionName;
   }

   public void setLineId(final String lineId) {
      this.lineId = lineId;
   }

   public void setLineName(final String lineName) {
      this.lineName = lineName;
   }

   public void setMonths(final Integer months) {
      this.months = months;
   }

   public void setNumber(final Integer number) {
      this.number = number;
   }

   public void setRemainingDays(final Integer remainingDays) {
      this.remainingDays = remainingDays;
   }

   public void setTrafficSize(final Integer trafficSize) {
      this.trafficSize = trafficSize;
   }

   public void setUpgradeTraffic(final Integer upgradeTraffic) {
      this.upgradeTraffic = upgradeTraffic;
   }

   public void setTrafficFreeSize(final Integer trafficFreeSize) {
      this.trafficFreeSize = trafficFreeSize;
   }

   public void setBillableTraffic(final Integer billableTraffic) {
      this.billableTraffic = billableTraffic;
   }

   public void setMonthPrice(final BigDecimal monthPrice) {
      this.monthPrice = monthPrice;
   }

   public void setRenewPrice(final BigDecimal renewPrice) {
      this.renewPrice = renewPrice;
   }

   public void setTrafficPrice(final BigDecimal trafficPrice) {
      this.trafficPrice = trafficPrice;
   }

   public void setTrafficDayPrice(final BigDecimal trafficDayPrice) {
      this.trafficDayPrice = trafficDayPrice;
   }

   public void setMonthAmount(final BigDecimal monthAmount) {
      this.monthAmount = monthAmount;
   }

   public void setTrafficAmount(final BigDecimal trafficAmount) {
      this.trafficAmount = trafficAmount;
   }

   public void setTotalPrice(final BigDecimal totalPrice) {
      this.totalPrice = totalPrice;
   }

   public void setDiscountTemplateId(final String discountTemplateId) {
      this.discountTemplateId = discountTemplateId;
   }

   public void setDiscountRate(final BigDecimal discountRate) {
      this.discountRate = discountRate;
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

   public void setPhpVersions(final String phpVersions) {
      this.phpVersions = phpVersions;
   }

   public void setPhpVersion(final String phpVersion) {
      this.phpVersion = phpVersion;
   }

   public void setDomains(final List<String> domains) {
      this.domains = domains;
   }

   public void setMysqlVersions(final String mysqlVersions) {
      this.mysqlVersions = mysqlVersions;
   }
}
