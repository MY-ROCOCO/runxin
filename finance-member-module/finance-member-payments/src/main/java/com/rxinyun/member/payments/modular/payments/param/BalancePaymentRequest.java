package com.rxinyun.member.payments.modular.payments.param;

import jakarta.validation.constraints.Min;
import java.math.BigDecimal;

public class BalancePaymentRequest {
   private Long userId;
   @Min(
      value = 1L,
      message = "支付金额必须大于0"
   )
   private BigDecimal payAmount;
   private String outTradeNo;
   private String businessType;

   public Long getUserId() {
      return this.userId;
   }

   public BigDecimal getPayAmount() {
      return this.payAmount;
   }

   public String getOutTradeNo() {
      return this.outTradeNo;
   }

   public String getBusinessType() {
      return this.businessType;
   }

   public void setUserId(final Long userId) {
      this.userId = userId;
   }

   public void setPayAmount(final BigDecimal payAmount) {
      this.payAmount = payAmount;
   }

   public void setOutTradeNo(final String outTradeNo) {
      this.outTradeNo = outTradeNo;
   }

   public void setBusinessType(final String businessType) {
      this.businessType = businessType;
   }

   @Override
   public boolean equals(final Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof BalancePaymentRequest other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else {
         Object this$userId = this.getUserId();
         Object other$userId = other.getUserId();
         if (this$userId == null ? other$userId == null : this$userId.equals(other$userId)) {
            Object this$payAmount = this.getPayAmount();
            Object other$payAmount = other.getPayAmount();
            if (this$payAmount == null ? other$payAmount == null : this$payAmount.equals(other$payAmount)) {
               Object this$outTradeNo = this.getOutTradeNo();
               Object other$outTradeNo = other.getOutTradeNo();
               if (this$outTradeNo == null ? other$outTradeNo == null : this$outTradeNo.equals(other$outTradeNo)) {
                  Object this$businessType = this.getBusinessType();
                  Object other$businessType = other.getBusinessType();
                  return this$businessType == null ? other$businessType == null : this$businessType.equals(other$businessType);
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
      return other instanceof BalancePaymentRequest;
   }

   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $userId = this.getUserId();
      result = result * 59 + ($userId == null ? 43 : $userId.hashCode());
      Object $payAmount = this.getPayAmount();
      result = result * 59 + ($payAmount == null ? 43 : $payAmount.hashCode());
      Object $outTradeNo = this.getOutTradeNo();
      result = result * 59 + ($outTradeNo == null ? 43 : $outTradeNo.hashCode());
      Object $businessType = this.getBusinessType();
      return result * 59 + ($businessType == null ? 43 : $businessType.hashCode());
   }

   @Override
   public String toString() {
      return "BalancePaymentRequest(userId="
         + this.getUserId()
         + ", payAmount="
         + this.getPayAmount()
         + ", outTradeNo="
         + this.getOutTradeNo()
         + ", businessType="
         + this.getBusinessType()
         + ")";
   }
}
