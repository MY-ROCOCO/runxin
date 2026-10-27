package com.rxinyun.admin.production.modular.dns.param;

import com.alibaba.fastjson.JSONObject;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
   description = "DNS节点回调参数"
)
public class DnsCallbackParam {
   @Schema(
      description = "节点侧事件唯一标识"
   )
   private String eventId;
   @Schema(
      description = "事件类型：NODE_INSTALLED、TASK_RESULT"
   )
   private String eventType;
   @Schema(
      description = "DNS节点ID"
   )
   private String nodeId;
   @Schema(
      description = "事件状态：SUCCEEDED、FAILED"
   )
   private String status;
   @Schema(
      description = "节点API监听地址，如 :8080"
   )
   private String apiAddress;
   @Schema(
      description = "节点DNS监听地址，如 :53"
   )
   private String dnsAddress;
   @Schema(
      description = "事件发生时间，ISO-8601字符串"
   )
   private String timestamp;
   @Schema(
      description = "节点侧任务ID"
   )
   private String taskId;
   @Schema(
      description = "节点侧命令ID"
   )
   private String commandId;
   @Schema(
      description = "配置版本号"
   )
   private Integer version;
   @Schema(
      description = "任务完成时间，ISO-8601字符串"
   )
   private String finishedAt;
   @Schema(
      description = "Java侧请求唯一标识"
   )
   private String requestId;
   @Schema(
      description = "节点侧任务ID"
   )
   private String nodeTaskId;
   @Schema(
      description = "任务动作"
   )
   private String action;
   @Schema(
      description = "错误信息"
   )
   private String errorMessage;
   @Schema(
      description = "任务成功结果"
   )
   private JSONObject result;
   @Schema(
      description = "任务失败错误"
   )
   private JSONObject error;

   public String getEventId() {
      return this.eventId;
   }

   public String getEventType() {
      return this.eventType;
   }

   public String getNodeId() {
      return this.nodeId;
   }

   public String getStatus() {
      return this.status;
   }

   public String getApiAddress() {
      return this.apiAddress;
   }

   public String getDnsAddress() {
      return this.dnsAddress;
   }

   public String getTimestamp() {
      return this.timestamp;
   }

   public String getTaskId() {
      return this.taskId;
   }

   public String getCommandId() {
      return this.commandId;
   }

   public Integer getVersion() {
      return this.version;
   }

   public String getFinishedAt() {
      return this.finishedAt;
   }

   public String getRequestId() {
      return this.requestId;
   }

   public String getNodeTaskId() {
      return this.nodeTaskId;
   }

   public String getAction() {
      return this.action;
   }

   public String getErrorMessage() {
      return this.errorMessage;
   }

   public JSONObject getResult() {
      return this.result;
   }

   public JSONObject getError() {
      return this.error;
   }

   public void setEventId(final String eventId) {
      this.eventId = eventId;
   }

   public void setEventType(final String eventType) {
      this.eventType = eventType;
   }

   public void setNodeId(final String nodeId) {
      this.nodeId = nodeId;
   }

   public void setStatus(final String status) {
      this.status = status;
   }

   public void setApiAddress(final String apiAddress) {
      this.apiAddress = apiAddress;
   }

   public void setDnsAddress(final String dnsAddress) {
      this.dnsAddress = dnsAddress;
   }

   public void setTimestamp(final String timestamp) {
      this.timestamp = timestamp;
   }

   public void setTaskId(final String taskId) {
      this.taskId = taskId;
   }

   public void setCommandId(final String commandId) {
      this.commandId = commandId;
   }

   public void setVersion(final Integer version) {
      this.version = version;
   }

   public void setFinishedAt(final String finishedAt) {
      this.finishedAt = finishedAt;
   }

   public void setRequestId(final String requestId) {
      this.requestId = requestId;
   }

   public void setNodeTaskId(final String nodeTaskId) {
      this.nodeTaskId = nodeTaskId;
   }

   public void setAction(final String action) {
      this.action = action;
   }

   public void setErrorMessage(final String errorMessage) {
      this.errorMessage = errorMessage;
   }

   public void setResult(final JSONObject result) {
      this.result = result;
   }

   public void setError(final JSONObject error) {
      this.error = error;
   }
}
