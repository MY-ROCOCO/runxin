package com.rxinyun.order.core.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;

public class BaseOrderAsyncTask {
   @Schema(
      description = "主键ID"
   )
   private Long id;
   @Schema(
      description = "订单号"
   )
   private String orderNo;
   @Schema(
      description = "任务类型"
   )
   private String taskType;
   @Schema(
      description = "任务状态：PENDING, PROCESSING, SUCCESS, FAILED"
   )
   private String status;
   @Schema(
      description = "任务参数JSON"
   )
   private String params;
   @Schema(
      description = "错误信息"
   )
   private String errorMsg;
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

   public Long getId() {
      return this.id;
   }

   public String getOrderNo() {
      return this.orderNo;
   }

   public String getTaskType() {
      return this.taskType;
   }

   public String getStatus() {
      return this.status;
   }

   public String getParams() {
      return this.params;
   }

   public String getErrorMsg() {
      return this.errorMsg;
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

   public void setId(final Long id) {
      this.id = id;
   }

   public void setOrderNo(final String orderNo) {
      this.orderNo = orderNo;
   }

   public void setTaskType(final String taskType) {
      this.taskType = taskType;
   }

   public void setStatus(final String status) {
      this.status = status;
   }

   public void setParams(final String params) {
      this.params = params;
   }

   public void setErrorMsg(final String errorMsg) {
      this.errorMsg = errorMsg;
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
