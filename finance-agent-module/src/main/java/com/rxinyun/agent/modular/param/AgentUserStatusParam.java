package com.rxinyun.agent.modular.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;

public class AgentUserStatusParam {
   @Schema(
      description = "代理账号ID",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "代理账号ID不能为空"
   )
   private String id;
   @Schema(
      description = "状态",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "状态不能为空"
   )
   private String status;

   public String getId() {
      return this.id;
   }

   public String getStatus() {
      return this.status;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setStatus(final String status) {
      this.status = status;
   }
}
