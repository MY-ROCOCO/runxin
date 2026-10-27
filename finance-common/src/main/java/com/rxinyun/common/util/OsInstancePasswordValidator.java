package com.rxinyun.common.util;

import com.rxinyun.common.exception.CommonException;
import java.util.Locale;

public final class OsInstancePasswordValidator {
   private static final int MIN_LENGTH = 14;
   private static final int MAX_CONSECUTIVE_SAME = 6;

   private OsInstancePasswordValidator() {
   }

   public static void validateOrThrow(String password) {
      String msg = validate(password);
      if (msg != null) {
         throw new CommonException(msg);
      }
   }

   public static String validate(String password) {
      if (password != null && !password.isEmpty()) {
         int[] cps = password.codePoints().toArray();
         if (cps.length < 14) {
            return "系统密码长度不能少于14位";
         } else {
            boolean upper = false;
            boolean lower = false;
            boolean digit = false;
            boolean special = false;

            for (int cp : cps) {
               if (cp < 33 || cp > 126) {
                  return "系统密码仅允许半角英文字母、数字与英文符号，禁止中文、全角字符、空格、表情符号及 \\ / : * ? \" < > |";
               }

               char c = (char)cp;
               if (isForbiddenChar(c)) {
                  return "系统密码不能包含空格及字符 \\ / : * ? \" < > |";
               }

               if (c >= 'A' && c <= 'Z') {
                  upper = true;
               } else if (c >= 'a' && c <= 'z') {
                  lower = true;
               } else if (c >= '0' && c <= '9') {
                  digit = true;
               } else {
                  special = true;
               }
            }

            if (!upper) {
               return "系统密码须包含大写英文字母";
            } else if (!lower) {
               return "系统密码须包含小写英文字母";
            } else if (!digit) {
               return "系统密码须包含数字";
            } else if (!special) {
               return "系统密码须包含特殊符号（英文标点，且不能为 \\ / : * ? \" < > |）";
            } else {
               int run = 1;

               for (int i = 1; i < cps.length; i++) {
                  if (cps[i] == cps[i - 1]) {
                     if (++run >= 6) {
                        return "系统密码不能包含连续6个及以上相同字符";
                     }
                  } else {
                     run = 1;
                  }
               }

               String low = password.toLowerCase(Locale.ROOT);
               if (low.contains("123456")) {
                  return "系统密码不能包含弱序列123456";
               } else if (low.contains("abcdef")) {
                  return "系统密码不能包含弱序列abcdef";
               } else {
                  return low.contains("password") ? "系统密码不能包含弱口令password" : null;
               }
            }
         }
      } else {
         return "系统密码不能为空";
      }
   }

   private static boolean isForbiddenChar(char c) {
      if (c == ' ') {
         return true;
      } else {
         return switch (c) {
            case '"', '*', '/', ':', '<', '>', '?', '\\', '|' -> true;
            default -> false;
         };
      }
   }
}
