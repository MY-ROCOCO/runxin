package com.rxinyun.payments.api.pojo;

import java.math.BigDecimal;
import java.util.Date;

public class PendingUsdtPaymentDto {
   private String orderNo;
   private BigDecimal expectedAmountUsdt;
   private Date createTime;

   public String getOrderNo() {
      return this.orderNo;
   }

   public BigDecimal getExpectedAmountUsdt() {
      return this.expectedAmountUsdt;
   }

   public Date getCreateTime() {
      return this.createTime;
   }

   public void setOrderNo(final String orderNo) {
      this.orderNo = orderNo;
   }

   public void setExpectedAmountUsdt(final BigDecimal expectedAmountUsdt) {
      this.expectedAmountUsdt = expectedAmountUsdt;
   }

   public void setCreateTime(final Date createTime) {
      this.createTime = createTime;
   }

   @Override
   public boolean equals(final Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof PendingUsdtPaymentDto other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else {
         Object this$orderNo = this.getOrderNo();
         Object other$orderNo = other.getOrderNo();
         if (this$orderNo == null ? other$orderNo == null : this$orderNo.equals(other$orderNo)) {
            Object this$expectedAmountUsdt = this.getExpectedAmountUsdt();
            Object other$expectedAmountUsdt = other.getExpectedAmountUsdt();
            if (this$expectedAmountUsdt == null ? other$expectedAmountUsdt == null : this$expectedAmountUsdt.equals(other$expectedAmountUsdt)) {
               Object this$createTime = this.getCreateTime();
               Object other$createTime = other.getCreateTime();
               return this$createTime == null ? other$createTime == null : this$createTime.equals(other$createTime);
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   protected boolean canEqual(final Object other) {
      return other instanceof PendingUsdtPaymentDto;
   }

   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $orderNo = this.getOrderNo();
      result = result * 59 + ($orderNo == null ? 43 : $orderNo.hashCode());
      Object $expectedAmountUsdt = this.getExpectedAmountUsdt();
      result = result * 59 + ($expectedAmountUsdt == null ? 43 : $expectedAmountUsdt.hashCode());
      Object $createTime = this.getCreateTime();
      return result * 59 + ($createTime == null ? 43 : $createTime.hashCode());
   }

   @Override
   public String toString() {
      return "PendingUsdtPaymentDto(orderNo="
         + this.getOrderNo()
         + ", expectedAmountUsdt="
         + this.getExpectedAmountUsdt()
         + ", createTime="
         + this.getCreateTime()
         + ")";
   }
}
