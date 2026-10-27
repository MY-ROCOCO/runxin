package com.rxinyun.member.payments.modular.payments.param;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.Date;

@Schema(
   description = "待确认USDT支付"
)
public class PendingUsdtPayment {
   @Schema(
      description = "订单号/充值单号"
   )
   private String orderNo;
   @Schema(
      description = "应付 USDT 数量"
   )
   private BigDecimal expectedAmountUsdt;
   @Schema(
      description = "创建时间"
   )
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
      } else if (!(o instanceof PendingUsdtPayment other)) {
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
      return other instanceof PendingUsdtPayment;
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
      return "PendingUsdtPayment(orderNo="
         + this.getOrderNo()
         + ", expectedAmountUsdt="
         + this.getExpectedAmountUsdt()
         + ", createTime="
         + this.getCreateTime()
         + ")";
   }
}
