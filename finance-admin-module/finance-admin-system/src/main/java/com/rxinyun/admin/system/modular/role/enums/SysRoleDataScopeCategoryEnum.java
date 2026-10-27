package com.rxinyun.admin.system.modular.role.enums;

import com.rxinyun.common.exception.CommonException;

public enum SysRoleDataScopeCategoryEnum {
   SCOPE_ALL("SCOPE_ALL"),
   SCOPE_SELF("SCOPE_SELF"),
   SCOPE_ORG("SCOPE_ORG"),
   SCOPE_ORG_CHILD("SCOPE_ORG_CHILD"),
   SCOPE_ORG_DEFINE("SCOPE_ORG_DEFINE");

   private final String value;

   private SysRoleDataScopeCategoryEnum(String value) {
      this.value = value;
   }

   public static void validate(String value) {
      boolean flag = SCOPE_ALL.getValue().equals(value)
         || SCOPE_SELF.getValue().equals(value)
         || SCOPE_ORG.getValue().equals(value)
         || SCOPE_ORG_CHILD.getValue().equals(value)
         || SCOPE_ORG_DEFINE.getValue().equals(value);
      if (!flag) {
         throw new CommonException("不支持的数据范围：{}", new Object[]{value});
      }
   }

   public String getValue() {
      return this.value;
   }
}
