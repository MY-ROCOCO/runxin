package com.rxinyun.member.auth.modular.user.enums;

import com.rxinyun.common.exception.CommonException;

public enum ClientUserStatusEnum {
   ENABLE("ENABLE"),
   DISABLED("DISABLED");

   private final String value;

   private ClientUserStatusEnum(String value) {
      this.value = value;
   }

   public static void validate(String value) {
      boolean flag = ENABLE.getValue().equals(value) || DISABLED.getValue().equals(value);
      if (!flag) {
         throw new CommonException("不支持的C端用户状态：{}", new Object[]{value});
      }
   }

   public String getValue() {
      return this.value;
   }
}
