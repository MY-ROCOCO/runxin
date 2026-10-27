package com.rxinyun.member.tools.modular.file.enums;

public enum MemberFileEngineTypeEnum {
   LOCAL("LOCAL"),
   ALIYUN("ALIYUN"),
   TENCENT("TENCENT"),
   MINIO("MINIO");

   private final String value;

   private MemberFileEngineTypeEnum(String value) {
      this.value = value;
   }

   public String getValue() {
      return this.value;
   }
}
