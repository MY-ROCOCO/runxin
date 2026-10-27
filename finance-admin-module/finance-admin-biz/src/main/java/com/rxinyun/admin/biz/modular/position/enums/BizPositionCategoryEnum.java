package com.rxinyun.admin.biz.modular.position.enums;

import com.rxinyun.common.exception.CommonException;

public enum BizPositionCategoryEnum {
   HIGH("HIGH"),
   MIDDLE("MIDDLE"),
   LOW("LOW");

   private final String value;

   private BizPositionCategoryEnum(String value) {
      this.value = value;
   }

   public static void validate(String value) {
      boolean flag = HIGH.getValue().equals(value) || MIDDLE.getValue().equals(value) || LOW.getValue().equals(value);
      if (!flag) {
         throw new CommonException("不支持的岗位分类：{}", new Object[]{value});
      }
   }

   public String getValue() {
      return this.value;
   }
}
