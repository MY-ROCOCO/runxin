package com.rxinyun.common.util.date.enums;

public enum DateUtilParamName {
   LAST_DAY("昨天"),
   TO_DAY("今天"),
   TO_WEEK("本周"),
   LAST_WEEK("上周"),
   TO_MONTH("本月"),
   LAST_MONTH("上月"),
   ALLTIME("全部");

   private final String value;

   private DateUtilParamName(String value) {
      this.value = value;
   }

   public String getValue() {
      return this.value;
   }

   public static DateUtilParamName fromValue(String value) {
      for (DateUtilParamName dateUtilParamName : values()) {
         if (dateUtilParamName.getValue().equals(value)) {
            return dateUtilParamName;
         }
      }

      throw new IllegalArgumentException("Unknown value: " + value);
   }
}
