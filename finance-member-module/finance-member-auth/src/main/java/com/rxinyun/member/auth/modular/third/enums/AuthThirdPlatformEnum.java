package com.rxinyun.member.auth.modular.third.enums;

import com.rxinyun.common.exception.CommonException;

public enum AuthThirdPlatformEnum {
   GITEE("GITEE"),
   WECHAT("WECHAT");

   private final String value;

   private AuthThirdPlatformEnum(String value) {
      this.value = value;
   }

   public static void validate(String value) {
      boolean flag = GITEE.getValue().equals(value) || WECHAT.getValue().equals(value);
      if (!flag) {
         throw new CommonException("不支持的第三方平台：{}", new Object[]{value});
      }
   }

   public String getValue() {
      return this.value;
   }
}
