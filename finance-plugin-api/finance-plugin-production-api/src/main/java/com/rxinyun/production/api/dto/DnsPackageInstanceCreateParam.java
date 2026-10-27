package com.rxinyun.production.api.dto;

import java.io.Serializable;
import java.util.Date;

public class DnsPackageInstanceCreateParam implements Serializable {
   private String memberId;
   private String packageId;
   private Integer domainQuota;
   private Integer periodMonths;
   private Date startTime;
   private String sourceOrderNo;
   private String remark;

   public String getMemberId() {
      return this.memberId;
   }

   public String getPackageId() {
      return this.packageId;
   }

   public Integer getDomainQuota() {
      return this.domainQuota;
   }

   public Integer getPeriodMonths() {
      return this.periodMonths;
   }

   public Date getStartTime() {
      return this.startTime;
   }

   public String getSourceOrderNo() {
      return this.sourceOrderNo;
   }

   public String getRemark() {
      return this.remark;
   }

   public void setMemberId(final String memberId) {
      this.memberId = memberId;
   }

   public void setPackageId(final String packageId) {
      this.packageId = packageId;
   }

   public void setDomainQuota(final Integer domainQuota) {
      this.domainQuota = domainQuota;
   }

   public void setPeriodMonths(final Integer periodMonths) {
      this.periodMonths = periodMonths;
   }

   public void setStartTime(final Date startTime) {
      this.startTime = startTime;
   }

   public void setSourceOrderNo(final String sourceOrderNo) {
      this.sourceOrderNo = sourceOrderNo;
   }

   public void setRemark(final String remark) {
      this.remark = remark;
   }
}
