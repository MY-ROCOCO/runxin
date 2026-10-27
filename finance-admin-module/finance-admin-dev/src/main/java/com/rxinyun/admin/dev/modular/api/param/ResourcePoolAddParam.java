package com.rxinyun.admin.dev.modular.api.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(
   description = "资源池添加参数"
)
public class ResourcePoolAddParam {
   @Schema(
      description = "资源池类型，如：Reckonkvm"
   )
   @NotBlank(
      message = "资源池类型不能为空"
   )
   private String poolType;
   @Schema(
      description = "API请求地址"
   )
   @NotBlank(
      message = "API请求地址不能为空"
   )
   private String apiUrl;
   @Schema(
      description = "API请求密钥"
   )
   @NotBlank(
      message = "API请求密钥不能为空"
   )
   private String apiKey;
   @Schema(
      description = "API密钥Secret（原始值，会加密存储）"
   )
   @NotBlank(
      message = "API密钥Secret不能为空"
   )
   private String apiSecret;
   @Schema(
      description = "资源池名称/备注"
   )
   private String name;

   public String getPoolType() {
      return this.poolType;
   }

   public String getApiUrl() {
      return this.apiUrl;
   }

   public String getApiKey() {
      return this.apiKey;
   }

   public String getApiSecret() {
      return this.apiSecret;
   }

   public String getName() {
      return this.name;
   }

   public void setPoolType(final String poolType) {
      this.poolType = poolType;
   }

   public void setApiUrl(final String apiUrl) {
      this.apiUrl = apiUrl;
   }

   public void setApiKey(final String apiKey) {
      this.apiKey = apiKey;
   }

   public void setApiSecret(final String apiSecret) {
      this.apiSecret = apiSecret;
   }

   public void setName(final String name) {
      this.name = name;
   }
}
