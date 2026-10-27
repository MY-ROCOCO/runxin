package com.rxinyun.common.enums;

public enum CertificationStatusEnum {
   PENDING("PENDING", "待审核"),
   APPROVED("APPROVED", "已认证"),
   REJECTED("REJECTED", "已拒绝");

   private final String key;
   private final String description;

   private CertificationStatusEnum(String key, String description) {
      this.key = key;
      this.description = description;
   }

   public String getKey() {
      return this.key;
   }

   public String getDescription() {
      return this.description;
   }
}
