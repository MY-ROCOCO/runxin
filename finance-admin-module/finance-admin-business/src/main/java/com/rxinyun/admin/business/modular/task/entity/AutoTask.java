package com.rxinyun.admin.business.modular.task.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;

@TableName(
   value = "AUTO_TASK",
   autoResultMap = true
)
public class AutoTask {
   @Schema(
      description = "任务标识"
   )
   private String id;
   @Schema(
      description = "宿主机ID"
   )
   private String masterId;
   @TableField(
      exist = false
   )
   @Schema(
      description = "宿主机IP（按 masterId 关联 server_master 查询，不落库）"
   )
   private String masterIp;
   @TableField(
      exist = false
   )
   @Schema(
      description = "宿主机名称（按 masterId 关联 server_master 查询，不落库）"
   )
   private String masterName;
   @TableField(
      exist = false
   )
   @Schema(
      description = "宿主机受控版本：1=轻舟 Hyper-V受控 2=魔方云受控 3=RkHyper-v受控"
   )
   private String guardVersion;
   @Schema(
      description = "服务器ID"
   )
   private String serverId;
   @TableField(
      exist = false
   )
   @Schema(
      description = "服务器IP（按 serverId 关联主表查询，不落库）"
   )
   private String serverIp;
   @Schema(
      description = "产品类型: 1 云服务器  2 独立服务器"
   )
   private Integer productType;
   @Schema(
      description = "任务类型: 创建 续费 重启 停止 升级 重建 删除"
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
      description = "创建人"
   )
   @TableField(
      fill = FieldFill.INSERT
   )
   private String createUser;
   @Schema(
      description = "执行结果"
   )
   private String executeResult;
   @TableField(
      exist = false
   )
   @Schema(
      description = "回调动作"
   )
   private String callbackAction;
   @TableField(
      exist = false
   )
   @Schema(
      description = "回调状态（0-失败，1-成功）"
   )
   private Integer callbackStatus;
   @TableField(
      exist = false
   )
   @Schema(
      description = "回调时间"
   )
   private Date callbackTime;
   @TableField(
      exist = false
   )
   @Schema(
      description = "回调错误信息"
   )
   private String callbackErrorMsg;
   @TableField(
      exist = false
   )
   @Schema(
      description = "回调请求参数"
   )
   private String callbackRequestData;
   @TableField(
      exist = false
   )
   @Schema(
      description = "回调响应结果"
   )
   private String callbackResponseData;
   @TableField(
      exist = false
   )
   @Schema(
      description = "回调信息摘要"
   )
   private String callbackInfo;
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

   public String getId() {
      return this.id;
   }

   public String getMasterId() {
      return this.masterId;
   }

   public String getMasterIp() {
      return this.masterIp;
   }

   public String getMasterName() {
      return this.masterName;
   }

   public String getGuardVersion() {
      return this.guardVersion;
   }

   public String getServerId() {
      return this.serverId;
   }

   public String getServerIp() {
      return this.serverIp;
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

   public String getCreateUser() {
      return this.createUser;
   }

   public String getExecuteResult() {
      return this.executeResult;
   }

   public String getCallbackAction() {
      return this.callbackAction;
   }

   public Integer getCallbackStatus() {
      return this.callbackStatus;
   }

   public Date getCallbackTime() {
      return this.callbackTime;
   }

   public String getCallbackErrorMsg() {
      return this.callbackErrorMsg;
   }

   public String getCallbackRequestData() {
      return this.callbackRequestData;
   }

   public String getCallbackResponseData() {
      return this.callbackResponseData;
   }

   public String getCallbackInfo() {
      return this.callbackInfo;
   }

   public Date getCreateTime() {
      return this.createTime;
   }

   public Date getUpdateTime() {
      return this.updateTime;
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

   public void setId(final String id) {
      this.id = id;
   }

   public void setMasterId(final String masterId) {
      this.masterId = masterId;
   }

   public void setMasterIp(final String masterIp) {
      this.masterIp = masterIp;
   }

   public void setMasterName(final String masterName) {
      this.masterName = masterName;
   }

   public void setGuardVersion(final String guardVersion) {
      this.guardVersion = guardVersion;
   }

   public void setServerId(final String serverId) {
      this.serverId = serverId;
   }

   public void setServerIp(final String serverIp) {
      this.serverIp = serverIp;
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

   public void setCreateUser(final String createUser) {
      this.createUser = createUser;
   }

   public void setExecuteResult(final String executeResult) {
      this.executeResult = executeResult;
   }

   public void setCallbackAction(final String callbackAction) {
      this.callbackAction = callbackAction;
   }

   public void setCallbackStatus(final Integer callbackStatus) {
      this.callbackStatus = callbackStatus;
   }

   public void setCallbackTime(final Date callbackTime) {
      this.callbackTime = callbackTime;
   }

   public void setCallbackErrorMsg(final String callbackErrorMsg) {
      this.callbackErrorMsg = callbackErrorMsg;
   }

   public void setCallbackRequestData(final String callbackRequestData) {
      this.callbackRequestData = callbackRequestData;
   }

   public void setCallbackResponseData(final String callbackResponseData) {
      this.callbackResponseData = callbackResponseData;
   }

   public void setCallbackInfo(final String callbackInfo) {
      this.callbackInfo = callbackInfo;
   }

   public void setCreateTime(final Date createTime) {
      this.createTime = createTime;
   }

   public void setUpdateTime(final Date updateTime) {
      this.updateTime = updateTime;
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
