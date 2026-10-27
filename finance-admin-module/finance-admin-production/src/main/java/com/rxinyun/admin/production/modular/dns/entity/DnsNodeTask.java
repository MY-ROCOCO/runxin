package com.rxinyun.admin.production.modular.dns.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.rxinyun.common.pojo.CommonEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;

@TableName(
   value = "DNS_NODE_TASK",
   autoResultMap = true
)
@Schema(
   description = "DNS节点任务队列"
)
public class DnsNodeTask extends CommonEntity {
   @Schema(
      description = "主键ID"
   )
   private String id;
   @Schema(
      description = "请求唯一标识，用于幂等和链路追踪"
   )
   private String requestId;
   @Schema(
      description = "DNS节点ID"
   )
   private String nodeId;
   @Schema(
      description = "节点侧任务ID"
   )
   private String nodeTaskId;
   @Schema(
      description = "业务类型，如 DOMAIN、RECORD、PACKAGE_INSTANCE"
   )
   private String bizType;
   @Schema(
      description = "业务数据ID"
   )
   private String bizId;
   @Schema(
      description = "任务动作，如 CREATE_DOMAIN、UPSERT_RECORD、DELETE_RECORD"
   )
   private String action;
   @Schema(
      description = "请求方法"
   )
   private String requestMethod;
   @Schema(
      description = "请求路径"
   )
   private String requestPath;
   @Schema(
      description = "请求节点参数JSON"
   )
   private String requestBody;
   @Schema(
      description = "节点响应参数JSON"
   )
   private String responseBody;
   @Schema(
      description = "任务状态：1待发送 2执行中 3执行成功 -1执行失败 -2已取消"
   )
   private Integer taskStatus;
   @Schema(
      description = "错误信息"
   )
   private String errorMessage;
   @Schema(
      description = "已重试次数"
   )
   private Integer retryCount;
   @Schema(
      description = "最大重试次数"
   )
   private Integer maxRetry;
   @Schema(
      description = "下次重试时间"
   )
   private Date nextRetryTime;
   @Schema(
      description = "开始处理时间"
   )
   private Date startTime;
   @Schema(
      description = "结束处理时间"
   )
   private Date endTime;
   @Schema(
      description = "任务超时时间"
   )
   private Date timeoutTime;
   @Schema(
      description = "备注"
   )
   private String remark;
   @Schema(
      description = "最近回调事件ID"
   )
   @TableField(
      exist = false
   )
   private String callbackEventId;
   @Schema(
      description = "最近回调事件类型"
   )
   @TableField(
      exist = false
   )
   private String callbackEventType;
   @Schema(
      description = "最近回调时间"
   )
   @TableField(
      exist = false
   )
   private Date callbackTime;
   @Schema(
      description = "最近回调处理状态：1成功 0失败"
   )
   @TableField(
      exist = false
   )
   private Integer callbackProcessStatus;
   @Schema(
      description = "最近回调错误信息"
   )
   @TableField(
      exist = false
   )
   private String callbackErrorMessage;
   @Schema(
      description = "最近回调请求体JSON"
   )
   @TableField(
      exist = false
   )
   private String callbackRequestBody;

   public String getId() {
      return this.id;
   }

   public String getRequestId() {
      return this.requestId;
   }

   public String getNodeId() {
      return this.nodeId;
   }

   public String getNodeTaskId() {
      return this.nodeTaskId;
   }

   public String getBizType() {
      return this.bizType;
   }

   public String getBizId() {
      return this.bizId;
   }

   public String getAction() {
      return this.action;
   }

   public String getRequestMethod() {
      return this.requestMethod;
   }

   public String getRequestPath() {
      return this.requestPath;
   }

   public String getRequestBody() {
      return this.requestBody;
   }

   public String getResponseBody() {
      return this.responseBody;
   }

   public Integer getTaskStatus() {
      return this.taskStatus;
   }

   public String getErrorMessage() {
      return this.errorMessage;
   }

   public Integer getRetryCount() {
      return this.retryCount;
   }

   public Integer getMaxRetry() {
      return this.maxRetry;
   }

   public Date getNextRetryTime() {
      return this.nextRetryTime;
   }

   public Date getStartTime() {
      return this.startTime;
   }

   public Date getEndTime() {
      return this.endTime;
   }

   public Date getTimeoutTime() {
      return this.timeoutTime;
   }

   public String getRemark() {
      return this.remark;
   }

   public String getCallbackEventId() {
      return this.callbackEventId;
   }

   public String getCallbackEventType() {
      return this.callbackEventType;
   }

   public Date getCallbackTime() {
      return this.callbackTime;
   }

   public Integer getCallbackProcessStatus() {
      return this.callbackProcessStatus;
   }

   public String getCallbackErrorMessage() {
      return this.callbackErrorMessage;
   }

   public String getCallbackRequestBody() {
      return this.callbackRequestBody;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setRequestId(final String requestId) {
      this.requestId = requestId;
   }

   public void setNodeId(final String nodeId) {
      this.nodeId = nodeId;
   }

   public void setNodeTaskId(final String nodeTaskId) {
      this.nodeTaskId = nodeTaskId;
   }

   public void setBizType(final String bizType) {
      this.bizType = bizType;
   }

   public void setBizId(final String bizId) {
      this.bizId = bizId;
   }

   public void setAction(final String action) {
      this.action = action;
   }

   public void setRequestMethod(final String requestMethod) {
      this.requestMethod = requestMethod;
   }

   public void setRequestPath(final String requestPath) {
      this.requestPath = requestPath;
   }

   public void setRequestBody(final String requestBody) {
      this.requestBody = requestBody;
   }

   public void setResponseBody(final String responseBody) {
      this.responseBody = responseBody;
   }

   public void setTaskStatus(final Integer taskStatus) {
      this.taskStatus = taskStatus;
   }

   public void setErrorMessage(final String errorMessage) {
      this.errorMessage = errorMessage;
   }

   public void setRetryCount(final Integer retryCount) {
      this.retryCount = retryCount;
   }

   public void setMaxRetry(final Integer maxRetry) {
      this.maxRetry = maxRetry;
   }

   public void setNextRetryTime(final Date nextRetryTime) {
      this.nextRetryTime = nextRetryTime;
   }

   public void setStartTime(final Date startTime) {
      this.startTime = startTime;
   }

   public void setEndTime(final Date endTime) {
      this.endTime = endTime;
   }

   public void setTimeoutTime(final Date timeoutTime) {
      this.timeoutTime = timeoutTime;
   }

   public void setRemark(final String remark) {
      this.remark = remark;
   }

   public void setCallbackEventId(final String callbackEventId) {
      this.callbackEventId = callbackEventId;
   }

   public void setCallbackEventType(final String callbackEventType) {
      this.callbackEventType = callbackEventType;
   }

   public void setCallbackTime(final Date callbackTime) {
      this.callbackTime = callbackTime;
   }

   public void setCallbackProcessStatus(final Integer callbackProcessStatus) {
      this.callbackProcessStatus = callbackProcessStatus;
   }

   public void setCallbackErrorMessage(final String callbackErrorMessage) {
      this.callbackErrorMessage = callbackErrorMessage;
   }

   public void setCallbackRequestBody(final String callbackRequestBody) {
      this.callbackRequestBody = callbackRequestBody;
   }
}
