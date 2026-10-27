package com.rxinyun.admin.biz.core.enums;

public enum BizDataTypeEnum {
   ORG("ORG"),
   POSITION("POSITION"),
   USER("USER");

   private final String value;

   private BizDataTypeEnum(String value) {
      this.value = value;
   }

   public String getValue() {
      return this.value;
   }
}
