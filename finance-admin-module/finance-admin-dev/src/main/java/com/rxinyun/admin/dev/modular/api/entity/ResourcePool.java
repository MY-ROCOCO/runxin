package com.rxinyun.admin.dev.modular.api.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rxinyun.common.pojo.CommonEntity;
import io.swagger.v3.oas.annotations.media.Schema;

@TableName(
   value = "RESOURCE_POOL",
   autoResultMap = true
)
public class ResourcePool extends CommonEntity {
   @Schema(
      description = "主键ID"
   )
   private String id;
   @Schema(
      description = "资源池类型，如：Reckonkvm"
   )
   private String poolType;
   @Schema(
      description = "API请求地址"
   )
   private String apiUrl;
   @Schema(
      description = "API请求密钥"
   )
   private String apiKey;
   @JsonIgnore
   @Schema(
      description = "API密钥Secret（加密存储），不返回给前端"
   )
   private String apiSecret;
   @Schema(
      description = "资源池名称/备注"
   )
   private String name;
   @Schema(
      description = "状态：0-禁用，1-启用"
   )
   private Integer status;

   public String getId() {
      return this.id;
   }

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

   public Integer getStatus() {
      return this.status;
   }

   public void setId(final String id) {
      this.id = id;
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

   @JsonIgnore
   public void setApiSecret(final String apiSecret) {
      this.apiSecret = apiSecret;
   }

   public void setName(final String name) {
      this.name = name;
   }

   public void setStatus(final Integer status) {
      this.status = status;
   }
}
