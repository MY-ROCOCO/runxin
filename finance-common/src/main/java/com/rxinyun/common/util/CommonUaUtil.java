package com.rxinyun.common.util;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.JakartaServletUtil;
import cn.hutool.http.useragent.Browser;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import jakarta.servlet.http.HttpServletRequest;

public class CommonUaUtil {
   public static String getBrowser(HttpServletRequest request) {
      UserAgent userAgent = getUserAgent(request);
      if (ObjectUtil.isEmpty(userAgent)) {
         return "-";
      } else {
         String browser = userAgent.getBrowser().toString();
         if (StrUtil.isNotBlank(browser) && browser.length() > 250) {
            browser = browser.substring(0, 250);
         }

         return "Unknown".equals(browser) ? "-" : browser;
      }
   }

   public static String getOs(HttpServletRequest request) {
      UserAgent userAgent = getUserAgent(request);
      if (ObjectUtil.isEmpty(userAgent)) {
         return "-";
      } else {
         String os = userAgent.getOs().toString();
         if (StrUtil.isNotBlank(os) && os.length() > 250) {
            os = os.substring(0, 250);
         }

         return "Unknown".equals(os) ? "-" : os;
      }
   }

   private static UserAgent getUserAgent(HttpServletRequest request) {
      String userAgentStr = JakartaServletUtil.getHeaderIgnoreCase(request, "User-Agent");
      UserAgent userAgent = UserAgentUtil.parse(userAgentStr);
      if (ObjectUtil.isNotEmpty(userAgentStr) && "Unknown".equals(userAgent.getBrowser().getName())) {
         userAgent.setBrowser(new Browser(userAgentStr, null, ""));
      }

      return userAgent;
   }
}
