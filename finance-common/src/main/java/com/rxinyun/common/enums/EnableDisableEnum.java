package com.rxinyun.common.enums;

public enum EnableDisableEnum {
   DISABLE(0, "禁用"),
   ENABLE(1, "启用");

   private final int code;
   private final String description;

   private EnableDisableEnum(int code, String description) {
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
