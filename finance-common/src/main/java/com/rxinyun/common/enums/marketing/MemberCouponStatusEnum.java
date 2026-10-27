package com.rxinyun.common.enums.marketing;

public enum MemberCouponStatusEnum {
   UNUSED(0, "未使用"),
   LOCKED(1, "已锁定"),
   USED_UP(2, "已用尽"),
   EXPIRED(3, "已过期"),
   DISABLED(4, "已禁用");

   private final int code;
   private final String description;

   private MemberCouponStatusEnum(int code, String description) {
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
