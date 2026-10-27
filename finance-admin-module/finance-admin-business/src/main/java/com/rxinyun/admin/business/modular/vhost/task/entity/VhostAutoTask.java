package com.rxinyun.admin.business.modular.vhost.task.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.rxinyun.common.pojo.CommonEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;

@TableName(
   value = "VHOST_AUTO_TASK",
   autoResultMap = true
)
@Schema(
   description = "虚拟主机自动化任务"
)
public class VhostAutoTask extends CommonEntity {
   @Schema(
      description = "任务ID"
   )
   private String id;
   @Schema(
      description = "主控ID"
   )
   private String masterId;
   @TableField(
      exist = false
   )
   @Schema(
      description = "主控名称"
   )
   private String masterName;
   @Schema(
      description = "虚拟主机实例ID"
   )
   private String hostId;
   @TableField(
      exist = false
   )
   @Schema(
      description = "主机名称"
   )
   private String hostName;
   @Schema(
      description = "任务类型：CREATE创建 RENEW续费 START开机 STOP关机 DELETE删除"
   )
   private String taskType;
   @Schema(
      description = "任务状态：1待执行 2执行中 3执行完成 -1执行失败"
   )
   private Integer taskStatus;
   @Schema(
      description = "任务描述"
   )
   private String taskDesc;
   @Schema(
      description = "操作平台：B管理员 C会员"
   )
   private String platform;
   @Schema(
      description = "任务参数JSON"
   )
   private String params;
   @Schema(
      description = "执行结果"
   )
   private String executeResult;
   @Schema(
      description = "已重试次数"
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

   public String getId() {
      return this.id;
   }

   public String getMasterId() {
      return this.masterId;
   }

   public String getMasterName() {
      return this.masterName;
   }

   public String getHostId() {
      return this.hostId;
   }

   public String getHostName() {
      return this.hostName;
   }

   public String getTaskType() {
      return this.taskType;
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

   public String getExecuteResult() {
      return this.executeResult;
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

   public void setId(final String id) {
      this.id = id;
   }

   public void setMasterId(final String masterId) {
      this.masterId = masterId;
   }

   public void setMasterName(final String masterName) {
      this.masterName = masterName;
   }

   public void setHostId(final String hostId) {
      this.hostId = hostId;
   }

   public void setHostName(final String hostName) {
      this.hostName = hostName;
   }

   public void setTaskType(final String taskType) {
      this.taskType = taskType;
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

   public void setExecuteResult(final String executeResult) {
      this.executeResult = executeResult;
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
}
