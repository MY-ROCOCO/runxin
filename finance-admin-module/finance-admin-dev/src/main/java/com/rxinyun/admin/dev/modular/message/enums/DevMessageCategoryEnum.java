package com.rxinyun.admin.dev.modular.message.enums;

public enum DevMessageCategoryEnum {
   SYS("SYS"),
   BIZ("BIZ");

   private final String value;

   private DevMessageCategoryEnum(String value) {
      this.value = value;
   }

   public String getValue() {
      return this.value;
   }
}
