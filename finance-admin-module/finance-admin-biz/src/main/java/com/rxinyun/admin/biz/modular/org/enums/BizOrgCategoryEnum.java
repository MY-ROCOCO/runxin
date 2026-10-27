package com.rxinyun.admin.biz.modular.org.enums;

import com.rxinyun.common.exception.CommonException;

public enum BizOrgCategoryEnum {
   COMPANY("COMPANY"),
   DEPT("DEPT");

   private final String value;

   private BizOrgCategoryEnum(String value) {
      this.value = value;
   }

   public static void validate(String value) {
      boolean flag = COMPANY.getValue().equals(value) || DEPT.getValue().equals(value);
      if (!flag) {
         throw new CommonException("不支持的机构分类：{}", new Object[]{value});
      }
   }

   public String getValue() {
      return this.value;
   }
}
