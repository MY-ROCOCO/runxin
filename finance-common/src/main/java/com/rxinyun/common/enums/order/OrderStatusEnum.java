package com.rxinyun.common.enums.order;

public enum OrderStatusEnum {
   UNPAID(0, "未支付"),
   PAID(1, "已支付"),
   CANCELLED(-1, "已取消"),
   REFUNDED(-2, "已退款"),
   REFUNDING(-3, "退款中");

   private final Integer code;
   private final String message;

   private OrderStatusEnum(Integer code, String message) {
      this.code = code;
      this.message = message;
   }

   public Integer getCode() {
      return this.code;
   }

   public String getMessage() {
      return this.message;
   }
}
