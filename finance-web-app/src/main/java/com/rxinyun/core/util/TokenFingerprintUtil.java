package com.rxinyun.core.util;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpLogic;
import cn.hutool.core.util.StrUtil;
import com.rxinyun.common.util.CommonServletUtil;
import jakarta.servlet.http.HttpServletRequest;

public final class TokenFingerprintUtil {
   public static final String SESSION_UA_KEY = "TOKEN_BIND_UA";

   private TokenFingerprintUtil() {
   }

   public static String currentUserAgent() {
      try {
         HttpServletRequest request = CommonServletUtil.getRequest();
         return StrUtil.nullToEmpty(request.getHeader("User-Agent"));
      } catch (Exception var1) {
         return "";
      }
   }

   public static boolean matchesLoggedToken(StpLogic stpLogic) {
      if (stpLogic != null && stpLogic.isLogin()) {
         SaSession tokenSession = stpLogic.getTokenSession();
         Object saved = tokenSession.get("TOKEN_BIND_UA");
         String current = currentUserAgent();
         if (saved == null) {
            tokenSession.set("TOKEN_BIND_UA", current);
            return true;
         } else {
            return StrUtil.equals(String.valueOf(saved), current);
         }
      } else {
         return true;
      }
   }
}
