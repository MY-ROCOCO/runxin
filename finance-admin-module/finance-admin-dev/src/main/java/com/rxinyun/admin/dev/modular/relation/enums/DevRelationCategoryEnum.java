package com.rxinyun.admin.dev.modular.relation.enums;

public enum DevRelationCategoryEnum {
   FILE_TO_BIZ_DEFAULT("FILE_TO_BIZ_DEFAULT"),
   MSG_TO_USER("MSG_TO_USER");

   private final String value;

   private DevRelationCategoryEnum(String value) {
      this.value = value;
   }

   public String getValue() {
      return this.value;
   }
}
