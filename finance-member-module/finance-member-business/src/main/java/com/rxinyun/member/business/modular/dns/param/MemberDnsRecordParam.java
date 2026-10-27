package com.rxinyun.member.business.modular.dns.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class MemberDnsRecordParam {
   public static class AddParam {
      @NotBlank(
         message = "托管域名不能为空"
      )
      @Schema(
         description = "关联托管域名ID",
         requiredMode = RequiredMode.REQUIRED
      )
      private String zoneId;
      @NotBlank(
         message = "主机记录不能为空"
      )
      @Schema(
         description = "主机记录，如 www、@",
         requiredMode = RequiredMode.REQUIRED
      )
      private String host;
      @NotBlank(
         message = "记录类型不能为空"
      )
      @Schema(
         description = "记录类型，如 A/AAAA/CNAME/MX/NS/PTR/TXT",
         requiredMode = RequiredMode.REQUIRED
      )
      private String type;
      @NotBlank(
         message = "解析目标不能为空"
      )
      @Schema(
         description = "解析目标，如IP、目标域名或文本内容",
         requiredMode = RequiredMode.REQUIRED
      )
      private String value;
      @Schema(
         description = "TTL时间，单位秒；不传默认使用套餐最低TTL"
      )
      private Integer ttl;
      @Schema(
         description = "MX优先级，其他类型可为空"
      )
      private Integer priority;
      @Schema(
         description = "是否启用：1启用 0暂停；不传默认启用"
      )
      private Integer enabled;
      @Schema(
         description = "解析线路：default/telecom/unicom/mobile/overseas；不传默认 default"
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

      public String getZoneId() {
         return this.zoneId;
      }

      public String getHost() {
         return this.host;
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

      public void setZoneId(final String zoneId) {
         this.zoneId = zoneId;
      }

      public void setHost(final String host) {
         this.host = host;
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

   public static class EditParam extends MemberDnsRecordParam.AddParam {
      @NotBlank(
         message = "id不能为空"
      )
      @Schema(
         description = "解析记录ID",
         requiredMode = RequiredMode.REQUIRED
      )
      private String id;

      public String getId() {
         return this.id;
      }

      public void setId(final String id) {
         this.id = id;
      }
   }

   public static class IdParam {
      @NotBlank(
         message = "id不能为空"
      )
      @Schema(
         description = "解析记录ID",
         requiredMode = RequiredMode.REQUIRED
      )
      private String id;

      public String getId() {
         return this.id;
      }

      public void setId(final String id) {
         this.id = id;
      }
   }

   public static class PageParam {
      @Schema(
         description = "当前页码"
      )
      private Integer current;
      @Schema(
         description = "每页条数"
      )
      private Integer size;
      @Schema(
         description = "排序字段，字段驼峰名称"
      )
      private String sortField;
      @Schema(
         description = "排序方式，升序：ASCEND；降序：DESCEND"
      )
      private String sortOrder;
      @Schema(
         description = "关键词，匹配主机记录、完整域名、记录值"
      )
      private String searchKey;
      @Schema(
         description = "关联托管域名ID"
      )
      private String zoneId;
      @Schema(
         description = "记录类型"
      )
      private String type;
      @Schema(
         description = "是否启用：1启用 0暂停"
      )
      private Integer enabled;

      public Integer getCurrent() {
         return this.current;
      }

      public Integer getSize() {
         return this.size;
      }

      public String getSortField() {
         return this.sortField;
      }

      public String getSortOrder() {
         return this.sortOrder;
      }

      public String getSearchKey() {
         return this.searchKey;
      }

      public String getZoneId() {
         return this.zoneId;
      }

      public String getType() {
         return this.type;
      }

      public Integer getEnabled() {
         return this.enabled;
      }

      public void setCurrent(final Integer current) {
         this.current = current;
      }

      public void setSize(final Integer size) {
         this.size = size;
      }

      public void setSortField(final String sortField) {
         this.sortField = sortField;
      }

      public void setSortOrder(final String sortOrder) {
         this.sortOrder = sortOrder;
      }

      public void setSearchKey(final String searchKey) {
         this.searchKey = searchKey;
      }

      public void setZoneId(final String zoneId) {
         this.zoneId = zoneId;
      }

      public void setType(final String type) {
         this.type = type;
      }

      public void setEnabled(final Integer enabled) {
         this.enabled = enabled;
      }
   }

   public static class StatusParam extends MemberDnsRecordParam.IdParam {
      @NotNull(
         message = "启用状态不能为空"
      )
      @Schema(
         description = "是否启用：1启用 0暂停",
         requiredMode = RequiredMode.REQUIRED
      )
      private Integer enabled;

      public Integer getEnabled() {
         return this.enabled;
      }

      public void setEnabled(final Integer enabled) {
         this.enabled = enabled;
      }
   }
}
