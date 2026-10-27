package com.rxinyun.production.api.dto;

import java.io.Serializable;

public class DnsPackageInstanceRenewParam implements Serializable {
   private String instanceId;
   private String memberId;
   private Integer periodMonths;

   public String getInstanceId() {
      return this.instanceId;
   }

   public String getMemberId() {
      return this.memberId;
   }

   public Integer getPeriodMonths() {
      return this.periodMonths;
   }

   public void setInstanceId(final String instanceId) {
      this.instanceId = instanceId;
   }

   public void setMemberId(final String memberId) {
      this.memberId = memberId;
   }

   public void setPeriodMonths(final Integer periodMonths) {
      this.periodMonths = periodMonths;
   }
}
