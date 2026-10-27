package com.rxinyun.common.enums;

public enum ResourcePoolRequestStatusEnum {
   PENDING(0, "待发送"),
   SENT(1, "已发送"),
   SUCCESS(2, "成功"),
   FAILED(3, "失败");

   private final Integer code;
   private final String description;

   private ResourcePoolRequestStatusEnum(Integer code, String description) {
      this.code = code;
      this.description = description;
   }

   public Integer getCode() {
      return this.code;
   }

   public String getDescription() {
      return this.description;
   }

   public static ResourcePoolRequestStatusEnum getByCode(Integer code) {
      for (ResourcePoolRequestStatusEnum status : values()) {
         if (status.getCode().equals(code)) {
            return status;
         }
      }

      return null;
   }
}
