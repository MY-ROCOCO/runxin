package com.rxinyun.member.payments.modular.payments.param;

import java.math.BigDecimal;

public class BalancePaymentResponse {
   private String orderNo;
   private BigDecimal payAmount;
   private String payType;
   private String payStatus;

   public String getOrderNo() {
      return this.orderNo;
   }

   public BigDecimal getPayAmount() {
      return this.payAmount;
   }

   public String getPayType() {
      return this.payType;
   }

   public String getPayStatus() {
      return this.payStatus;
   }

   public void setOrderNo(final String orderNo) {
      this.orderNo = orderNo;
   }

   public void setPayAmount(final BigDecimal payAmount) {
      this.payAmount = payAmount;
   }

   public void setPayType(final String payType) {
      this.payType = payType;
   }

   public void setPayStatus(final String payStatus) {
      this.payStatus = payStatus;
   }

   @Override
   public boolean equals(final Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof BalancePaymentResponse other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else {
         Object this$orderNo = this.getOrderNo();
         Object other$orderNo = other.getOrderNo();
         if (this$orderNo == null ? other$orderNo == null : this$orderNo.equals(other$orderNo)) {
            Object this$payAmount = this.getPayAmount();
            Object other$payAmount = other.getPayAmount();
            if (this$payAmount == null ? other$payAmount == null : this$payAmount.equals(other$payAmount)) {
               Object this$payType = this.getPayType();
               Object other$payType = other.getPayType();
               if (this$payType == null ? other$payType == null : this$payType.equals(other$payType)) {
                  Object this$payStatus = this.getPayStatus();
                  Object other$payStatus = other.getPayStatus();
                  return this$payStatus == null ? other$payStatus == null : this$payStatus.equals(other$payStatus);
               } else {
                  return false;
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   protected boolean canEqual(final Object other) {
      return other instanceof BalancePaymentResponse;
   }

   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $orderNo = this.getOrderNo();
      result = result * 59 + ($orderNo == null ? 43 : $orderNo.hashCode());
      Object $payAmount = this.getPayAmount();
      result = result * 59 + ($payAmount == null ? 43 : $payAmount.hashCode());
      Object $payType = this.getPayType();
      result = result * 59 + ($payType == null ? 43 : $payType.hashCode());
      Object $payStatus = this.getPayStatus();
      return result * 59 + ($payStatus == null ? 43 : $payStatus.hashCode());
   }

   @Override
   public String toString() {
      return "BalancePaymentResponse(orderNo="
         + this.getOrderNo()
         + ", payAmount="
         + this.getPayAmount()
         + ", payType="
         + this.getPayType()
         + ", payStatus="
         + this.getPayStatus()
         + ")";
   }
}
