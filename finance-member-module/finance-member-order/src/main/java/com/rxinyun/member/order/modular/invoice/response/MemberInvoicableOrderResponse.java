package com.rxinyun.member.order.modular.invoice.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.Date;

public class MemberInvoicableOrderResponse {
   @Schema(
      description = "订单号"
   )
   private String orderNo;
   @Schema(
      description = "产品类型: 1 云服务器 2 独立服务器 3 域名 4 DNS云解析"
   )
   private Integer productType;
   @Schema(
      description = "订单类型 (1: 新购, 2: 升级, 3: 续费)"
   )
   private Integer orderType;
   @Schema(
      description = "实付金额"
   )
   private BigDecimal payPrice;
   @Schema(
      description = "支付时间"
   )
   private Date payTime;
   @Schema(
      description = "创建时间"
   )
   private Date createTime;

   public String getOrderNo() {
      return this.orderNo;
   }

   public Integer getProductType() {
      return this.productType;
   }

   public Integer getOrderType() {
      return this.orderType;
   }

   public BigDecimal getPayPrice() {
      return this.payPrice;
   }

   public Date getPayTime() {
      return this.payTime;
   }

   public Date getCreateTime() {
      return this.createTime;
   }

   public void setOrderNo(final String orderNo) {
      this.orderNo = orderNo;
   }

   public void setProductType(final Integer productType) {
      this.productType = productType;
   }

   public void setOrderType(final Integer orderType) {
      this.orderType = orderType;
   }

   public void setPayPrice(final BigDecimal payPrice) {
      this.payPrice = payPrice;
   }

   public void setPayTime(final Date payTime) {
      this.payTime = payTime;
   }

   public void setCreateTime(final Date createTime) {
      this.createTime = createTime;
   }
}
