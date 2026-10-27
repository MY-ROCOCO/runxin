package com.rxinyun.member.core.enums;

import com.rxinyun.common.exception.CommonException;

public enum SaClientTypeEnum {
   B("B"),
   C("C");

   private final String value;

   private SaClientTypeEnum(String value) {
      this.value = value;
   }

   public static void validate(String value) {
      boolean flag = B.getValue().equals(value) || C.getValue().equals(value);
      if (!flag) {
         throw new CommonException("不支持的登录端类型：{}", new Object[]{value});
      }
   }

   public String getValue() {
      return this.value;
   }
}
