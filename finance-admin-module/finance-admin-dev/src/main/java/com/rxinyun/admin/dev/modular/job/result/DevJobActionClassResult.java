package com.rxinyun.admin.dev.modular.job.result;

import io.swagger.v3.oas.annotations.media.Schema;

public class DevJobActionClassResult {
   @Schema(
      description = "任务类名"
   )
   private String actionClass;
   @Schema(
      description = "任务类中文名称"
   )
   private String actionClassName;

   public String getActionClass() {
      return this.actionClass;
   }

   public String getActionClassName() {
      return this.actionClassName;
   }

   public void setActionClass(final String actionClass) {
      this.actionClass = actionClass;
   }

   public void setActionClassName(final String actionClassName) {
      this.actionClassName = actionClassName;
   }
}
