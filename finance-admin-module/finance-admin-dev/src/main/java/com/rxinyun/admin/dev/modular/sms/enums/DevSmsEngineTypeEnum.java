package com.rxinyun.admin.dev.modular.sms.enums;

public enum DevSmsEngineTypeEnum {
   ALIYUN("ALIYUN"),
   TENCENT("TENCENT"),
   SMSBAO("SMSBAO"),
   SAIYOU("SAIYOU");

   private final String value;

   private DevSmsEngineTypeEnum(String value) {
      this.value = value;
   }

   public String getValue() {
      return this.value;
   }
}
