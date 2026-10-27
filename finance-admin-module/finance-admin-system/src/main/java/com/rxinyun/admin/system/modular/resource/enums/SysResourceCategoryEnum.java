package com.rxinyun.admin.system.modular.resource.enums;

import com.rxinyun.common.exception.CommonException;

public enum SysResourceCategoryEnum {
   MODULE("MODULE"),
   MENU("MENU"),
   BUTTON("BUTTON");

   private final String value;

   private SysResourceCategoryEnum(String value) {
      this.value = value;
   }

   public static void validate(String value) {
      boolean flag = MODULE.getValue().equals(value) || MENU.getValue().equals(value) || BUTTON.getValue().equals(value);
      if (!flag) {
         throw new CommonException("不支持的资源分类：{}", new Object[]{value});
      }
   }

   public String getValue() {
      return this.value;
   }
}
