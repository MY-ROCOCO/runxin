package com.rxinyun.common.util;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

public class CommonFilterExceptionUtil {
   public static void handleFilterException(ServletRequest request, ServletResponse response, Exception e) {
      try {
         request.setAttribute("model", e);
         request.getRequestDispatcher("/errorView").forward(request, response);
      } catch (Exception var4) {
      }
   }
}
