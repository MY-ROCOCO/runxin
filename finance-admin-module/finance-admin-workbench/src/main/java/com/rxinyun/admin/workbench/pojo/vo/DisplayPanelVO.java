package com.rxinyun.admin.workbench.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

public class DisplayPanelVO {
   @Schema(
      description = "会员总数"
   )
   private Long clientNum;
   @Schema(
      description = "今日待解决工单"
   )
   private Long workOrderMissNum;
   @Schema(
      description = "今日待支付订单数量"
   )
   private Long orderNonPaymentNum;
   @Schema(
      description = "今日已支付订单"
   )
   private Long orderPayNum;
   @Schema(
      description = "今日支付金额"
   )
   private BigDecimal orderPayMoney;
   @Schema(
      description = "待审核发票数量"
   )
   private Long invoicePendingNum;
   @Schema(
      description = "待开发票金额"
   )
   private BigDecimal invoicePendingMoney;

   public Long getClientNum() {
      return this.clientNum;
   }

   public Long getWorkOrderMissNum() {
      return this.workOrderMissNum;
   }

   public Long getOrderNonPaymentNum() {
      return this.orderNonPaymentNum;
   }

   public Long getOrderPayNum() {
      return this.orderPayNum;
   }

   public BigDecimal getOrderPayMoney() {
      return this.orderPayMoney;
   }

   public Long getInvoicePendingNum() {
      return this.invoicePendingNum;
   }

   public BigDecimal getInvoicePendingMoney() {
      return this.invoicePendingMoney;
   }

   public void setClientNum(final Long clientNum) {
      this.clientNum = clientNum;
   }

   public void setWorkOrderMissNum(final Long workOrderMissNum) {
      this.workOrderMissNum = workOrderMissNum;
   }

   public void setOrderNonPaymentNum(final Long orderNonPaymentNum) {
      this.orderNonPaymentNum = orderNonPaymentNum;
   }

   public void setOrderPayNum(final Long orderPayNum) {
      this.orderPayNum = orderPayNum;
   }

   public void setOrderPayMoney(final BigDecimal orderPayMoney) {
      this.orderPayMoney = orderPayMoney;
   }

   public void setInvoicePendingNum(final Long invoicePendingNum) {
      this.invoicePendingNum = invoicePendingNum;
   }

   public void setInvoicePendingMoney(final BigDecimal invoicePendingMoney) {
      this.invoicePendingMoney = invoicePendingMoney;
   }
}
