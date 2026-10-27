package com.rxinyun.payments.api.pojo;

import java.math.BigDecimal;
import java.util.Date;

public class PaymentsRecordAddParam {
   private Long memberNo;
   private String orderNo;
   private String tradeNo;
   private String businessType;
   private Date payTime;
   private BigDecimal payAmount;
   private Integer payType;
   private BigDecimal memberBalanceAfter;
   private String remark;
   private String businessParams;

   public Long getMemberNo() {
      return this.memberNo;
   }

   public String getOrderNo() {
      return this.orderNo;
   }

   public String getTradeNo() {
      return this.tradeNo;
   }

   public String getBusinessType() {
      return this.businessType;
   }

   public Date getPayTime() {
      return this.payTime;
   }

   public BigDecimal getPayAmount() {
      return this.payAmount;
   }

   public Integer getPayType() {
      return this.payType;
   }

   public BigDecimal getMemberBalanceAfter() {
      return this.memberBalanceAfter;
   }

   public String getRemark() {
      return this.remark;
   }

   public String getBusinessParams() {
      return this.businessParams;
   }

   public void setMemberNo(final Long memberNo) {
      this.memberNo = memberNo;
   }

   public void setOrderNo(final String orderNo) {
      this.orderNo = orderNo;
   }

   public void setTradeNo(final String tradeNo) {
      this.tradeNo = tradeNo;
   }

   public void setBusinessType(final String businessType) {
      this.businessType = businessType;
   }

   public void setPayTime(final Date payTime) {
      this.payTime = payTime;
   }

   public void setPayAmount(final BigDecimal payAmount) {
      this.payAmount = payAmount;
   }

   public void setPayType(final Integer payType) {
      this.payType = payType;
   }

   public void setMemberBalanceAfter(final BigDecimal memberBalanceAfter) {
      this.memberBalanceAfter = memberBalanceAfter;
   }

   public void setRemark(final String remark) {
      this.remark = remark;
   }

   public void setBusinessParams(final String businessParams) {
      this.businessParams = businessParams;
   }

   @Override
   public boolean equals(final Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof PaymentsRecordAddParam other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else {
         Object this$memberNo = this.getMemberNo();
         Object other$memberNo = other.getMemberNo();
         if (this$memberNo == null ? other$memberNo == null : this$memberNo.equals(other$memberNo)) {
            Object this$payType = this.getPayType();
            Object other$payType = other.getPayType();
            if (this$payType == null ? other$payType == null : this$payType.equals(other$payType)) {
               Object this$orderNo = this.getOrderNo();
               Object other$orderNo = other.getOrderNo();
               if (this$orderNo == null ? other$orderNo == null : this$orderNo.equals(other$orderNo)) {
                  Object this$tradeNo = this.getTradeNo();
                  Object other$tradeNo = other.getTradeNo();
                  if (this$tradeNo == null ? other$tradeNo == null : this$tradeNo.equals(other$tradeNo)) {
                     Object this$businessType = this.getBusinessType();
                     Object other$businessType = other.getBusinessType();
                     if (this$businessType == null ? other$businessType == null : this$businessType.equals(other$businessType)) {
                        Object this$payTime = this.getPayTime();
                        Object other$payTime = other.getPayTime();
                        if (this$payTime == null ? other$payTime == null : this$payTime.equals(other$payTime)) {
                           Object this$payAmount = this.getPayAmount();
                           Object other$payAmount = other.getPayAmount();
                           if (this$payAmount == null ? other$payAmount == null : this$payAmount.equals(other$payAmount)) {
                              Object this$memberBalanceAfter = this.getMemberBalanceAfter();
                              Object other$memberBalanceAfter = other.getMemberBalanceAfter();
                              if (this$memberBalanceAfter == null ? other$memberBalanceAfter == null : this$memberBalanceAfter.equals(other$memberBalanceAfter)
                                 )
                               {
                                 Object this$remark = this.getRemark();
                                 Object other$remark = other.getRemark();
                                 if (this$remark == null ? other$remark == null : this$remark.equals(other$remark)) {
                                    Object this$businessParams = this.getBusinessParams();
                                    Object other$businessParams = other.getBusinessParams();
                                    return this$businessParams == null ? other$businessParams == null : this$businessParams.equals(other$businessParams);
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
         } else {
            return false;
         }
      }
   }

   protected boolean canEqual(final Object other) {
      return other instanceof PaymentsRecordAddParam;
   }

   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $memberNo = this.getMemberNo();
      result = result * 59 + ($memberNo == null ? 43 : $memberNo.hashCode());
      Object $payType = this.getPayType();
      result = result * 59 + ($payType == null ? 43 : $payType.hashCode());
      Object $orderNo = this.getOrderNo();
      result = result * 59 + ($orderNo == null ? 43 : $orderNo.hashCode());
      Object $tradeNo = this.getTradeNo();
      result = result * 59 + ($tradeNo == null ? 43 : $tradeNo.hashCode());
      Object $businessType = this.getBusinessType();
      result = result * 59 + ($businessType == null ? 43 : $businessType.hashCode());
      Object $payTime = this.getPayTime();
      result = result * 59 + ($payTime == null ? 43 : $payTime.hashCode());
      Object $payAmount = this.getPayAmount();
      result = result * 59 + ($payAmount == null ? 43 : $payAmount.hashCode());
      Object $memberBalanceAfter = this.getMemberBalanceAfter();
      result = result * 59 + ($memberBalanceAfter == null ? 43 : $memberBalanceAfter.hashCode());
      Object $remark = this.getRemark();
      result = result * 59 + ($remark == null ? 43 : $remark.hashCode());
      Object $businessParams = this.getBusinessParams();
      return result * 59 + ($businessParams == null ? 43 : $businessParams.hashCode());
   }

   @Override
   public String toString() {
      return "PaymentsRecordAddParam(memberNo="
         + this.getMemberNo()
         + ", orderNo="
         + this.getOrderNo()
         + ", tradeNo="
         + this.getTradeNo()
         + ", businessType="
         + this.getBusinessType()
         + ", payTime="
         + this.getPayTime()
         + ", payAmount="
         + this.getPayAmount()
         + ", payType="
         + this.getPayType()
         + ", memberBalanceAfter="
         + this.getMemberBalanceAfter()
         + ", remark="
         + this.getRemark()
         + ", businessParams="
         + this.getBusinessParams()
         + ")";
   }
}
