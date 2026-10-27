package com.rxinyun.admin.system.modular.user.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;

public class SysUserUpdateWorkbenchParam {
   @Schema(
      description = "工作台数据",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "workbenchData不能为空"
   )
   private String workbenchData;

   public String getWorkbenchData() {
      return this.workbenchData;
   }

   public void setWorkbenchData(final String workbenchData) {
      this.workbenchData = workbenchData;
   }
}
