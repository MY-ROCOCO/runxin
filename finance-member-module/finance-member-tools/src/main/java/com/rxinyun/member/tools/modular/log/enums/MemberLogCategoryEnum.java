package com.rxinyun.member.tools.modular.log.enums;

public enum MemberLogCategoryEnum {
   OPERATE("OPERATE"),
   EXCEPTION("EXCEPTION"),
   LOGIN("LOGIN"),
   LOGOUT("LOGOUT");

   private final String value;

   private MemberLogCategoryEnum(String value) {
      this.value = value;
   }

   public String getValue() {
      return this.value;
   }
}
