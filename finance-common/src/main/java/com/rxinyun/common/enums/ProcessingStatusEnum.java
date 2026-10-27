package com.rxinyun.common.enums;

public enum ProcessingStatusEnum {
   UNPROCESSED(0, "未处理"),
   PROCESSING(1, "处理中"),
   PROCESSING_COMPLETE(2, "处理完成"),
   PROCESSING_FAILED(3, "处理失败");

   private final int code;
   private final String description;

   private ProcessingStatusEnum(int code, String description) {
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
