package com.rxinyun.admin.biz.modular.user.enums;

import com.rxinyun.common.exception.CommonException;

public enum BizRoleCategoryEnum {
   GLOBAL("GLOBAL"),
   ORG("ORG");

   private final String value;

   private BizRoleCategoryEnum(String value) {
      this.value = value;
   }

   public static void validate(String value) {
      boolean flag = GLOBAL.getValue().equals(value) || ORG.getValue().equals(value);
      if (!flag) {
         throw new CommonException("不支持的角色分类：{}", new Object[]{value});
      }
   }

   public String getValue() {
      return this.value;
   }
}
