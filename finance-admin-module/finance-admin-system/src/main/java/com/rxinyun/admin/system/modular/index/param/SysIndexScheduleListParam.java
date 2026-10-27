package com.rxinyun.admin.system.modular.index.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;

public class SysIndexScheduleListParam {
   @Schema(
      description = "日程日期",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "scheduleDate不能为空"
   )
   private String scheduleDate;

   public String getScheduleDate() {
      return this.scheduleDate;
   }

   public void setScheduleDate(final String scheduleDate) {
      this.scheduleDate = scheduleDate;
   }
}
