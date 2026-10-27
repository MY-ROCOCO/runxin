package com.rxinyun.common.enums;

import com.rxinyun.common.exception.CommonException;

public enum CommonSortOrderEnum {
   ASC("ASCEND"),
   DESC("DESCEND");

   private final String value;

   private CommonSortOrderEnum(String value) {
      this.value = value.toUpperCase();
   }

   public static void validate(String value) {
      boolean flag = value != null && (value.equalsIgnoreCase(ASC.getValue()) || value.equalsIgnoreCase(DESC.getValue()));
      if (!flag) {
         throw new CommonException("不支持该排序方式：{}", value);
      }
   }

   public String getValue() {
      return this.value;
   }
}
