package com.rxinyun.admin.order.modular.ecs.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.Date;

@TableName(
   value = "domain_order_item",
   autoResultMap = true
)
@Schema(
   description = "域名订单明细"
)
public class DomainOrderItem {
   @Schema(
      description = "主键"
   )
   private String id;
   @Schema(
      description = "订单编号"
   )
   private String orderNo;
   @Schema(
      description = "域名全称"
   )
   private String domainName;
   @Schema(
      description = "后缀"
   )
   private String tld;
   @Schema(
      description = "模板ID"
   )
   private String templateId;
   @Schema(
      description = "注册年限"
   )
   private Integer years;
   @Schema(
      description = "单价"
   )
   private BigDecimal unitPrice;
   @Schema(
      description = "小计"
   )
   private BigDecimal totalPrice;
   private String deleteFlag;
   private Date createTime;

   public String getId() {
      return this.id;
   }

   public String getOrderNo() {
      return this.orderNo;
   }

   public String getDomainName() {
      return this.domainName;
   }

   public String getTld() {
      return this.tld;
   }

   public String getTemplateId() {
      return this.templateId;
   }

   public Integer getYears() {
      return this.years;
   }

   public BigDecimal getUnitPrice() {
      return this.unitPrice;
   }

   public BigDecimal getTotalPrice() {
      return this.totalPrice;
   }

   public String getDeleteFlag() {
      return this.deleteFlag;
   }

   public Date getCreateTime() {
      return this.createTime;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setOrderNo(final String orderNo) {
      this.orderNo = orderNo;
   }

   public void setDomainName(final String domainName) {
      this.domainName = domainName;
   }

   public void setTld(final String tld) {
      this.tld = tld;
   }

   public void setTemplateId(final String templateId) {
      this.templateId = templateId;
   }

   public void setYears(final Integer years) {
      this.years = years;
   }

   public void setUnitPrice(final BigDecimal unitPrice) {
      this.unitPrice = unitPrice;
   }

   public void setTotalPrice(final BigDecimal totalPrice) {
      this.totalPrice = totalPrice;
   }

   public void setDeleteFlag(final String deleteFlag) {
      this.deleteFlag = deleteFlag;
   }

   public void setCreateTime(final Date createTime) {
      this.createTime = createTime;
   }
}
