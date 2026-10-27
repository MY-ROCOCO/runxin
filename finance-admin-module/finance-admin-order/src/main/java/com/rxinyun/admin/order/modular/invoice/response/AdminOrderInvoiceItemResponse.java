package com.rxinyun.admin.order.modular.invoice.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.Date;

public class AdminOrderInvoiceItemResponse {
   @Schema(
      description = "订单号"
   )
   private String orderNo;
   @Schema(
      description = "产品类型"
   )
   private Integer productType;
   @Schema(
      description = "订单类型"
   )
   private Integer orderType;
   @Schema(
      description = "订单状态"
   )
   private Integer orderStatus;
   @Schema(
      description = "申请时实付快照"
   )
   private BigDecimal payPrice;
   @Schema(
      description = "支付时间"
   )
   private Date payTime;

   public String getOrderNo() {
      return this.orderNo;
   }

   public Integer getProductType() {
      return this.productType;
   }

   public Integer getOrderType() {
      return this.orderType;
   }

   public Integer getOrderStatus() {
      return this.orderStatus;
   }

   public BigDecimal getPayPrice() {
      return this.payPrice;
   }

   public Date getPayTime() {
      return this.payTime;
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

   public void setOrderStatus(final Integer orderStatus) {
      this.orderStatus = orderStatus;
   }

   public void setPayPrice(final BigDecimal payPrice) {
      this.payPrice = payPrice;
   }

   public void setPayTime(final Date payTime) {
      this.payTime = payTime;
   }
}
