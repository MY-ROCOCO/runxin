package com.rxinyun.common.enums.marketing;

public enum CouponOrderRelationStatusEnum {
   LOCKED(1, "已锁定"),
   USED(2, "已使用"),
   RELEASED(3, "已释放"),
   REFUNDED(4, "已退回");

   private final int code;
   private final String description;

   private CouponOrderRelationStatusEnum(int code, String description) {
      this.code = code;
      this.description = description;
   }

   public int getCode() {
      return this.code;
   }

   public String getDescription() {
      return this.description;
   }
}
