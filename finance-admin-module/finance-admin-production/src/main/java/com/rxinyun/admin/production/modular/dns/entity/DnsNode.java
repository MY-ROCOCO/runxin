package com.rxinyun.admin.production.modular.dns.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rxinyun.common.pojo.CommonEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;

@TableName(
   value = "DNS_NODE",
   autoResultMap = true
)
public class DnsNode extends CommonEntity {
   @Schema(
      description = "主键ID"
   )
   private String id;
   @Schema(
      description = "节点名称"
   )
   private String name;
   @Schema(
      description = "节点API地址"
   )
   private String apiUrl;
   @JsonIgnore
   @Schema(
      description = "节点API Token密文"
   )
   private String apiToken;
   @Schema(
      description = "节点API Token脱敏值"
   )
   @TableField(
      exist = false
   )
   private String apiTokenMasked;
   @Schema(
      description = "Java服务回调地址"
   )
   private String callbackUrl;
   @JsonIgnore
   @Schema(
      description = "回调Token摘要"
   )
   private String callbackTokenHash;
   @Schema(
      description = "节点版本号"
   )
   private String version;
   @Schema(
      description = "安装状态：0未安装 1已安装"
   )
   private Integer installStatus;
   @Schema(
      description = "安装完成时间"
   )
   private Date installTime;
   @Schema(
      description = "回调Token脱敏值"
   )
   @TableField(
      exist = false
   )
   private String callbackTokenMasked;
   @Schema(
      description = "状态：1启用 0禁用"
   )
   private Integer status;
   @Schema(
      description = "最后心跳时间"
   )
   private Date lastHeartbeatTime;
   @Schema(
      description = "心跳状态：1在线 0离线"
   )
   private Integer heartbeatStatus;
   @Schema(
      description = "连续心跳失败次数"
   )
   private Integer heartbeatFailCount;
   @Schema(
      description = "节点运行时长，单位秒"
   )
   private Long uptimeSeconds;
   @Schema(
      description = "节点最后执行命令ID"
   )
   private String lastCommandId;
   @Schema(
      description = "节点托管域名数量"
   )
   private Integer zoneCount;
   @Schema(
      description = "节点解析记录数量"
   )
   private Integer recordCount;
   @Schema(
      description = "DNS查询次数"
   )
   private Long dnsQueryCount;
   @Schema(
      description = "节点排队任务数"
   )
   private Integer taskQueuedCount;
   @Schema(
      description = "节点执行中任务数"
   )
   private Integer taskRunningCount;
   @Schema(
      description = "节点成功任务数"
   )
   private Integer taskSucceededCount;
   @Schema(
      description = "节点失败任务数"
   )
   private Integer taskFailedCount;
   @Schema(
      description = "备注"
   )
   private String remark;

   public String getId() {
      return this.id;
   }

   public String getName() {
      return this.name;
   }

   public String getApiUrl() {
      return this.apiUrl;
   }

   public String getApiToken() {
      return this.apiToken;
   }

   public String getApiTokenMasked() {
      return this.apiTokenMasked;
   }

   public String getCallbackUrl() {
      return this.callbackUrl;
   }

   public String getCallbackTokenHash() {
      return this.callbackTokenHash;
   }

   public String getVersion() {
      return this.version;
   }

   public Integer getInstallStatus() {
      return this.installStatus;
   }

   public Date getInstallTime() {
      return this.installTime;
   }

   public String getCallbackTokenMasked() {
      return this.callbackTokenMasked;
   }

   public Integer getStatus() {
      return this.status;
   }

   public Date getLastHeartbeatTime() {
      return this.lastHeartbeatTime;
   }

   public Integer getHeartbeatStatus() {
      return this.heartbeatStatus;
   }

   public Integer getHeartbeatFailCount() {
      return this.heartbeatFailCount;
   }

   public Long getUptimeSeconds() {
      return this.uptimeSeconds;
   }

   public String getLastCommandId() {
      return this.lastCommandId;
   }

   public Integer getZoneCount() {
      return this.zoneCount;
   }

   public Integer getRecordCount() {
      return this.recordCount;
   }

   public Long getDnsQueryCount() {
      return this.dnsQueryCount;
   }

   public Integer getTaskQueuedCount() {
      return this.taskQueuedCount;
   }

   public Integer getTaskRunningCount() {
      return this.taskRunningCount;
   }

   public Integer getTaskSucceededCount() {
      return this.taskSucceededCount;
   }

   public Integer getTaskFailedCount() {
      return this.taskFailedCount;
   }

   public String getRemark() {
      return this.remark;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setName(final String name) {
      this.name = name;
   }

   public void setApiUrl(final String apiUrl) {
      this.apiUrl = apiUrl;
   }

   @JsonIgnore
   public void setApiToken(final String apiToken) {
      this.apiToken = apiToken;
   }

   public void setApiTokenMasked(final String apiTokenMasked) {
      this.apiTokenMasked = apiTokenMasked;
   }

   public void setCallbackUrl(final String callbackUrl) {
      this.callbackUrl = callbackUrl;
   }

   @JsonIgnore
   public void setCallbackTokenHash(final String callbackTokenHash) {
      this.callbackTokenHash = callbackTokenHash;
   }

   public void setVersion(final String version) {
      this.version = version;
   }

   public void setInstallStatus(final Integer installStatus) {
      this.installStatus = installStatus;
   }

   public void setInstallTime(final Date installTime) {
      this.installTime = installTime;
   }

   public void setCallbackTokenMasked(final String callbackTokenMasked) {
      this.callbackTokenMasked = callbackTokenMasked;
   }

   public void setStatus(final Integer status) {
      this.status = status;
   }

   public void setLastHeartbeatTime(final Date lastHeartbeatTime) {
      this.lastHeartbeatTime = lastHeartbeatTime;
   }

   public void setHeartbeatStatus(final Integer heartbeatStatus) {
      this.heartbeatStatus = heartbeatStatus;
   }

   public void setHeartbeatFailCount(final Integer heartbeatFailCount) {
      this.heartbeatFailCount = heartbeatFailCount;
   }

   public void setUptimeSeconds(final Long uptimeSeconds) {
      this.uptimeSeconds = uptimeSeconds;
   }

   public void setLastCommandId(final String lastCommandId) {
      this.lastCommandId = lastCommandId;
   }

   public void setZoneCount(final Integer zoneCount) {
      this.zoneCount = zoneCount;
   }

   public void setRecordCount(final Integer recordCount) {
      this.recordCount = recordCount;
   }

   public void setDnsQueryCount(final Long dnsQueryCount) {
      this.dnsQueryCount = dnsQueryCount;
   }

   public void setTaskQueuedCount(final Integer taskQueuedCount) {
      this.taskQueuedCount = taskQueuedCount;
   }

   public void setTaskRunningCount(final Integer taskRunningCount) {
      this.taskRunningCount = taskRunningCount;
   }

   public void setTaskSucceededCount(final Integer taskSucceededCount) {
      this.taskSucceededCount = taskSucceededCount;
   }

   public void setTaskFailedCount(final Integer taskFailedCount) {
      this.taskFailedCount = taskFailedCount;
   }

   public void setRemark(final String remark) {
      this.remark = remark;
   }
}
