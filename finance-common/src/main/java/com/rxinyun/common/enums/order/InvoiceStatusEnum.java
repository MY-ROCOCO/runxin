package com.rxinyun.common.enums.order;

public enum InvoiceStatusEnum {
   PENDING(0, "待审核"),
   ISSUED(1, "已开具"),
   REJECTED(-1, "已拒绝");

   private final Integer code;
   private final String message;

   private InvoiceStatusEnum(Integer code, String message) {
      this.code = code;
      this.message = message;
   }

   public Integer getCode() {
      return this.code;
   }

   public String getMessage() {
      return this.message;
   }

   public static boolean isOccupied(Integer status) {
      return PENDING.getCode().equals(status) || ISSUED.getCode().equals(status);
   }
}
