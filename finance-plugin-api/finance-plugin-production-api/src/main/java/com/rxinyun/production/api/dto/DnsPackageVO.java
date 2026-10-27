package com.rxinyun.production.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.math.BigDecimal;

@Schema(
   description = "云解析DNS套餐信息"
)
public class DnsPackageVO implements Serializable {
   @Schema(
      description = "套餐ID"
   )
   private String id;
   @Schema(
      description = "套餐名称"
   )
   private String name;
   @Schema(
      description = "绑定的DNS线路分组ID"
   )
   private String lineGroupId;
   @Schema(
      description = "单个域名月价格"
   )
   private BigDecimal singleDomainPrice;
   @Schema(
      description = "最低TTL，单位秒"
   )
   private Integer minTtl;
   @Schema(
      description = "默认域名配额"
   )
   private Integer defaultDomainQuota;
   @Schema(
      description = "主NS域名"
   )
   private String primaryNs;
   @Schema(
      description = "备NS域名"
   )
   private String secondaryNs;
   @Schema(
      description = "状态：0禁用 1启用"
   )
   private Integer status;

   public String getId() {
      return this.id;
   }

   public String getName() {
      return this.name;
   }

   public String getLineGroupId() {
      return this.lineGroupId;
   }

   public BigDecimal getSingleDomainPrice() {
      return this.singleDomainPrice;
   }

   public Integer getMinTtl() {
      return this.minTtl;
   }

   public Integer getDefaultDomainQuota() {
      return this.defaultDomainQuota;
   }

   public String getPrimaryNs() {
      return this.primaryNs;
   }

   public String getSecondaryNs() {
      return this.secondaryNs;
   }

   public Integer getStatus() {
      return this.status;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setName(final String name) {
      this.name = name;
   }

   public void setLineGroupId(final String lineGroupId) {
      this.lineGroupId = lineGroupId;
   }

   public void setSingleDomainPrice(final BigDecimal singleDomainPrice) {
      this.singleDomainPrice = singleDomainPrice;
   }

   public void setMinTtl(final Integer minTtl) {
      this.minTtl = minTtl;
   }

   public void setDefaultDomainQuota(final Integer defaultDomainQuota) {
      this.defaultDomainQuota = defaultDomainQuota;
   }

   public void setPrimaryNs(final String primaryNs) {
      this.primaryNs = primaryNs;
   }

   public void setSecondaryNs(final String secondaryNs) {
      this.secondaryNs = secondaryNs;
   }

   public void setStatus(final Integer status) {
      this.status = status;
   }
}
