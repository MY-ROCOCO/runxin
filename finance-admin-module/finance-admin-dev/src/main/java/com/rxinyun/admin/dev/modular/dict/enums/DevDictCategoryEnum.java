package com.rxinyun.admin.dev.modular.dict.enums;

import com.rxinyun.common.exception.CommonException;

public enum DevDictCategoryEnum {
   FRM("FRM"),
   BIZ("BIZ");

   private final String value;

   private DevDictCategoryEnum(String value) {
      this.value = value;
   }

   public static void validate(String value) {
      boolean flag = FRM.getValue().equals(value) || BIZ.getValue().equals(value);
      if (!flag) {
         throw new CommonException("不支持的字典分类：{}", new Object[]{value});
      }
   }

   public String getValue() {
      return this.value;
   }
}
