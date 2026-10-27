package com.rxinyun.admin.dev.modular.slideshow.enums;

import com.rxinyun.common.exception.CommonException;

public enum DevSlideshowStatusEnum {
   ENABLE("ENABLE"),
   DISABLE("DISABLE");

   private final String value;

   private DevSlideshowStatusEnum(String value) {
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
