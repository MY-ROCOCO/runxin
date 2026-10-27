package com.rxinyun.production.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;

@Schema(
   description = "DNS解析记录下发参数"
)
public class DnsRecordCommandParam implements Serializable {
   @Schema(
      description = "解析记录ID"
   )
   private String recordId;
   @Schema(
      description = "托管域名ID"
   )
   private String zoneId;
   @Schema(
      description = "DNS套餐实例ID"
   )
   private String packageInstanceId;
   @Schema(
      description = "完整域名，如 app.example.com"
   )
   private String name;
   @Schema(
      description = "记录类型"
   )
   private String type;
   @Schema(
      description = "解析目标"
   )
   private String value;
   @Schema(
      description = "TTL"
   )
   private Integer ttl;
   @Schema(
      description = "MX优先级"
   )
   private Integer priority;
   @Schema(
      description = "是否启用"
   )
   private Boolean enabled;
   @Schema(
      description = "解析线路"
   )
   private String line;
   @Schema(
      description = "国家线路条件"
   )
   private String country;
   @Schema(
      description = "国家/地区代码"
   )
   private String region;
   @Schema(
      description = "省份线路条件"
   )
   private String province;
   @Schema(
      description = "城市线路条件"
   )
   private String city;
   @Schema(
      description = "运营商线路条件"
   )
   private String isp;

   public String getRecordId() {
      return this.recordId;
   }

   public String getZoneId() {
      return this.zoneId;
   }

   public String getPackageInstanceId() {
      return this.packageInstanceId;
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

   public void setPackageInstanceId(final String packageInstanceId) {
      this.packageInstanceId = packageInstanceId;
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
