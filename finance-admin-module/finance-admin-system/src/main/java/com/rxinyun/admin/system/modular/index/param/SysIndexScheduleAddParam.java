package com.rxinyun.admin.system.modular.index.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;

public class SysIndexScheduleAddParam {
   @Schema(
      description = "日程日期",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "scheduleDate不能为空"
   )
   private String scheduleDate;
   @Schema(
      description = "日程时间",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "scheduleTime不能为空"
   )
   private String scheduleTime;
   @Schema(
      description = "日程内容",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "scheduleContent不能为空"
   )
   private String scheduleContent;
   @Schema(
      description = "用户id",
      hidden = true
   )
   private String scheduleUserId;
   @Schema(
      description = "用户姓名",
      hidden = true
   )
   private String scheduleUserName;

   public String getScheduleDate() {
      return this.scheduleDate;
   }

   public String getScheduleTime() {
      return this.scheduleTime;
   }

   public String getScheduleContent() {
      return this.scheduleContent;
   }

   public String getScheduleUserId() {
      return this.scheduleUserId;
   }

   public String getScheduleUserName() {
      return this.scheduleUserName;
   }

   public void setScheduleDate(final String scheduleDate) {
      this.scheduleDate = scheduleDate;
   }

   public void setScheduleTime(final String scheduleTime) {
      this.scheduleTime = scheduleTime;
   }

   public void setScheduleContent(final String scheduleContent) {
      this.scheduleContent = scheduleContent;
   }

   public void setScheduleUserId(final String scheduleUserId) {
      this.scheduleUserId = scheduleUserId;
   }

   public void setScheduleUserName(final String scheduleUserName) {
      this.scheduleUserName = scheduleUserName;
   }
}
