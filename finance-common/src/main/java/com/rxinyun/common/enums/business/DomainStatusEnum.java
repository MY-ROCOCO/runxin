package com.rxinyun.common.enums.business;

public enum DomainStatusEnum {
   NORMAL(1, "正常"),
   EXPIRING(0, "即将到期"),
   EXPIRED(-1, "已过期"),
   LOCKED(2, "锁定");

   private final Integer code;
   private final String description;

   private DomainStatusEnum(Integer code, String description) {
      this.code = code;
      this.description = description;
   }

   public Integer getCode() {
      return this.code;
   }

   public String getDescription() {
      return this.description;
   }

   public static DomainStatusEnum getByCode(Integer code) {
      if (code == null) {
         return null;
      } else {
         for (DomainStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
               return status;
            }
         }

         return null;
      }
   }
}
