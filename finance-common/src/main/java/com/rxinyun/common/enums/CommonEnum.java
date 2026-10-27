package com.rxinyun.common.enums;

public enum CommonEnum {
   NO(0, "否"),
   YES(1, "是");

   private final int code;
   private final String description;

   private CommonEnum(int code, String description) {
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
