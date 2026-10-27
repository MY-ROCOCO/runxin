package com.rxinyun.admin.production.modular.dns.vo;

public class DnsNodeReplaceConfigZone {
   private String zoneId;
   private String name;
   private String adminEmail;
   private Integer soaTtl;
   private Integer negativeTtl;
   private String primaryNs;
   private String secondaryNs;

   public String getZoneId() {
      return this.zoneId;
   }

   public String getName() {
      return this.name;
   }

   public String getAdminEmail() {
      return this.adminEmail;
   }

   public Integer getSoaTtl() {
      return this.soaTtl;
   }

   public Integer getNegativeTtl() {
      return this.negativeTtl;
   }

   public String getPrimaryNs() {
      return this.primaryNs;
   }

   public String getSecondaryNs() {
      return this.secondaryNs;
   }

   public void setZoneId(final String zoneId) {
      this.zoneId = zoneId;
   }

   public void setName(final String name) {
      this.name = name;
   }

   public void setAdminEmail(final String adminEmail) {
      this.adminEmail = adminEmail;
   }

   public void setSoaTtl(final Integer soaTtl) {
      this.soaTtl = soaTtl;
   }

   public void setNegativeTtl(final Integer negativeTtl) {
      this.negativeTtl = negativeTtl;
   }

   public void setPrimaryNs(final String primaryNs) {
      this.primaryNs = primaryNs;
   }

   public void setSecondaryNs(final String secondaryNs) {
      this.secondaryNs = secondaryNs;
   }
}
