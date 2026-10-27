package com.rxinyun.admin.dev.modular.job.enums;

import com.rxinyun.common.exception.CommonException;

public enum DevJobCategoryEnum {
   FRM("FRM"),
   BIZ("BIZ");

   private final String value;

   private DevJobCategoryEnum(String value) {
      this.value = value;
   }

   public static void validate(String value) {
      boolean flag = FRM.getValue().equals(value) || BIZ.getValue().equals(value);
      if (!flag) {
         throw new CommonException("不支持的定时任务分类：{}", new Object[]{value});
      }
   }

   public String getValue() {
      return this.value;
   }
}
