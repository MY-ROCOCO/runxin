package com.rxinyun.member.tools.modular.config.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class MemberConfigEditParam {
   @Schema(
      description = "id",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "id不能为空"
   )
   private String id;
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
   @Schema(
      description = "备注"
   )
   private String remark;
   @Schema(
      description = "排序码",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotNull(
      message = "sortCode不能为空"
   )
   private Integer sortCode;
   @Schema(
      description = "扩展信息"
   )
   private String extJson;

   public String getId() {
      return this.id;
   }

   public String getConfigKey() {
      return this.configKey;
   }

   public String getConfigValue() {
      return this.configValue;
   }

   public String getRemark() {
      return this.remark;
   }

   public Integer getSortCode() {
      return this.sortCode;
   }

   public String getExtJson() {
      return this.extJson;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setConfigKey(final String configKey) {
      this.configKey = configKey;
   }

   public void setConfigValue(final String configValue) {
      this.configValue = configValue;
   }

   public void setRemark(final String remark) {
      this.remark = remark;
   }

   public void setSortCode(final Integer sortCode) {
      this.sortCode = sortCode;
   }

   public void setExtJson(final String extJson) {
      this.extJson = extJson;
   }
}
