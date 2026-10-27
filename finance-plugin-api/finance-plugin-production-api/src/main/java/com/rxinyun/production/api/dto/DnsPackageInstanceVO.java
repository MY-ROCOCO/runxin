package com.rxinyun.production.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Schema(
   description = "云解析DNS套餐实例信息"
)
public class DnsPackageInstanceVO implements Serializable {
   @Schema(
      description = "DNS套餐实例ID"
   )
   private String id;
   @Schema(
      description = "会员ID"
   )
   private String memberId;
   @Schema(
      description = "套餐模板ID"
   )
   private String packageId;
   @Schema(
      description = "套餐名称快照"
   )
   private String packageName;
   @Schema(
      description = "线路分组ID快照"
   )
   private String lineGroupId;
   @Schema(
      description = "单域名月价格快照"
   )
   private BigDecimal singleDomainPrice;
   @Schema(
      description = "最低TTL快照，单位秒"
   )
   private Integer minTtl;
   @Schema(
      description = "域名总配额"
   )
   private Integer domainQuota;
   @Schema(
      description = "已使用域名配额"
   )
   private Integer usedQuota;
   @Schema(
      description = "购买周期月数"
   )
   private Integer periodMonths;
   @Schema(
      description = "实例开始时间"
   )
   private Date startTime;
   @Schema(
      description = "下次到期时间"
   )
   private Date nextDueTime;
   @Schema(
      description = "来源订单号"
   )
   private String sourceOrderNo;
   @Schema(
      description = "状态：0停用 1启用"
   )
   private Integer status;
   @Schema(
      description = "主NS域名"
   )
   private String primaryNs;
   @Schema(
      description = "备NS域名"
   )
   private String secondaryNs;
   @Schema(
      description = "当前套餐实例绑定的托管域名列表"
   )
   private List<DnsBoundDomainVO> domains;

   public String getId() {
      return this.id;
   }

   public String getMemberId() {
      return this.memberId;
   }

   public String getPackageId() {
      return this.packageId;
   }

   public String getPackageName() {
      return this.packageName;
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

   public Integer getDomainQuota() {
      return this.domainQuota;
   }

   public Integer getUsedQuota() {
      return this.usedQuota;
   }

   public Integer getPeriodMonths() {
      return this.periodMonths;
   }

   public Date getStartTime() {
      return this.startTime;
   }

   public Date getNextDueTime() {
      return this.nextDueTime;
   }

   public String getSourceOrderNo() {
      return this.sourceOrderNo;
   }

   public Integer getStatus() {
      return this.status;
   }

   public String getPrimaryNs() {
      return this.primaryNs;
   }

   public String getSecondaryNs() {
      return this.secondaryNs;
   }

   public List<DnsBoundDomainVO> getDomains() {
      return this.domains;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setMemberId(final String memberId) {
      this.memberId = memberId;
   }

   public void setPackageId(final String packageId) {
      this.packageId = packageId;
   }

   public void setPackageName(final String packageName) {
      this.packageName = packageName;
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

   public void setDomainQuota(final Integer domainQuota) {
      this.domainQuota = domainQuota;
   }

   public void setUsedQuota(final Integer usedQuota) {
      this.usedQuota = usedQuota;
   }

   public void setPeriodMonths(final Integer periodMonths) {
      this.periodMonths = periodMonths;
   }

   public void setStartTime(final Date startTime) {
      this.startTime = startTime;
   }

   public void setNextDueTime(final Date nextDueTime) {
      this.nextDueTime = nextDueTime;
   }

   public void setSourceOrderNo(final String sourceOrderNo) {
      this.sourceOrderNo = sourceOrderNo;
   }

   public void setStatus(final Integer status) {
      this.status = status;
   }

   public void setPrimaryNs(final String primaryNs) {
      this.primaryNs = primaryNs;
   }

   public void setSecondaryNs(final String secondaryNs) {
      this.secondaryNs = secondaryNs;
   }

   public void setDomains(final List<DnsBoundDomainVO> domains) {
      this.domains = domains;
   }
}
