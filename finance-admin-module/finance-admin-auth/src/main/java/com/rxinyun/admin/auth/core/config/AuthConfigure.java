package com.rxinyun.admin.auth.core.config;

import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.strategy.SaStrategy;
import cn.hutool.json.JSONUtil;
import com.rxinyun.auth.core.enums.SaClientTypeEnum;
import com.rxinyun.auth.core.util.StpLoginUserUtil;
import com.rxinyun.common.cache.CommonCacheOperator;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AuthConfigure implements WebMvcConfigurer {
   public void addInterceptors(InterceptorRegistry registry) {
      registry.addInterceptor(new SaInterceptor()).addPathPatterns(new String[]{"/**"});
   }

   @Primary
   @Bean({"stpLogic"})
   public StpLogic getStpLogic(SaTokenConfig saTokenConfig) {
      return new StpLogic(SaClientTypeEnum.B.getValue()).setConfig(saTokenConfig);
   }

   @Autowired
   public void rewriteSaStrategy() {
      SaStrategy.instance.getAnnotation = AnnotatedElementUtils::getMergedAnnotation;
   }

   @Component
   public static class StpInterfaceImpl implements StpInterface {
      @Resource
      private CommonCacheOperator commonCacheOperator;

      public List<String> getPermissionList(Object loginId, String loginType) {
         if ("A".equals(loginType)) {
            return new ArrayList<>();
         } else {
            Object permissionListObject;
            if (SaClientTypeEnum.B.getValue().equals(loginType)) {
               permissionListObject = this.commonCacheOperator.get("auth-b-permission-list:" + loginId);
            } else {
               permissionListObject = this.commonCacheOperator.get("auth-c-permission-list:" + loginId);
            }

            return JSONUtil.parseArray(permissionListObject).toList(String.class);
         }
      }

      public List<String> getRoleList(Object loginId, String loginType) {
         return (List<String>)(!SaClientTypeEnum.C.getValue().equals(loginType) && !"A".equals(loginType)
            ? StpLoginUserUtil.getLoginUser().getRoleCodeList()
            : new ArrayList<>());
      }
   }
}
