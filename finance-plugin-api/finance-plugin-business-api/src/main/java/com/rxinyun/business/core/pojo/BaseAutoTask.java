package com.rxinyun.business.core.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;

@Schema(
   description = "自动化任务添加参数"
)
public class BaseAutoTask {
   @Schema(
      description = "宿主机ID"
   )
   private String masterId;
   @Schema(
      description = "服务器ID"
   )
   private String serverId;
   @Schema(
      description = "产品类型: 1 云服务器  2 独立服务器"
   )
   private Integer productType;
   @Schema(
      description = "任务类型"
   )
   private String taskType;
   @Schema(
      description = "执行时间"
   )
   private String executeTime;
   @Schema(
      description = "任务状态: 1:待执行 2:执行中 3:执行完成 -1:执行失败"
   )
   private Integer taskStatus;
   @Schema(
      description = "任务描述"
   )
   private String taskDesc;
   @Schema(
      description = "操作平台: B 管理员操作, C 会员操作"
   )
   private String platform;
   @Schema(
      description = "任务参数JSON"
   )
   private String params;
   @Schema(
      description = "重试次数"
   )
   private Integer retryCount;
   @Schema(
      description = "最大重试次数"
   )
   private Integer maxRetry;
   @Schema(
      description = "开始处理时间"
   )
   private Date startTime;
   @Schema(
      description = "结束处理时间"
   )
   private Date endTime;
   @Schema(
      description = "下次重试时间"
   )
   private Date nextRetryTime;
   @Schema(
      description = "任务超时时间"
   )
   private Date timeoutTime;

   public String getMasterId() {
      return this.masterId;
   }

   public String getServerId() {
      return this.serverId;
   }

   public Integer getProductType() {
      return this.productType;
   }

   public String getTaskType() {
      return this.taskType;
   }

   public String getExecuteTime() {
      return this.executeTime;
   }

   public Integer getTaskStatus() {
      return this.taskStatus;
   }

   public String getTaskDesc() {
      return this.taskDesc;
   }

   public String getPlatform() {
      return this.platform;
   }

   public String getParams() {
      return this.params;
   }

   public Integer getRetryCount() {
      return this.retryCount;
   }

   public Integer getMaxRetry() {
      return this.maxRetry;
   }

   public Date getStartTime() {
      return this.startTime;
   }

   public Date getEndTime() {
      return this.endTime;
   }

   public Date getNextRetryTime() {
      return this.nextRetryTime;
   }

   public Date getTimeoutTime() {
      return this.timeoutTime;
   }

   public void setMasterId(final String masterId) {
      this.masterId = masterId;
   }

   public void setServerId(final String serverId) {
      this.serverId = serverId;
   }

   public void setProductType(final Integer productType) {
      this.productType = productType;
   }

   public void setTaskType(final String taskType) {
      this.taskType = taskType;
   }

   public void setExecuteTime(final String executeTime) {
      this.executeTime = executeTime;
   }

   public void setTaskStatus(final Integer taskStatus) {
      this.taskStatus = taskStatus;
   }

   public void setTaskDesc(final String taskDesc) {
      this.taskDesc = taskDesc;
   }

   public void setPlatform(final String platform) {
      this.platform = platform;
   }

   public void setParams(final String params) {
      this.params = params;
   }

   public void setRetryCount(final Integer retryCount) {
      this.retryCount = retryCount;
   }

   public void setMaxRetry(final Integer maxRetry) {
      this.maxRetry = maxRetry;
   }

   public void setStartTime(final Date startTime) {
      this.startTime = startTime;
   }

   public void setEndTime(final Date endTime) {
      this.endTime = endTime;
   }

   public void setNextRetryTime(final Date nextRetryTime) {
      this.nextRetryTime = nextRetryTime;
   }

   public void setTimeoutTime(final Date timeoutTime) {
      this.timeoutTime = timeoutTime;
   }
}
