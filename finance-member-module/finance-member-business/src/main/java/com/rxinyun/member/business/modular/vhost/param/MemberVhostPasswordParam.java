package com.rxinyun.member.business.modular.vhost.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class MemberVhostPasswordParam extends MemberVhostIdParam {
   @NotBlank(
      message = "新密码不能为空"
   )
   @Size(
      min = 6,
      max = 32,
      message = "新密码长度须为6到32位"
   )
   @Schema(
      description = "新密码，6到32位",
      requiredMode = RequiredMode.REQUIRED
   )
   private String password;

   public String getPassword() {
      return this.password;
   }

   public void setPassword(final String password) {
      this.password = password;
   }
}
