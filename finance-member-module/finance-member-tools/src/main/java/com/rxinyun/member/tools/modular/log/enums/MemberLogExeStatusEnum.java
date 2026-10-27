package com.rxinyun.member.tools.modular.log.enums;

public enum MemberLogExeStatusEnum {
   SUCCESS("SUCCESS"),
   FAIL("FAIL");

   private final String value;

   private MemberLogExeStatusEnum(String value) {
      this.value = value;
   }

   public String getValue() {
      return this.value;
   }
}
