package com.rxinyun.admin.production.modular.dns.vo;

public class DnsNodeReplaceConfigRecord {
   private String recordId;
   private String zoneId;
   private String name;
   private String type;
   private String value;
   private Integer ttl;
   private Integer priority;
   private Boolean enabled;
   private String line;
   private String country;
   private String region;
   private String province;
   private String city;
   private String isp;

   public String getRecordId() {
      return this.recordId;
   }

   public String getZoneId() {
      return this.zoneId;
   }

   public String getName() {
      return this.name;
   }

   public String getType() {
      return this.type;
   }

   public String getValue() {
      return this.value;
   }

   public Integer getTtl() {
      return this.ttl;
   }

   public Integer getPriority() {
      return this.priority;
   }

   public Boolean getEnabled() {
      return this.enabled;
   }

   public String getLine() {
      return this.line;
   }

   public String getCountry() {
      return this.country;
   }

   public String getRegion() {
      return this.region;
   }

   public String getProvince() {
      return this.province;
   }

   public String getCity() {
      return this.city;
   }

   public String getIsp() {
      return this.isp;
   }

   public void setRecordId(final String recordId) {
      this.recordId = recordId;
   }

   public void setZoneId(final String zoneId) {
      this.zoneId = zoneId;
   }

   public void setName(final String name) {
      this.name = name;
   }

   public void setType(final String type) {
      this.type = type;
   }

   public void setValue(final String value) {
      this.value = value;
   }

   public void setTtl(final Integer ttl) {
      this.ttl = ttl;
   }

   public void setPriority(final Integer priority) {
      this.priority = priority;
   }

   public void setEnabled(final Boolean enabled) {
      this.enabled = enabled;
   }

   public void setLine(final String line) {
      this.line = line;
   }

   public void setCountry(final String country) {
      this.country = country;
   }

   public void setRegion(final String region) {
      this.region = region;
   }

   public void setProvince(final String province) {
      this.province = province;
   }

   public void setCity(final String city) {
      this.city = city;
   }

   public void setIsp(final String isp) {
      this.isp = isp;
   }
}
