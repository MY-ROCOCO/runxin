package com.rxinyun.admin.dev.modular.message.enums;

public enum NotificationSendStatusEnum {
   PENDING("PENDING"),
   SENDING("SENDING"),
   SUCCESS("SUCCESS"),
   FAILED("FAILED");

   private final String value;

   private NotificationSendStatusEnum(String value) {
      this.value = value;
   }

   public String getValue() {
      return this.value;
   }
}
