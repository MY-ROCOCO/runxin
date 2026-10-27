package com.rxinyun.admin.business.modular.dns.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.rxinyun.common.pojo.CommonEntity;
import io.swagger.v3.oas.annotations.media.Schema;

@TableName(
   value = "DNS_RECORD",
   autoResultMap = true
)
@Schema(
   description = "DNS解析记录"
)
public class DnsRecord extends CommonEntity {
   @Schema(
      description = "解析记录ID"
   )
   private String id;
   @Schema(
      description = "关联托管域名ID"
   )
   private String zoneId;
   @Schema(
      description = "主机记录，如 www、@"
   )
   private String host;
   @Schema(
      description = "完整域名，如 www.example.com"
   )
   private String name;
   @Schema(
      description = "记录类型，如 A/AAAA/CNAME/MX/NS/PTR/TXT"
   )
   private String type;
   @Schema(
      description = "解析目标，如IP、目标域名或文本内容"
   )
   private String value;
   @Schema(
      description = "TTL时间，单位秒"
   )
   private Integer ttl;
   @Schema(
      description = "MX优先级，其他类型可为空"
   )
   private Integer priority;
   @Schema(
      description = "是否启用：1启用 0暂停"
   )
   private Integer enabled;
   @Schema(
      description = "解析线路：default/telecom/unicom/mobile/overseas"
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
   @Schema(
      description = "备注"
   )
   private String remark;

   public String getId() {
      return this.id;
   }

   public String getZoneId() {
      return this.zoneId;
   }

   public String getHost() {
      return this.host;
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

   public Integer getEnabled() {
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

   public String getRemark() {
      return this.remark;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setZoneId(final String zoneId) {
      this.zoneId = zoneId;
   }

   public void setHost(final String host) {
      this.host = host;
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

   public void setEnabled(final Integer enabled) {
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

   public void setRemark(final String remark) {
      this.remark = remark;
   }
}
