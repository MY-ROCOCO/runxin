package com.rxinyun.admin.member.modular.relation.enums;

public enum ClientRelationCategoryEnum {
   USER_TEST("USER_TEST");

   private final String value;

   private ClientRelationCategoryEnum(String value) {
      this.value = value;
   }

   public String getValue() {
      return this.value;
   }
}
