package com.rxinyun.admin.biz.modular.notice.enums;

import com.rxinyun.common.exception.CommonException;

public enum BizNoticeStatusEnum {
   ENABLE("ENABLE"),
   DISABLE("DISABLE");

   private final String value;

   private BizNoticeStatusEnum(String value) {
      this.value = value;
   }

   public static void validate(String value) {
      boolean flag = ENABLE.getValue().equals(value) || DISABLE.getValue().equals(value);
      if (!flag) {
         throw new CommonException("不支持的状态：{}", new Object[]{value});
      }
   }

   public String getValue() {
      return this.value;
   }
}
