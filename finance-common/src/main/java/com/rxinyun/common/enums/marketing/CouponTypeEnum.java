package com.rxinyun.common.enums.marketing;

public enum CouponTypeEnum {
   CASH(1, "固定金额券"),
   DISCOUNT(2, "折扣券"),
   FULL_REDUCTION(3, "满减券");

   private final int code;
   private final String description;

   private CouponTypeEnum(int code, String description) {
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
