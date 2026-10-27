package com.rxinyun.admin.order.modular.manual.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.math.BigDecimal;

public class ManualOrderResult implements Serializable {
   @Schema(
      description = "订单号"
   )
   private String orderNo;
   @Schema(
      description = "会员ID"
   )
   private Long memberId;
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
      description = "原价"
   )
   private BigDecimal totalPrice;
   @Schema(
      description = "实付金额/扣除金额"
   )
   private BigDecimal payPrice;

   public String getOrderNo() {
      return this.orderNo;
   }

   public Long getMemberId() {
      return this.memberId;
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

   public BigDecimal getTotalPrice() {
      return this.totalPrice;
   }

   public BigDecimal getPayPrice() {
      return this.payPrice;
   }

   public void setOrderNo(final String orderNo) {
      this.orderNo = orderNo;
   }

   public void setMemberId(final Long memberId) {
      this.memberId = memberId;
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

   public void setTotalPrice(final BigDecimal totalPrice) {
      this.totalPrice = totalPrice;
   }

   public void setPayPrice(final BigDecimal payPrice) {
      this.payPrice = payPrice;
   }
}
