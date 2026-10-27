package com.rxinyun.common.enums;

public enum PlatformEnum {
   OA("OA", "公众号"),
   PC("PC", "电脑端"),
   H5("H5", "手机端"),
   MP("MP", "小程序"),
   APP("APP", "APP");

   private final String code;
   private final String description;

   private PlatformEnum(String code, String description) {
      this.code = code;
      this.description = description;
   }

   public String getCode() {
      return this.code;
   }

   public String getDescription() {
      return this.description;
   }

   public static PlatformEnum fromCode(String code) {
      for (PlatformEnum type : values()) {
         if (type.getCode().equals(code)) {
            return type;
         }
      }

      throw new IllegalArgumentException("Invalid order type code: " + code);
   }
}
