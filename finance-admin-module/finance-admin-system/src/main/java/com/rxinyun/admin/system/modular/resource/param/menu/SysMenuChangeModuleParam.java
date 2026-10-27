package com.rxinyun.admin.system.modular.resource.param.menu;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;

public class SysMenuChangeModuleParam {
   @Schema(
      description = "id",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "id不能为空"
   )
   private String id;
   @Schema(
      description = "module",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "module不能为空"
   )
   private String module;

   public String getId() {
      return this.id;
   }

   public String getModule() {
      return this.module;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setModule(final String module) {
      this.module = module;
   }
}
