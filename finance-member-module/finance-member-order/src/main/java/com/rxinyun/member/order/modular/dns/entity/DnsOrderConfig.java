package com.rxinyun.member.order.modular.dns.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.rxinyun.common.pojo.CommonEntity;
import com.rxinyun.production.api.dto.DnsPackageInstanceVO;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@TableName(
   value = "dns_order_config",
   autoResultMap = true
)
@Schema(
   description = "DNS订单配置快照"
)
public class DnsOrderConfig extends CommonEntity {
   @Schema(
      description = "主键ID"
   )
   private String id;
   @Schema(
      description = "订单号"
   )
   private String orderNo;
   @Schema(
      description = "订单类型：1新购 2升级 3续费"
   )
   private Integer orderType;
   @Schema(
      description = "DNS套餐实例ID"
   )
   private String instanceId;
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
      description = "最低TTL快照"
   )
   private Integer minTtl;
   @Schema(
      description = "订单完成后的域名总配额"
   )
   private Integer domainQuota;
   @Schema(
      description = "升级新增域名配额"
   )
   private Integer upgradeDomainQuota;
   @Schema(
      description = "周期月数"
   )
   private Integer periodMonths;
   @Schema(
      description = "升级时剩余天数"
   )
   private Integer remainingDays;
   @Schema(
      description = "订单原价"
   )
   private BigDecimal totalPrice;
   @Schema(
      description = "订单实付金额"
   )
   private BigDecimal payPrice;
   @Schema(
      description = "备注"
   )
   private String remark;
   @Schema(
      description = "支付后创建或更新后的DNS套餐实例"
   )
   @TableField(
      exist = false
   )
   private DnsPackageInstanceVO packageInstance;

   public String getId() {
      return this.id;
   }

   public String getOrderNo() {
      return this.orderNo;
   }

   public Integer getOrderType() {
      return this.orderType;
   }

   public String getInstanceId() {
      return this.instanceId;
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

   public Integer getUpgradeDomainQuota() {
      return this.upgradeDomainQuota;
   }

   public Integer getPeriodMonths() {
      return this.periodMonths;
   }

   public Integer getRemainingDays() {
      return this.remainingDays;
   }

   public BigDecimal getTotalPrice() {
      return this.totalPrice;
   }

   public BigDecimal getPayPrice() {
      return this.payPrice;
   }

   public String getRemark() {
      return this.remark;
   }

   public DnsPackageInstanceVO getPackageInstance() {
      return this.packageInstance;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setOrderNo(final String orderNo) {
      this.orderNo = orderNo;
   }

   public void setOrderType(final Integer orderType) {
      this.orderType = orderType;
   }

   public void setInstanceId(final String instanceId) {
      this.instanceId = instanceId;
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

   public void setUpgradeDomainQuota(final Integer upgradeDomainQuota) {
      this.upgradeDomainQuota = upgradeDomainQuota;
   }

   public void setPeriodMonths(final Integer periodMonths) {
      this.periodMonths = periodMonths;
   }

   public void setRemainingDays(final Integer remainingDays) {
      this.remainingDays = remainingDays;
   }

   public void setTotalPrice(final BigDecimal totalPrice) {
      this.totalPrice = totalPrice;
   }

   public void setPayPrice(final BigDecimal payPrice) {
      this.payPrice = payPrice;
   }

   public void setRemark(final String remark) {
      this.remark = remark;
   }

   public void setPackageInstance(final DnsPackageInstanceVO packageInstance) {
      this.packageInstance = packageInstance;
   }
}
