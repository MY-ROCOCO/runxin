package com.rxinyun.admin.dev.modular.log.enums;

public enum DevLogExeStatusEnum {
   SUCCESS("SUCCESS"),
   FAIL("FAIL");

   private final String value;

   private DevLogExeStatusEnum(String value) {
      this.value = value;
   }

   public String getValue() {
      return this.value;
   }
}
