package com.rxinyun.admin.dev.modular.email.enums;

public enum DevEmailEngineTypeEnum {
   LOCAL("LOCAL");

   private final String value;

   private DevEmailEngineTypeEnum(String value) {
      this.value = value;
   }

   public String getValue() {
      return this.value;
   }
}
