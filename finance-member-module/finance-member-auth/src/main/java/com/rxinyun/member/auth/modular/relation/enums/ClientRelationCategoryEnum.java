package com.rxinyun.member.auth.modular.relation.enums;

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
