package com.rxinyun.member.auth.modular.login.enums;

import com.rxinyun.common.exception.CommonException;

public enum AuthDeviceTypeEnum {
   PC("PC"),
   APP("APP"),
   MINI("MINI");

   private final String value;

   private AuthDeviceTypeEnum(String value) {
      this.value = value;
   }

   public static void validate(String value) {
      boolean flag = PC.getValue().equals(value) || APP.getValue().equals(value) || MINI.getValue().equals(value);
      if (!flag) {
         throw new CommonException("不支持的登录设备类型：{}", new Object[]{value});
      }
   }

   public String getValue() {
      return this.value;
   }
}
