package com.rxinyun.admin.biz.core.enums;

public enum BizBuildInEnum {
   BUILD_IN_USER_ACCOUNT("superAdmin", "超管");

   private final String value;
   private final String name;

   private BizBuildInEnum(String value, String name) {
      this.value = value;
      this.name = name;
   }

   public String getValue() {
      return this.value;
   }

   public String getName() {
      return this.name;
   }
}
