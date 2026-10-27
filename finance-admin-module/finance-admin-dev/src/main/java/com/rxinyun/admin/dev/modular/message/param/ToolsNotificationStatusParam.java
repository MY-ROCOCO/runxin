package com.rxinyun.admin.dev.modular.message.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;

public class ToolsNotificationStatusParam {
   @Schema(
      description = "id",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "id不能为空"
   )
   private String id;
   @Schema(
      description = "状态: 0:不存在,1:开启,2:关闭",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "状态不能为空"
   )
   private Integer status;

   public String getId() {
      return this.id;
   }

   public Integer getStatus() {
      return this.status;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setStatus(final Integer status) {
      this.status = status;
   }
}
