package com.rxinyun.admin.system.core.enums;

public enum SysBuildInEnum {
   BUILD_IN_USER_ACCOUNT("superAdmin", "超管"),
   BUILD_IN_ROLE_CODE("superAdmin", "超管"),
   BUILD_IN_MODULE_CODE("system", "系统内置");

   private final String value;
   private final String name;

   private SysBuildInEnum(String value, String name) {
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
