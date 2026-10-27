package com.rxinyun.admin.system.core.enums;

public enum SysDataTypeEnum {
   ORG("ORG"),
   POSITION("POSITION"),
   RESOURCE("RESOURCE"),
   ROLE("ROLE"),
   USER("USER");

   private final String value;

   private SysDataTypeEnum(String value) {
      this.value = value;
   }

   public String getValue() {
      return this.value;
   }
}
