package com.rxinyun.core.config;

import com.alibaba.druid.spring.boot3.autoconfigure.properties.DruidStatProperties;
import com.alibaba.druid.spring.boot3.autoconfigure.properties.DruidStatProperties.StatViewServlet;
import com.alibaba.druid.util.Utils;
import jakarta.servlet.Filter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(
   name = {"spring.datasource.druid.stat-view-servlet.enabled"},
   havingValue = "true"
)
public class DruidConfigure {
   public DruidConfigure() {
   }

   @Bean
   public FilterRegistrationBean removeDruidAdFilterRegistrationBean(DruidStatProperties properties) {
      StatViewServlet config = properties.getStatViewServlet();
      String pattern = config.getUrlPattern() != null ? config.getUrlPattern() : "/druid/*";
      String commonJsPattern = pattern.replaceAll("\\*", "js/common.js");
      String filePath = "support/http/resources/js/common.js";
      Filter filter = (request, response, chain) -> {
         chain.doFilter(request, response);
         response.resetBuffer();
         String text = Utils.readFromResource("support/http/resources/js/common.js");
         text = text.replaceAll("<a.*?banner\"></a><br/>", "");
         text = text.replaceAll("powered.*?shrek.wang</a>", "");
         response.getWriter().write(text);
      };
      FilterRegistrationBean registrationBean = new FilterRegistrationBean();
      registrationBean.setFilter(filter);
      registrationBean.addUrlPatterns(new String[]{commonJsPattern});
      return registrationBean;
   }
}
