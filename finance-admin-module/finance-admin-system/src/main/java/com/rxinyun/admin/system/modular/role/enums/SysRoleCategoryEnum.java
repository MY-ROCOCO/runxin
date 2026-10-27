package com.rxinyun.admin.system.modular.role.enums;

import com.rxinyun.common.exception.CommonException;

public enum SysRoleCategoryEnum {
   GLOBAL("GLOBAL");

   private final String value;

   private SysRoleCategoryEnum(String value) {
      this.value = value;
   }

   public static void validate(String value) {
      if (!GLOBAL.getValue().equals(value)) {
         throw new CommonException("不支持的角色分类：{}", new Object[]{value});
      }
   }

   public String getValue() {
      return this.value;
   }
}
