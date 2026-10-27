package com.rxinyun.admin.production.modular.dns.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.rxinyun.common.pojo.CommonEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;

@TableName(
   value = "DNS_NODE_CALLBACK_LOG",
   autoResultMap = true
)
@Schema(
   description = "DNS节点回调日志"
)
public class DnsNodeCallbackLog extends CommonEntity {
   @Schema(
      description = "主键ID"
   )
   private String id;
   @Schema(
      description = "DNS节点任务ID"
   )
   private String taskId;
   @Schema(
      description = "请求唯一标识"
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
      description = "节点侧事件唯一标识"
   )
   private String eventId;
   @Schema(
      description = "事件类型，如 NODE_INSTALLED、TASK_RESULT"
   )
   private String eventType;
   @Schema(
      description = "回调动作"
   )
   private String action;
   @Schema(
      description = "节点回调时间"
   )
   private Date callbackTime;
   @Schema(
      description = "回调请求头JSON"
   )
   private String requestHeaders;
   @Schema(
      description = "回调请求体JSON"
   )
   private String requestBody;
   @Schema(
      description = "Java响应内容JSON"
   )
   private String responseBody;
   @Schema(
      description = "验签状态：1通过 0失败"
   )
   private Integer signStatus;
   @Schema(
      description = "处理状态：1成功 0失败"
   )
   private Integer processStatus;
   @Schema(
      description = "错误信息"
   )
   private String errorMessage;
   @Schema(
      description = "备注"
   )
   private String remark;

   public String getId() {
      return this.id;
   }

   public String getTaskId() {
      return this.taskId;
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

   public String getEventId() {
      return this.eventId;
   }

   public String getEventType() {
      return this.eventType;
   }

   public String getAction() {
      return this.action;
   }

   public Date getCallbackTime() {
      return this.callbackTime;
   }

   public String getRequestHeaders() {
      return this.requestHeaders;
   }

   public String getRequestBody() {
      return this.requestBody;
   }

   public String getResponseBody() {
      return this.responseBody;
   }

   public Integer getSignStatus() {
      return this.signStatus;
   }

   public Integer getProcessStatus() {
      return this.processStatus;
   }

   public String getErrorMessage() {
      return this.errorMessage;
   }

   public String getRemark() {
      return this.remark;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setTaskId(final String taskId) {
      this.taskId = taskId;
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

   public void setEventId(final String eventId) {
      this.eventId = eventId;
   }

   public void setEventType(final String eventType) {
      this.eventType = eventType;
   }

   public void setAction(final String action) {
      this.action = action;
   }

   public void setCallbackTime(final Date callbackTime) {
      this.callbackTime = callbackTime;
   }

   public void setRequestHeaders(final String requestHeaders) {
      this.requestHeaders = requestHeaders;
   }

   public void setRequestBody(final String requestBody) {
      this.requestBody = requestBody;
   }

   public void setResponseBody(final String responseBody) {
      this.responseBody = responseBody;
   }

   public void setSignStatus(final Integer signStatus) {
      this.signStatus = signStatus;
   }

   public void setProcessStatus(final Integer processStatus) {
      this.processStatus = processStatus;
   }

   public void setErrorMessage(final String errorMessage) {
      this.errorMessage = errorMessage;
   }

   public void setRemark(final String remark) {
      this.remark = remark;
   }
}
