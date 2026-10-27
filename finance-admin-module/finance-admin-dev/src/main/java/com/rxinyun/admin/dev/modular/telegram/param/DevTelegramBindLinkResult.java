package com.rxinyun.admin.dev.modular.telegram.param;

import io.swagger.v3.oas.annotations.media.Schema;

public class DevTelegramBindLinkResult {
   @Schema(
      description = "绑定token"
   )
   private String token;
   @Schema(
      description = "Telegram机器人绑定链接"
   )
   private String bindUrl;

   public DevTelegramBindLinkResult(String token, String bindUrl) {
      this.token = token;
      this.bindUrl = bindUrl;
   }

   public String getToken() {
      return this.token;
   }

   public String getBindUrl() {
      return this.bindUrl;
   }

   public void setToken(final String token) {
      this.token = token;
   }

   public void setBindUrl(final String bindUrl) {
      this.bindUrl = bindUrl;
   }
}
