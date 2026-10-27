package com.rxinyun.common.enums.order;

import com.rxinyun.common.exception.CommonException;

public enum ServerRefundModeEnum {
   FULL,
   PRORATED;

   public static ServerRefundModeEnum fromParam(String raw) {
      if (raw != null && !raw.isBlank()) {
         String s = raw.trim().toUpperCase();
         if ("FULL".equals(s)) {
            return FULL;
         } else if (!"PRORATED".equals(s) && !"BY_DAY".equals(s)) {
            throw new CommonException("不支持的 refundMode: " + raw + "，请使用 FULL 或 PRORATED（BY_DAY）");
         } else {
            return PRORATED;
         }
      } else {
         throw new CommonException("refundMode 不能为空");
      }
   }
}
