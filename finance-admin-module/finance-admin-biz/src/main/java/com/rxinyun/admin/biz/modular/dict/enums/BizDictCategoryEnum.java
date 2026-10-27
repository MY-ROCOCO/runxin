package com.rxinyun.admin.biz.modular.dict.enums;

import com.rxinyun.common.exception.CommonException;

public enum BizDictCategoryEnum {
   BIZ("BIZ");

   private final String value;

   private BizDictCategoryEnum(String value) {
      this.value = value;
   }

   public static void validate(String value) {
      boolean flag = BIZ.getValue().equals(value);
      if (!flag) {
         throw new CommonException("不支持的字典分类：{}", new Object[]{value});
      }
   }

   public String getValue() {
      return this.value;
   }
}
