package com.rxinyun.marketing.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class CouponLockRequest extends CouponCalculateRequest {
   @Schema(
      description = "订单编号"
   )
   private String orderNo;

   public String getOrderNo() {
      return this.orderNo;
   }

   public void setOrderNo(final String orderNo) {
      this.orderNo = orderNo;
   }
}
