package com.rxinyun.business.core.pojo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class BaseMemberVhostCreate implements Serializable {
   private String memberNo;
   private String orderNo;
   private Integer number;
   private String versionId;
   private String versionName;
   private String regionId;
   private String regionName;
   private String lineId;
   private String lineName;
   private Integer months;
   private Integer trafficSize;
   private Integer trafficFreeSize;
   private Integer billableTraffic;
   private BigDecimal monthPrice;
   private BigDecimal payPrice;
   private BigDecimal renewPrice;
   private String phpVersion;
   private List<String> domains;
   private String hostName;
   private Date endTime;
   private String platform;

   public String getMemberNo() {
      return this.memberNo;
   }

   public String getOrderNo() {
      return this.orderNo;
   }

   public Integer getNumber() {
      return this.number;
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

   public Integer getTrafficSize() {
      return this.trafficSize;
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

   public BigDecimal getPayPrice() {
      return this.payPrice;
   }

   public BigDecimal getRenewPrice() {
      return this.renewPrice;
   }

   public String getPhpVersion() {
      return this.phpVersion;
   }

   public List<String> getDomains() {
      return this.domains;
   }

   public String getHostName() {
      return this.hostName;
   }

   public Date getEndTime() {
      return this.endTime;
   }

   public String getPlatform() {
      return this.platform;
   }

   public void setMemberNo(final String memberNo) {
      this.memberNo = memberNo;
   }

   public void setOrderNo(final String orderNo) {
      this.orderNo = orderNo;
   }

   public void setNumber(final Integer number) {
      this.number = number;
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

   public void setTrafficSize(final Integer trafficSize) {
      this.trafficSize = trafficSize;
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

   public void setPayPrice(final BigDecimal payPrice) {
      this.payPrice = payPrice;
   }

   public void setRenewPrice(final BigDecimal renewPrice) {
      this.renewPrice = renewPrice;
   }

   public void setPhpVersion(final String phpVersion) {
      this.phpVersion = phpVersion;
   }

   public void setDomains(final List<String> domains) {
      this.domains = domains;
   }

   public void setHostName(final String hostName) {
      this.hostName = hostName;
   }

   public void setEndTime(final Date endTime) {
      this.endTime = endTime;
   }

   public void setPlatform(final String platform) {
      this.platform = platform;
   }
}
