package com.rxinyun.admin.system.modular.org.enums;

import com.rxinyun.common.exception.CommonException;

public enum SysOrgCategoryEnum {
   COMPANY("COMPANY"),
   DEPT("DEPT");

   private final String value;

   private SysOrgCategoryEnum(String value) {
      this.value = value;
   }

   public static void validate(String value) {
      boolean flag = COMPANY.getValue().equals(value) || DEPT.getValue().equals(value);
      if (!flag) {
         throw new CommonException("不支持的组织分类：{}", new Object[]{value});
      }
   }

   public String getValue() {
      return this.value;
   }
}
