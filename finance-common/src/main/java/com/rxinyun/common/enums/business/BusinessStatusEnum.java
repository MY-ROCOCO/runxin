package com.rxinyun.common.enums.business;

public enum BusinessStatusEnum {
   NORMAL(1, "正常"),
   EXPIRED(-1, "过期"),
   DELETE(-2, "删除");

   private final Integer code;
   private final String description;

   private BusinessStatusEnum(Integer code, String description) {
      this.code = code;
      this.description = description;
   }

   public Integer getCode() {
      return this.code;
   }

   public String getDescription() {
      return this.description;
   }

   public static BusinessStatusEnum getByCode(Integer code) {
      for (BusinessStatusEnum status : values()) {
         if (status.getCode() == code) {
            return status;
         }
      }

      return null;
   }
}
