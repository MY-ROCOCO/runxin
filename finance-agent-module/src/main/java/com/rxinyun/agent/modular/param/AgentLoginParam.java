package com.rxinyun.agent.modular.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;

public class AgentLoginParam {
   @Schema(
      description = "账号",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "账号不能为空"
   )
   private String account;
   @Schema(
      description = "密码",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "密码不能为空"
   )
   private String password;
   @Schema(
      description = "设备"
   )
   private String device;

   public String getAccount() {
      return this.account;
   }

   public String getPassword() {
      return this.password;
   }

   public String getDevice() {
      return this.device;
   }

   public void setAccount(final String account) {
      this.account = account;
   }

   public void setPassword(final String password) {
      this.password = password;
   }

   public void setDevice(final String device) {
      this.device = device;
   }
}
