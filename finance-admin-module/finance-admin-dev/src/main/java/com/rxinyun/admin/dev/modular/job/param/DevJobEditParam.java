package com.rxinyun.admin.dev.modular.job.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class DevJobEditParam {
   @Schema(
      description = "id",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "id不能为空"
   )
   private String id;
   @Schema(
      description = "名称",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "name不能为空"
   )
   private String name;
   @Schema(
      description = "分类",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "category不能为空"
   )
   private String category;
   @Schema(
      description = "任务类名",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "actionClass不能为空"
   )
   private String actionClass;
   @Schema(
      description = "cron表达式",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "cronExpression不能为空"
   )
   private String cronExpression;
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

   public String getName() {
      return this.name;
   }

   public String getCategory() {
      return this.category;
   }

   public String getActionClass() {
      return this.actionClass;
   }

   public String getCronExpression() {
      return this.cronExpression;
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

   public void setName(final String name) {
      this.name = name;
   }

   public void setCategory(final String category) {
      this.category = category;
   }

   public void setActionClass(final String actionClass) {
      this.actionClass = actionClass;
   }

   public void setCronExpression(final String cronExpression) {
      this.cronExpression = cronExpression;
   }

   public void setSortCode(final Integer sortCode) {
      this.sortCode = sortCode;
   }

   public void setExtJson(final String extJson) {
      this.extJson = extJson;
   }
}
