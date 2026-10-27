package com.rxinyun.admin.system.modular.relation.enums;

public enum SysRelationCategoryEnum {
   SYS_USER_WORKBENCH_DATA("SYS_USER_WORKBENCH_DATA"),
   SYS_USER_SCHEDULE_DATA("SYS_USER_SCHEDULE_DATA"),
   SYS_USER_HAS_RESOURCE("SYS_USER_HAS_RESOURCE"),
   SYS_USER_HAS_PERMISSION("SYS_USER_HAS_PERMISSION"),
   SYS_USER_HAS_ROLE("SYS_USER_HAS_ROLE"),
   SYS_ROLE_HAS_RESOURCE("SYS_ROLE_HAS_RESOURCE"),
   SYS_ROLE_HAS_MOBILE_MENU("SYS_ROLE_HAS_MOBILE_MENU"),
   SYS_ROLE_HAS_PERMISSION("SYS_ROLE_HAS_PERMISSION");

   private final String value;

   private SysRelationCategoryEnum(String value) {
      this.value = value;
   }

   public String getValue() {
      return this.value;
   }
}
