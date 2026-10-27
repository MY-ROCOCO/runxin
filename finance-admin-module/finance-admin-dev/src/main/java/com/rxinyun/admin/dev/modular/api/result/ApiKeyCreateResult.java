package com.rxinyun.admin.dev.modular.api.result;

import io.swagger.v3.oas.annotations.media.Schema;

public class ApiKeyCreateResult {
   @Schema(
      description = "API密钥（UUID）"
   )
   private String apiKey;
   @Schema(
      description = "API密钥Secret（原始值，仅创建时返回一次）"
   )
   private String apiSecret;

   public String getApiKey() {
      return this.apiKey;
   }

   public String getApiSecret() {
      return this.apiSecret;
   }

   public void setApiKey(final String apiKey) {
      this.apiKey = apiKey;
   }

   public void setApiSecret(final String apiSecret) {
      this.apiSecret = apiSecret;
   }
}
