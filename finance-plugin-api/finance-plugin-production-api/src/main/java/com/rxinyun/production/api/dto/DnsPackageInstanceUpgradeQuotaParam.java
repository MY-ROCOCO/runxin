package com.rxinyun.production.api.dto;

import java.io.Serializable;

public class DnsPackageInstanceUpgradeQuotaParam implements Serializable {
   private String instanceId;
   private String memberId;
   private Integer upgradeDomainQuota;

   public String getInstanceId() {
      return this.instanceId;
   }

   public String getMemberId() {
      return this.memberId;
   }

   public Integer getUpgradeDomainQuota() {
      return this.upgradeDomainQuota;
   }

   public void setInstanceId(final String instanceId) {
      this.instanceId = instanceId;
   }

   public void setMemberId(final String memberId) {
      this.memberId = memberId;
   }

   public void setUpgradeDomainQuota(final Integer upgradeDomainQuota) {
      this.upgradeDomainQuota = upgradeDomainQuota;
   }
}
