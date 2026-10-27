package com.rxinyun.common.enums.business;

public enum NetworkStatusEnum {
   OCCUPIED(1, "已占用"),
   UNOCCUPIED(0, "未占用");

   private final int code;
   private final String description;

   private NetworkStatusEnum(int code, String description) {
      this.code = code;
      this.description = description;
   }

   public int getCode() {
      return this.code;
   }

   public String getDescription() {
      return this.description;
   }

   public static NetworkStatusEnum getByCode(int code) {
      for (NetworkStatusEnum status : values()) {
         if (status.getCode() == code) {
            return status;
         }
      }

      return null;
   }
}
