package com.rxinyun.admin.payments.modular.payments.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.Date;

@TableName("payments_record")
public class PaymentsRecord {
   @Schema(
      description = "交易明细ID"
   )
   @TableId(
      type = IdType.AUTO
   )
   private String id;
   @Schema(
      description = "会员编号"
   )
   private String memberNo;
   @Schema(
      description = "订单号"
   )
   private String orderNo;
   @Schema(
      description = "交易流水号"
   )
   private String tradeNo;
   @Schema(
      description = "业务类型"
   )
   private String businessType;
   @Schema(
      description = "付款时间"
   )
   private Date payTime;
   @Schema(
      description = "支付金额"
   )
   private BigDecimal payAmount;
   @Schema(
      description = "业务参数"
   )
   private String businessParams;
   @Schema(
      description = "支付方式(1: 微信, 2: 支付宝, 3: 积分, 4: 余额, 5: USDT, 6:财务调整 增加 7:财务调整 扣减)"
   )
   private Integer payType;
   @Schema(
      description = "本笔发生后会员账户余额"
   )
   private BigDecimal memberBalanceAfter;
   @Schema(
      description = "创建时间"
   )
   private Date createTime;
   @Schema(
      description = "备注"
   )
   private String remark;

   public String getId() {
      return this.id;
   }

   public String getMemberNo() {
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

   public String getBusinessParams() {
      return this.businessParams;
   }

   public Integer getPayType() {
      return this.payType;
   }

   public BigDecimal getMemberBalanceAfter() {
      return this.memberBalanceAfter;
   }

   public Date getCreateTime() {
      return this.createTime;
   }

   public String getRemark() {
      return this.remark;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setMemberNo(final String memberNo) {
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

   public void setBusinessParams(final String businessParams) {
      this.businessParams = businessParams;
   }

   public void setPayType(final Integer payType) {
      this.payType = payType;
   }

   public void setMemberBalanceAfter(final BigDecimal memberBalanceAfter) {
      this.memberBalanceAfter = memberBalanceAfter;
   }

   public void setCreateTime(final Date createTime) {
      this.createTime = createTime;
   }

   public void setRemark(final String remark) {
      this.remark = remark;
   }

   @Override
   public boolean equals(final Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof PaymentsRecord other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else {
         Object this$payType = this.getPayType();
         Object other$payType = other.getPayType();
         if (this$payType == null ? other$payType == null : this$payType.equals(other$payType)) {
            Object this$id = this.getId();
            Object other$id = other.getId();
            if (this$id == null ? other$id == null : this$id.equals(other$id)) {
               Object this$memberNo = this.getMemberNo();
               Object other$memberNo = other.getMemberNo();
               if (this$memberNo == null ? other$memberNo == null : this$memberNo.equals(other$memberNo)) {
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
                                 Object this$businessParams = this.getBusinessParams();
                                 Object other$businessParams = other.getBusinessParams();
                                 if (this$businessParams == null ? other$businessParams == null : this$businessParams.equals(other$businessParams)) {
                                    Object this$memberBalanceAfter = this.getMemberBalanceAfter();
                                    Object other$memberBalanceAfter = other.getMemberBalanceAfter();
                                    if (this$memberBalanceAfter == null
                                       ? other$memberBalanceAfter == null
                                       : this$memberBalanceAfter.equals(other$memberBalanceAfter)) {
                                       Object this$createTime = this.getCreateTime();
                                       Object other$createTime = other.getCreateTime();
                                       if (this$createTime == null ? other$createTime == null : this$createTime.equals(other$createTime)) {
                                          Object this$remark = this.getRemark();
                                          Object other$remark = other.getRemark();
                                          return this$remark == null ? other$remark == null : this$remark.equals(other$remark);
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
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   protected boolean canEqual(final Object other) {
      return other instanceof PaymentsRecord;
   }

   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $payType = this.getPayType();
      result = result * 59 + ($payType == null ? 43 : $payType.hashCode());
      Object $id = this.getId();
      result = result * 59 + ($id == null ? 43 : $id.hashCode());
      Object $memberNo = this.getMemberNo();
      result = result * 59 + ($memberNo == null ? 43 : $memberNo.hashCode());
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
      Object $businessParams = this.getBusinessParams();
      result = result * 59 + ($businessParams == null ? 43 : $businessParams.hashCode());
      Object $memberBalanceAfter = this.getMemberBalanceAfter();
      result = result * 59 + ($memberBalanceAfter == null ? 43 : $memberBalanceAfter.hashCode());
      Object $createTime = this.getCreateTime();
      result = result * 59 + ($createTime == null ? 43 : $createTime.hashCode());
      Object $remark = this.getRemark();
      return result * 59 + ($remark == null ? 43 : $remark.hashCode());
   }

   @Override
   public String toString() {
      return "PaymentsRecord(id="
         + this.getId()
         + ", memberNo="
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
         + ", businessParams="
         + this.getBusinessParams()
         + ", payType="
         + this.getPayType()
         + ", memberBalanceAfter="
         + this.getMemberBalanceAfter()
         + ", createTime="
         + this.getCreateTime()
         + ", remark="
         + this.getRemark()
         + ")";
   }
}
