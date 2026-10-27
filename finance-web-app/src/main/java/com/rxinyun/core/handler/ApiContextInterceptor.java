package com.rxinyun.core.handler;

import com.rxinyun.common.context.ApiContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class ApiContextInterceptor implements HandlerInterceptor {
   public ApiContextInterceptor() {
   }

   public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
      ApiContext.clear();
   }
}
