package com.rxinyun.member.payments.modular.payments.param;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(
   description = "USDT支付信息"
)
public class UsdtPayResponse {
   @Schema(
      description = "收款地址"
   )
   private String walletAddress;
   @Schema(
      description = "应付 USDT 数量"
   )
   private BigDecimal amountUsdt;
   @Schema(
      description = "订单号/充值单号，转账备注务必填写此项"
   )
   private String orderNo;
   @Schema(
      description = "链类型，如 TRC20/ERC20"
   )
   private String chain;
   @Schema(
      description = "当前汇率：1 USDT = ? 人民币"
   )
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
