package com.rxinyun.common.enums.business;

public enum PayTypeEnum {
   WECHAT(1, "微信"),
   ALIPAY(2, "支付宝"),
   INTEGRAL(3, "积分"),
   BALANCE(4, "余额"),
   USDT(5, "USDT");

   private final int code;
   private final String description;

   private PayTypeEnum(int code, String description) {
      this.code = code;
      this.description = description;
   }

   public int getCode() {
      return this.code;
   }

   public String getDescription() {
      return this.description;
   }

   public static PayTypeEnum fromCode(int code) {
      for (PayTypeEnum type : values()) {
         if (type.getCode() == code) {
            return type;
         }
      }

      throw new IllegalArgumentException("Invalid order type code: " + code);
   }
}
