package com.rxinyun.admin.system.modular.resource.enums;

import com.rxinyun.common.exception.CommonException;

public enum SysResourceMenuTypeEnum {
   CATALOG("CATALOG"),
   MENU("MENU"),
   IFRAME("IFRAME"),
   LINK("LINK");

   private final String value;

   private SysResourceMenuTypeEnum(String value) {
      this.value = value;
   }

   public static void validate(String value) {
      boolean flag = CATALOG.getValue().equals(value) || MENU.getValue().equals(value) || IFRAME.getValue().equals(value) || LINK.getValue().equals(value);
      if (!flag) {
         throw new CommonException("不支持的菜单类型：{}", new Object[]{value});
      }
   }

   public String getValue() {
      return this.value;
   }
}
