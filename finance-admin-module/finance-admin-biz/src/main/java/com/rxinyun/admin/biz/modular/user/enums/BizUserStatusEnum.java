package com.rxinyun.admin.biz.modular.user.enums;

import com.rxinyun.common.exception.CommonException;

public enum BizUserStatusEnum {
   ENABLE("ENABLE"),
   DISABLED("DISABLED");

   private final String value;

   private BizUserStatusEnum(String value) {
      this.value = value;
   }

   public static void validate(String value) {
      boolean flag = ENABLE.getValue().equals(value) || DISABLED.getValue().equals(value);
      if (!flag) {
         throw new CommonException("不支持的人员状态：{}", new Object[]{value});
      }
   }

   public String getValue() {
      return this.value;
   }
}
