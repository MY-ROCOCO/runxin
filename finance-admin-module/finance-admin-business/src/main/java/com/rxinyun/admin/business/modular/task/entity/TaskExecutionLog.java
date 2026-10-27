package com.rxinyun.admin.business.modular.task.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;

@TableName("BUSINESS_TASK_EXECUTION_LOG")
public class TaskExecutionLog {
   @Schema(
      description = "主键"
   )
   private String id;
   @Schema(
      description = "任务ID"
   )
   private String taskId;
   @Schema(
      description = "服务器ID"
   )
   private String serverId;
   @Schema(
      description = "任务类型"
   )
   private String taskType;
   @Schema(
      description = "执行开始时间"
   )
   private Date startTime;
   @Schema(
      description = "执行结束时间"
   )
   private Date endTime;
   @Schema(
      description = "执行耗时(毫秒)"
   )
   private Long duration;
   @Schema(
      description = "执行状态: SUCCESS-成功, FAILED-失败"
   )
   private String status;
   @Schema(
      description = "执行结果详情"
   )
   private String resultDetail;
   @Schema(
      description = "错误信息"
   )
   private String errorMessage;
   @Schema(
      description = "创建时间"
   )
   @TableField(
      fill = FieldFill.INSERT
   )
   private Date createTime;
   @Schema(
      description = "更新时间"
   )
   @TableField(
      fill = FieldFill.UPDATE
   )
   private Date updateTime;

   public String getId() {
      return this.id;
   }

   public String getTaskId() {
      return this.taskId;
   }

   public String getServerId() {
      return this.serverId;
   }

   public String getTaskType() {
      return this.taskType;
   }

   public Date getStartTime() {
      return this.startTime;
   }

   public Date getEndTime() {
      return this.endTime;
   }

   public Long getDuration() {
      return this.duration;
   }

   public String getStatus() {
      return this.status;
   }

   public String getResultDetail() {
      return this.resultDetail;
   }

   public String getErrorMessage() {
      return this.errorMessage;
   }

   public Date getCreateTime() {
      return this.createTime;
   }

   public Date getUpdateTime() {
      return this.updateTime;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setTaskId(final String taskId) {
      this.taskId = taskId;
   }

   public void setServerId(final String serverId) {
      this.serverId = serverId;
   }

   public void setTaskType(final String taskType) {
      this.taskType = taskType;
   }

   public void setStartTime(final Date startTime) {
      this.startTime = startTime;
   }

   public void setEndTime(final Date endTime) {
      this.endTime = endTime;
   }

   public void setDuration(final Long duration) {
      this.duration = duration;
   }

   public void setStatus(final String status) {
      this.status = status;
   }

   public void setResultDetail(final String resultDetail) {
      this.resultDetail = resultDetail;
   }

   public void setErrorMessage(final String errorMessage) {
      this.errorMessage = errorMessage;
   }

   public void setCreateTime(final Date createTime) {
      this.createTime = createTime;
   }

   public void setUpdateTime(final Date updateTime) {
      this.updateTime = updateTime;
   }
}
