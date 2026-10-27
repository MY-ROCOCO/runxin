package com.rxinyun.common.util.date.enums;

public enum DateResponseKeyEnum {
   START_TIME_NAME("start"),
   END_TIME_NAME("end");

   private final String value;

   private DateResponseKeyEnum(String value) {
      this.value = value;
   }

   public String getValue() {
      return this.value;
   }
}
