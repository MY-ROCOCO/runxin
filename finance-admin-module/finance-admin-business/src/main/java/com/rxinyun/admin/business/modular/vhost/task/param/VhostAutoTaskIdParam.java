package com.rxinyun.admin.business.modular.vhost.task.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;

public class VhostAutoTaskIdParam {
   @NotBlank(
      message = "任务不能为空"
   )
   @Schema(
      description = "任务ID",
      requiredMode = RequiredMode.REQUIRED
   )
   private String id;

   public String getId() {
      return this.id;
   }

   public void setId(final String id) {
      this.id = id;
   }
}
