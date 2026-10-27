package com.rxinyun.payments.api.pojo;

import java.io.Serializable;
import java.math.BigDecimal;

public class UsdtPayResponse implements Serializable {
   private static final long serialVersionUID = 1L;
   private String walletAddress;
   private BigDecimal amountUsdt;
   private String orderNo;
   private String chain;
   private BigDecimal rate;

   public String getWalletAddress() {
      return this.walletAddress;
   }

   public BigDecimal getAmountUsdt() {
      return this.amountUsdt;
   }

   public String getOrderNo() {
      return this.orderNo;
   }

   public String getChain() {
      return this.chain;
   }

   public BigDecimal getRate() {
      return this.rate;
   }

   public void setWalletAddress(final String walletAddress) {
      this.walletAddress = walletAddress;
   }

   public void setAmountUsdt(final BigDecimal amountUsdt) {
      this.amountUsdt = amountUsdt;
   }

   public void setOrderNo(final String orderNo) {
      this.orderNo = orderNo;
   }

   public void setChain(final String chain) {
      this.chain = chain;
   }

   public void setRate(final BigDecimal rate) {
      this.rate = rate;
   }

   @Override
   public boolean equals(final Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof UsdtPayResponse other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else {
         Object this$walletAddress = this.getWalletAddress();
         Object other$walletAddress = other.getWalletAddress();
         if (this$walletAddress == null ? other$walletAddress == null : this$walletAddress.equals(other$walletAddress)) {
            Object this$amountUsdt = this.getAmountUsdt();
            Object other$amountUsdt = other.getAmountUsdt();
            if (this$amountUsdt == null ? other$amountUsdt == null : this$amountUsdt.equals(other$amountUsdt)) {
               Object this$orderNo = this.getOrderNo();
               Object other$orderNo = other.getOrderNo();
               if (this$orderNo == null ? other$orderNo == null : this$orderNo.equals(other$orderNo)) {
                  Object this$chain = this.getChain();
                  Object other$chain = other.getChain();
                  if (this$chain == null ? other$chain == null : this$chain.equals(other$chain)) {
                     Object this$rate = this.getRate();
                     Object other$rate = other.getRate();
                     return this$rate == null ? other$rate == null : this$rate.equals(other$rate);
                  } else {
                     return false;
                  }
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
      return other instanceof UsdtPayResponse;
   }

   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $walletAddress = this.getWalletAddress();
      result = result * 59 + ($walletAddress == null ? 43 : $walletAddress.hashCode());
      Object $amountUsdt = this.getAmountUsdt();
      result = result * 59 + ($amountUsdt == null ? 43 : $amountUsdt.hashCode());
      Object $orderNo = this.getOrderNo();
      result = result * 59 + ($orderNo == null ? 43 : $orderNo.hashCode());
      Object $chain = this.getChain();
      result = result * 59 + ($chain == null ? 43 : $chain.hashCode());
      Object $rate = this.getRate();
      return result * 59 + ($rate == null ? 43 : $rate.hashCode());
   }

   @Override
   public String toString() {
      return "UsdtPayResponse(walletAddress="
         + this.getWalletAddress()
         + ", amountUsdt="
         + this.getAmountUsdt()
         + ", orderNo="
         + this.getOrderNo()
         + ", chain="
         + this.getChain()
         + ", rate="
         + this.getRate()
         + ")";
   }
}
