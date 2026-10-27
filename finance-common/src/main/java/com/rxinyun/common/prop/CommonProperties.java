package com.rxinyun.common.prop;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(
   prefix = "runxin.config.common"
)
public class CommonProperties {
   private String frontUrl;
   private String backendUrl;

   public String getFrontUrl() {
      return this.frontUrl;
   }

   public String getBackendUrl() {
      return this.backendUrl;
   }

   public void setFrontUrl(final String frontUrl) {
      this.frontUrl = frontUrl;
   }

   public void setBackendUrl(final String backendUrl) {
      this.backendUrl = backendUrl;
   }
}
