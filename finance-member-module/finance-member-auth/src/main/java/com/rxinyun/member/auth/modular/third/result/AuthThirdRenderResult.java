package com.rxinyun.member.auth.modular.third.result;

import io.swagger.v3.oas.annotations.media.Schema;

public class AuthThirdRenderResult {
   @Schema(
      description = "授权地址"
   )
   private String authorizeUrl;
   @Schema(
      description = "状态码"
   )
   private String state;

   public String getAuthorizeUrl() {
      return this.authorizeUrl;
   }

   public String getState() {
      return this.state;
   }

   public void setAuthorizeUrl(final String authorizeUrl) {
      this.authorizeUrl = authorizeUrl;
   }

   public void setState(final String state) {
      this.state = state;
   }
}
