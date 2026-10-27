package com.rxinyun.admin.dev.modular.log.enums;

public enum DevLogCategoryEnum {
   OPERATE("OPERATE"),
   EXCEPTION("EXCEPTION"),
   LOGIN("LOGIN"),
   LOGOUT("LOGOUT");

   private final String value;

   private DevLogCategoryEnum(String value) {
      this.value = value;
   }

   public String getValue() {
      return this.value;
   }
}
