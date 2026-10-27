package com.rxinyun.member.auth.core.config;

import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.strategy.SaStrategy;
import com.rxinyun.member.core.enums.SaClientTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MemberAuthConfigure implements WebMvcConfigurer {
   public void addInterceptors(InterceptorRegistry registry) {
      registry.addInterceptor(new SaInterceptor()).addPathPatterns(new String[]{"/**"});
   }

   @Bean({"memberStpClientLogic"})
   public StpLogic getStpClientLogic(SaTokenConfig saTokenConfig) {
      return new StpLogic(SaClientTypeEnum.C.getValue()).setConfig(saTokenConfig);
   }

   @Autowired
   public void rewriteSaStrategy() {
      SaStrategy.instance.getAnnotation = AnnotatedElementUtils::getMergedAnnotation;
   }
}
