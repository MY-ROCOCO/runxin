package com.rxinyun.common.enums;

public enum UserTypeEnum {
   ENTERPRISE("ENTERPRISE", "企业"),
   PERSONAL("PERSONAL", "个人");

   private final String key;
   private final String description;

   private UserTypeEnum(String key, String description) {
      this.key = key;
      this.description = description;
   }

   public String getKey() {
      return this.key;
   }

   public String getDescription() {
      return this.description;
   }
}
