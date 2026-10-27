package com.rxinyun.admin.auth.modular.monitor.param;

import io.swagger.v3.oas.annotations.media.Schema;

public class AuthSessionPageParam {
   @Schema(
      description = "用户id"
   )
   private String userId;

   public String getUserId() {
      return this.userId;
   }

   public void setUserId(final String userId) {
      this.userId = userId;
   }
}
