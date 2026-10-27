package com.rxinyun.admin.dev.modular.file.enums;

public enum DevFileEngineTypeEnum {
   LOCAL("LOCAL"),
   ALIYUN("ALIYUN"),
   TENCENT("TENCENT"),
   MINIO("MINIO");

   private final String value;

   private DevFileEngineTypeEnum(String value) {
      this.value = value;
   }

   public String getValue() {
      return this.value;
   }
}
