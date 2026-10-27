package com.rxinyun.admin.dev.modular.email.enums;

public enum DevEmailTypeEnum {
   TEXT("TEXT"),
   HTML("HTML"),
   TEMPLATE("TEMPLATE");

   private final String value;

   private DevEmailTypeEnum(String value) {
      this.value = value;
   }

   public String getValue() {
      return this.value;
   }
}
