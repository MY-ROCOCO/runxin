package com.rxinyun.common.enums.order;

public enum OrderTypeEnum {
   NEW_PURCHASE(1, "新购"),
   UPGRADE(2, "升级"),
   RENEW(3, "续费"),
   GROUP_BUY(4, "团购"),
   PRE_ORDER(5, "预订");

   private final int code;
   private final String description;

   private OrderTypeEnum(int code, String description) {
      this.code = code;
      this.description = description;
   }

   public int getCode() {
      return this.code;
   }

   public String getDescription() {
      return this.description;
   }

   public static String getDescriptionByCode(Integer code) {
      for (OrderTypeEnum orderTypeEnum : values()) {
         if (orderTypeEnum.getCode() == code) {
            return orderTypeEnum.getDescription();
         }
      }

      return null;
   }
}
