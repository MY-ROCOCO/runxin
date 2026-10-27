package com.rxinyun.agent.modular.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;

public class AgentBindIdParam {
   @Schema(
      description = "绑定记录ID",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "绑定记录ID不能为空"
   )
   private String id;

   public String getId() {
      return this.id;
   }

   public void setId(final String id) {
      this.id = id;
   }
}
