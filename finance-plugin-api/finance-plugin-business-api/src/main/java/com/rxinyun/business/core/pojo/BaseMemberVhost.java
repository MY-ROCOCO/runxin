package com.rxinyun.business.core.pojo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class BaseMemberVhost implements Serializable {
   private String id;
   private String memberNo;
   private String orderNo;
   private String lastOrderNo;
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
   private BigDecimal trafficPrice;
   private BigDecimal renewPrice;
   private String phpVersions;
   private String phpVersion;
   private String mysqlVersions;
   private String hostName;
   private List<String> bindDomains;
   private String hostStatus;
   private String panelHostId;
   private Integer businessStatus;
   private Date endTime;
   private Date deleteTime;
   private Date createTime;

   public String getId() {
      return this.id;
   }

   public String getMemberNo() {
      return this.memberNo;
   }

   public String getOrderNo() {
      return this.orderNo;
   }

   public String getLastOrderNo() {
      return this.lastOrderNo;
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

   public BigDecimal getTrafficPrice() {
      return this.trafficPrice;
   }

   public BigDecimal getRenewPrice() {
      return this.renewPrice;
   }

   public String getPhpVersions() {
      return this.phpVersions;
   }

   public String getPhpVersion() {
      return this.phpVersion;
   }

   public String getMysqlVersions() {
      return this.mysqlVersions;
   }

   public String getHostName() {
      return this.hostName;
   }

   public List<String> getBindDomains() {
      return this.bindDomains;
   }

   public String getHostStatus() {
      return this.hostStatus;
   }

   public String getPanelHostId() {
      return this.panelHostId;
   }

   public Integer getBusinessStatus() {
      return this.businessStatus;
   }

   public Date getEndTime() {
      return this.endTime;
   }

   public Date getDeleteTime() {
      return this.deleteTime;
   }

   public Date getCreateTime() {
      return this.createTime;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setMemberNo(final String memberNo) {
      this.memberNo = memberNo;
   }

   public void setOrderNo(final String orderNo) {
      this.orderNo = orderNo;
   }

   public void setLastOrderNo(final String lastOrderNo) {
      this.lastOrderNo = lastOrderNo;
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

   public void setTrafficPrice(final BigDecimal trafficPrice) {
      this.trafficPrice = trafficPrice;
   }

   public void setRenewPrice(final BigDecimal renewPrice) {
      this.renewPrice = renewPrice;
   }

   public void setPhpVersions(final String phpVersions) {
      this.phpVersions = phpVersions;
   }

   public void setPhpVersion(final String phpVersion) {
      this.phpVersion = phpVersion;
   }

   public void setMysqlVersions(final String mysqlVersions) {
      this.mysqlVersions = mysqlVersions;
   }

   public void setHostName(final String hostName) {
      this.hostName = hostName;
   }

   public void setBindDomains(final List<String> bindDomains) {
      this.bindDomains = bindDomains;
   }

   public void setHostStatus(final String hostStatus) {
      this.hostStatus = hostStatus;
   }

   public void setPanelHostId(final String panelHostId) {
      this.panelHostId = panelHostId;
   }

   public void setBusinessStatus(final Integer businessStatus) {
      this.businessStatus = businessStatus;
   }

   public void setEndTime(final Date endTime) {
      this.endTime = endTime;
   }

   public void setDeleteTime(final Date deleteTime) {
      this.deleteTime = deleteTime;
   }

   public void setCreateTime(final Date createTime) {
      this.createTime = createTime;
   }
}
