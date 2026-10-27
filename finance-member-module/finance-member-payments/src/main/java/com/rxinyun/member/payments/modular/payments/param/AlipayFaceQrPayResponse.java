package com.rxinyun.member.payments.modular.payments.param;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
   description = "支付宝当面付扫码支付信息"
)
public class AlipayFaceQrPayResponse {
   @Schema(
      description = "业务订单号或充值单号"
   )
   private String orderNo;
   @Schema(
      description = "支付宝商户订单号"
   )
   private String outTradeNo;
   @Schema(
      description = "支付方式，支付宝固定为 2"
   )
   private Integer payType;
   @Schema(
      description = "实际支付路由方式：FACE_QR"
   )
   private String payMode;
   @Schema(
      description = "支付宝返回的二维码内容"
   )
   private String qrCode;
   @Schema(
      description = "支付超时时间表达式，如 30m"
   )
   private String expireTime;

   public String getOrderNo() {
      return this.orderNo;
   }

   public String getOutTradeNo() {
      return this.outTradeNo;
   }

   public Integer getPayType() {
      return this.payType;
   }

   public String getPayMode() {
      return this.payMode;
   }

   public String getQrCode() {
      return this.qrCode;
   }

   public String getExpireTime() {
      return this.expireTime;
   }

   public void setOrderNo(final String orderNo) {
      this.orderNo = orderNo;
   }

   public void setOutTradeNo(final String outTradeNo) {
      this.outTradeNo = outTradeNo;
   }

   public void setPayType(final Integer payType) {
      this.payType = payType;
   }

   public void setPayMode(final String payMode) {
      this.payMode = payMode;
   }

   public void setQrCode(final String qrCode) {
      this.qrCode = qrCode;
   }

   public void setExpireTime(final String expireTime) {
      this.expireTime = expireTime;
   }

   @Override
   public boolean equals(final Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof AlipayFaceQrPayResponse other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else {
         Object this$payType = this.getPayType();
         Object other$payType = other.getPayType();
         if (this$payType == null ? other$payType == null : this$payType.equals(other$payType)) {
            Object this$orderNo = this.getOrderNo();
            Object other$orderNo = other.getOrderNo();
            if (this$orderNo == null ? other$orderNo == null : this$orderNo.equals(other$orderNo)) {
               Object this$outTradeNo = this.getOutTradeNo();
               Object other$outTradeNo = other.getOutTradeNo();
               if (this$outTradeNo == null ? other$outTradeNo == null : this$outTradeNo.equals(other$outTradeNo)) {
                  Object this$payMode = this.getPayMode();
                  Object other$payMode = other.getPayMode();
                  if (this$payMode == null ? other$payMode == null : this$payMode.equals(other$payMode)) {
                     Object this$qrCode = this.getQrCode();
                     Object other$qrCode = other.getQrCode();
                     if (this$qrCode == null ? other$qrCode == null : this$qrCode.equals(other$qrCode)) {
                        Object this$expireTime = this.getExpireTime();
                        Object other$expireTime = other.getExpireTime();
                        return this$expireTime == null ? other$expireTime == null : this$expireTime.equals(other$expireTime);
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
      return other instanceof AlipayFaceQrPayResponse;
   }

   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $payType = this.getPayType();
      result = result * 59 + ($payType == null ? 43 : $payType.hashCode());
      Object $orderNo = this.getOrderNo();
      result = result * 59 + ($orderNo == null ? 43 : $orderNo.hashCode());
      Object $outTradeNo = this.getOutTradeNo();
      result = result * 59 + ($outTradeNo == null ? 43 : $outTradeNo.hashCode());
      Object $payMode = this.getPayMode();
      result = result * 59 + ($payMode == null ? 43 : $payMode.hashCode());
      Object $qrCode = this.getQrCode();
      result = result * 59 + ($qrCode == null ? 43 : $qrCode.hashCode());
      Object $expireTime = this.getExpireTime();
      return result * 59 + ($expireTime == null ? 43 : $expireTime.hashCode());
   }

   @Override
   public String toString() {
      return "AlipayFaceQrPayResponse(orderNo="
         + this.getOrderNo()
         + ", outTradeNo="
         + this.getOutTradeNo()
         + ", payType="
         + this.getPayType()
         + ", payMode="
         + this.getPayMode()
         + ", qrCode="
         + this.getQrCode()
         + ", expireTime="
         + this.getExpireTime()
         + ")";
   }
}
