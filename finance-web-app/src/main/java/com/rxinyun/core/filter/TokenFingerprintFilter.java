package com.rxinyun.core.filter;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.stp.StpLogic;
import com.rxinyun.common.util.CommonResponseUtil;
import com.rxinyun.core.util.TokenFingerprintUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(Integer.MAX_VALUE)
public class TokenFingerprintFilter extends OncePerRequestFilter {
   private static final Logger log = LoggerFactory.getLogger(TokenFingerprintFilter.class);
   private static final String[] LOGIN_TYPES = new String[]{"login", "C", "A"};

   public TokenFingerprintFilter() {
   }

   protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
      for (String loginType : LOGIN_TYPES) {
         StpLogic stpLogic = SaManager.getStpLogic(loginType, false);
         if (stpLogic != null && stpLogic.isLogin() && !TokenFingerprintUtil.matchesLoggedToken(stpLogic)) {
            String tokenValue = stpLogic.getTokenValue();
            log.warn("Token浏览器指纹不一致，已注销，loginType={}，path={}", loginType, request.getRequestURI());
            stpLogic.logoutByTokenValue(tokenValue);
            CommonResponseUtil.renderError(response, 401, "登录环境已变化，请重新登录");
            return;
         }
      }

      filterChain.doFilter(request, response);
   }
}
