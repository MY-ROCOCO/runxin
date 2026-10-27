package com.rxinyun.common.util;

import cn.hutool.core.util.ObjectUtil;
import com.rxinyun.common.exception.CommonException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class CommonServletUtil {
   private static final Logger log = LoggerFactory.getLogger(CommonServletUtil.class);

   public static String getParamFromRequest(String paramName) {
      HttpServletRequest request = getRequest();
      String paramValue = request.getParameter(paramName);
      if (ObjectUtil.isEmpty(paramValue)) {
         paramValue = request.getHeader(paramName);
      }

      if (ObjectUtil.isEmpty(paramValue)) {
         Cookie[] cookies = request.getCookies();
         if (ObjectUtil.isNotEmpty(cookies)) {
            for (Cookie cookie : cookies) {
               String cookieName = cookie.getName();
               if (cookieName.equals(paramName)) {
                  return cookie.getValue();
               }
            }
         }
      }

      return paramValue;
   }

   public static HttpServletRequest getRequest() {
      ServletRequestAttributes servletRequestAttributes;
      try {
         servletRequestAttributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
      } catch (Exception var2) {
         log.error(">>> 非Web上下文无法获取Request：", var2);
         throw new CommonException("非Web上下文无法获取Request");
      }

      if (servletRequestAttributes == null) {
         throw new CommonException("非Web上下文无法获取Request");
      } else {
         return servletRequestAttributes.getRequest();
      }
   }

   public static HttpServletResponse getResponse() {
      ServletRequestAttributes servletRequestAttributes;
      try {
         servletRequestAttributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
      } catch (Exception var2) {
         log.error(">>> 非Web上下文无法获取Response：", var2);
         throw new CommonException("非Web上下文无法获取Response");
      }

      if (servletRequestAttributes == null) {
         throw new CommonException("非Web上下文无法获取Response");
      } else {
         return servletRequestAttributes.getResponse();
      }
   }

   public static boolean isWeb() {
      return RequestContextHolder.getRequestAttributes() != null;
   }
}
