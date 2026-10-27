package com.rxinyun.member.auth.modular.login.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;

public class AuthSwitchLoginParam {
   @Schema(
      description = "后台切换会员前台登录ticket",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "ticket不能为空"
   )
   private String ticket;

   public String getTicket() {
      return this.ticket;
   }

   public void setTicket(final String ticket) {
      this.ticket = ticket;
   }
}
