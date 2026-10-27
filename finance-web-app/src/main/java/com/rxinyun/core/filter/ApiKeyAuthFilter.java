package com.rxinyun.core.filter;

import cn.hutool.core.util.StrUtil;
import com.rxinyun.common.context.ApiContext;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.util.CommonCryptogramUtil;
import com.rxinyun.dev.api.ApiKeyApi;
import com.rxinyun.dev.core.pojo.BaseApiKey;
import jakarta.annotation.Resource;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class ApiKeyAuthFilter implements Filter {
   private static final Logger log = LoggerFactory.getLogger(ApiKeyAuthFilter.class);
   @Resource
   private ApiKeyApi apiKeyApi;
   private static final String[] API_AUTH_PATHS = new String[]{"/api/order", "/api/product", "/api/resource-pool"};
   private static final String[] WHITE_LIST_PATHS = new String[]{"/api/callback", "/api/dns/callback"};

   public ApiKeyAuthFilter() {
   }

   public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
      HttpServletRequest httpRequest = (HttpServletRequest)request;
      String requestPath = httpRequest.getRequestURI();

      try {
         if (this.needsApiKeyAuth(requestPath)) {
            log.info("API Key认证：检查路径 {}", requestPath);
            String apiKey = httpRequest.getHeader("X-Api-Key");
            if (StrUtil.isEmpty(apiKey)) {
               log.error("API Key认证失败：缺少 X-Api-Key 请求头，路径：{}", requestPath);
               throw new CommonException(401, "缺少 X-Api-Key 请求头", new Object[0]);
            }

            String apiSecret = httpRequest.getHeader("X-Api-Secret");
            BaseApiKey baseApiKey = this.apiKeyApi.getByApiKey(apiKey);
            if (baseApiKey == null) {
               if (requestPath.startsWith("/api/resource-pool")) {
                  log.error("API Key认证失败：未匹配到会员 API Key，路径：{}，apiKey：{}", requestPath, apiKey);
                  throw new CommonException(401, "无效的 API Key", new Object[0]);
               }

               log.error("API Key认证失败：无效的 API Key，路径：{}，apiKey：{}", requestPath, apiKey);
               throw new CommonException(401, "无效的 API Key", new Object[0]);
            }

            if (baseApiKey.getStatus() == null || baseApiKey.getStatus() != 1) {
               log.error("API Key认证失败：API Key已禁用，路径：{}，apiKey：{}", requestPath, apiKey);
               throw new CommonException(401, "API Key已禁用", new Object[0]);
            }

            if (requestPath.startsWith("/api/resource-pool")) {
               if (StrUtil.isEmpty(apiSecret)) {
                  log.error("API Key认证失败：缺少 X-Api-Secret 请求头，路径：{}", requestPath);
                  throw new CommonException(401, "缺少 X-Api-Secret 请求头", new Object[0]);
               }

               String decryptedSecret = CommonCryptogramUtil.doSm4CbcDecrypt(baseApiKey.getApiSecret());
               if (!StrUtil.equals(apiSecret, decryptedSecret)) {
                  log.error("API Key认证失败：API Secret无效，路径：{}，apiKey：{}", requestPath, apiKey);
                  throw new CommonException(401, "无效的 API Secret", new Object[0]);
               }
            }

            ApiContext.setMemberNo(baseApiKey.getMemberNo());
            log.info("API Key认证成功：路径：{}，会员编号：{}，apiKey：{}", new Object[]{requestPath, baseApiKey.getMemberNo(), apiKey});
         }

         chain.doFilter(request, response);
      } finally {
         if (this.needsApiKeyAuth(requestPath)) {
            ApiContext.clear();
         }
      }
   }

   private boolean needsApiKeyAuth(String requestPath) {
      for (String whiteListPath : WHITE_LIST_PATHS) {
         if (requestPath.startsWith(whiteListPath)) {
            return false;
         }
      }

      for (String authPath : API_AUTH_PATHS) {
         if (requestPath.startsWith(authPath)) {
            return true;
         }
      }

      return false;
   }
}
