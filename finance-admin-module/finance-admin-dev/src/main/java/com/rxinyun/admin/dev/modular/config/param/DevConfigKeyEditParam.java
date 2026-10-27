package com.rxinyun.admin.dev.modular.config.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;

public class DevConfigKeyEditParam {
   @Schema(
      description = "配置键",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "configKey不能为空"
   )
   private String configKey;
   @Schema(
      description = "配置值",
      requiredMode = RequiredMode.REQUIRED
   )
   private String configValue;

   public String getConfigKey() {
      return this.configKey;
   }

   public String getConfigValue() {
      return this.configValue;
   }

   public void setConfigKey(final String configKey) {
      this.configKey = configKey;
   }

   public void setConfigValue(final String configValue) {
      this.configValue = configValue;
   }
}
