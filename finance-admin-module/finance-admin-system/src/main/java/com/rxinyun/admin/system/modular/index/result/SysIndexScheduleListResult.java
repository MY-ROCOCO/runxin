package com.rxinyun.admin.system.modular.index.result;

import io.swagger.v3.oas.annotations.media.Schema;

public class SysIndexScheduleListResult {
   @Schema(
      description = "id"
   )
   private String id;
   @Schema(
      description = "用户id"
   )
   private String scheduleUserId;
   @Schema(
      description = "用户姓名"
   )
   private String scheduleUserName;
   @Schema(
      description = "日程日期"
   )
   private String scheduleDate;
   @Schema(
      description = "日程时间"
   )
   private String scheduleTime;
   @Schema(
      description = "日程内容"
   )
   private String scheduleContent;

   public String getId() {
      return this.id;
   }

   public String getScheduleUserId() {
      return this.scheduleUserId;
   }

   public String getScheduleUserName() {
      return this.scheduleUserName;
   }

   public String getScheduleDate() {
      return this.scheduleDate;
   }

   public String getScheduleTime() {
      return this.scheduleTime;
   }

   public String getScheduleContent() {
      return this.scheduleContent;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setScheduleUserId(final String scheduleUserId) {
      this.scheduleUserId = scheduleUserId;
   }

   public void setScheduleUserName(final String scheduleUserName) {
      this.scheduleUserName = scheduleUserName;
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
}
