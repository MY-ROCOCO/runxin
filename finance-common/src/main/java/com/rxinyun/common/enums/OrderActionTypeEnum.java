package com.rxinyun.common.enums;

public enum OrderActionTypeEnum {
   BUY(1, "购买"),
   RENEW(2, "续费"),
   UPGRADE(3, "升级");

   private final int code;
   private final String description;

   private OrderActionTypeEnum(int code, String description) {
      this.code = code;
      this.description = description;
   }

   public int getCode() {
      return this.code;
   }

   public String getDescription() {
      return this.description;
   }
}
