package com.rxinyun.agent.modular.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;

public class AgentUserAddParam {
   @Schema(
      description = "代理ID",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "代理ID不能为空"
   )
   private String agentId;
   @Schema(
      description = "登录账号",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "登录账号不能为空"
   )
   private String loginAccount;
   @Schema(
      description = "密码",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "密码不能为空"
   )
   private String password;

   public String getAgentId() {
      return this.agentId;
   }

   public String getLoginAccount() {
      return this.loginAccount;
   }

   public String getPassword() {
      return this.password;
   }

   public void setAgentId(final String agentId) {
      this.agentId = agentId;
   }

   public void setLoginAccount(final String loginAccount) {
      this.loginAccount = loginAccount;
   }

   public void setPassword(final String password) {
      this.password = password;
   }
}
