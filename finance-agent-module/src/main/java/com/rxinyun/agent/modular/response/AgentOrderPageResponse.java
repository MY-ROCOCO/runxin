package com.rxinyun.agent.modular.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.Date;

public class AgentOrderPageResponse {
   @Schema(
      description = "订单号"
   )
   private String orderNo;
   @Schema(
      description = "会员编号"
   )
   private Long memberNo;
   @Schema(
      description = "会员账号"
   )
   private String memberAccount;
   @Schema(
      description = "会员姓名"
   )
   private String memberName;
   @Schema(
      description = "产品类型"
   )
   private Integer productType;
   @Schema(
      description = "订单状态"
   )
   private Integer orderStatus;
   @Schema(
      description = "实付金额"
   )
   private BigDecimal payPrice;
   @Schema(
      description = "支付时间"
   )
   private Date payTime;
   @Schema(
      description = "创建时间"
   )
   private Date createTime;

   public String getOrderNo() {
      return this.orderNo;
   }

   public Long getMemberNo() {
      return this.memberNo;
   }

   public String getMemberAccount() {
      return this.memberAccount;
   }

   public String getMemberName() {
      return this.memberName;
   }

   public Integer getProductType() {
      return this.productType;
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

   public Date getCreateTime() {
      return this.createTime;
   }

   public void setOrderNo(final String orderNo) {
      this.orderNo = orderNo;
   }

   public void setMemberNo(final Long memberNo) {
      this.memberNo = memberNo;
   }

   public void setMemberAccount(final String memberAccount) {
      this.memberAccount = memberAccount;
   }

   public void setMemberName(final String memberName) {
      this.memberName = memberName;
   }

   public void setProductType(final Integer productType) {
      this.productType = productType;
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

   public void setCreateTime(final Date createTime) {
      this.createTime = createTime;
   }

   @Override
   public boolean equals(final Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof AgentOrderPageResponse other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else {
         Object this$memberNo = this.getMemberNo();
         Object other$memberNo = other.getMemberNo();
         if (this$memberNo == null ? other$memberNo == null : this$memberNo.equals(other$memberNo)) {
            Object this$productType = this.getProductType();
            Object other$productType = other.getProductType();
            if (this$productType == null ? other$productType == null : this$productType.equals(other$productType)) {
               Object this$orderStatus = this.getOrderStatus();
               Object other$orderStatus = other.getOrderStatus();
               if (this$orderStatus == null ? other$orderStatus == null : this$orderStatus.equals(other$orderStatus)) {
                  Object this$orderNo = this.getOrderNo();
                  Object other$orderNo = other.getOrderNo();
                  if (this$orderNo == null ? other$orderNo == null : this$orderNo.equals(other$orderNo)) {
                     Object this$memberAccount = this.getMemberAccount();
                     Object other$memberAccount = other.getMemberAccount();
                     if (this$memberAccount == null ? other$memberAccount == null : this$memberAccount.equals(other$memberAccount)) {
                        Object this$memberName = this.getMemberName();
                        Object other$memberName = other.getMemberName();
                        if (this$memberName == null ? other$memberName == null : this$memberName.equals(other$memberName)) {
                           Object this$payPrice = this.getPayPrice();
                           Object other$payPrice = other.getPayPrice();
                           if (this$payPrice == null ? other$payPrice == null : this$payPrice.equals(other$payPrice)) {
                              Object this$payTime = this.getPayTime();
                              Object other$payTime = other.getPayTime();
                              if (this$payTime == null ? other$payTime == null : this$payTime.equals(other$payTime)) {
                                 Object this$createTime = this.getCreateTime();
                                 Object other$createTime = other.getCreateTime();
                                 return this$createTime == null ? other$createTime == null : this$createTime.equals(other$createTime);
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
      return other instanceof AgentOrderPageResponse;
   }

   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $memberNo = this.getMemberNo();
      result = result * 59 + ($memberNo == null ? 43 : $memberNo.hashCode());
      Object $productType = this.getProductType();
      result = result * 59 + ($productType == null ? 43 : $productType.hashCode());
      Object $orderStatus = this.getOrderStatus();
      result = result * 59 + ($orderStatus == null ? 43 : $orderStatus.hashCode());
      Object $orderNo = this.getOrderNo();
      result = result * 59 + ($orderNo == null ? 43 : $orderNo.hashCode());
      Object $memberAccount = this.getMemberAccount();
      result = result * 59 + ($memberAccount == null ? 43 : $memberAccount.hashCode());
      Object $memberName = this.getMemberName();
      result = result * 59 + ($memberName == null ? 43 : $memberName.hashCode());
      Object $payPrice = this.getPayPrice();
      result = result * 59 + ($payPrice == null ? 43 : $payPrice.hashCode());
      Object $payTime = this.getPayTime();
      result = result * 59 + ($payTime == null ? 43 : $payTime.hashCode());
      Object $createTime = this.getCreateTime();
      return result * 59 + ($createTime == null ? 43 : $createTime.hashCode());
   }

   @Override
   public String toString() {
      return "AgentOrderPageResponse(orderNo="
         + this.getOrderNo()
         + ", memberNo="
         + this.getMemberNo()
         + ", memberAccount="
         + this.getMemberAccount()
         + ", memberName="
         + this.getMemberName()
         + ", productType="
         + this.getProductType()
         + ", orderStatus="
         + this.getOrderStatus()
         + ", payPrice="
         + this.getPayPrice()
         + ", payTime="
         + this.getPayTime()
         + ", createTime="
         + this.getCreateTime()
         + ")";
   }
}
