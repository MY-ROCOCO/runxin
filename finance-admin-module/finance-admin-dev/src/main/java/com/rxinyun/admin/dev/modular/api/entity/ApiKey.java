package com.rxinyun.admin.dev.modular.api.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rxinyun.common.pojo.CommonEntity;
import io.swagger.v3.oas.annotations.media.Schema;

@TableName("API_KEY")
public class ApiKey extends CommonEntity {
   @Schema(
      description = "id"
   )
   private String id;
   @Schema(
      description = "会员编号（关联client_user.id）"
   )
   private Long memberNo;
   @Schema(
      description = "API密钥（UUID）"
   )
   private String apiKey;
   @JsonIgnore
   @Schema(
      description = "API密钥Secret（加密存储），不返回给前端"
   )
   private String apiSecret;
   @Schema(
      description = "密钥名称/备注，如「B平台生产环境」"
   )
   private String name;
   @Schema(
      description = "状态：0-禁用，1-启用"
   )
   private Integer status;

   public String getId() {
      return this.id;
   }

   public Long getMemberNo() {
      return this.memberNo;
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

   public void setMemberNo(final Long memberNo) {
      this.memberNo = memberNo;
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
