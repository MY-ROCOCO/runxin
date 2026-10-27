package com.rxinyun.admin.business.modular.vhost.task.param;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.List;

public class VhostAutoTaskPageParam implements Serializable {
   @Schema(
      description = "虚拟主机实例ID"
   )
   private String hostId;
   @Schema(
      description = "主机名称，模糊查询"
   )
   private String hostName;
   @Schema(
      description = "主控ID"
   )
   private String masterId;
   @Schema(
      description = "任务类型：CREATE创建 RENEW续费 START开机 STOP关机 DELETE删除"
   )
   private String taskType;
   @Schema(
      description = "任务状态：1待执行 2执行中 3执行完成 -1执行失败"
   )
   private Integer taskStatus;
   @Schema(
      description = "任务状态列表"
   )
   private List<Integer> taskStatusList;
   @Schema(
      description = "操作平台：B管理员 C会员"
   )
   private String platform;
   @Schema(
      description = "关键字，匹配任务描述或主机名称"
   )
   private String searchKey;

   public String getHostId() {
      return this.hostId;
   }

   public String getHostName() {
      return this.hostName;
   }

   public String getMasterId() {
      return this.masterId;
   }

   public String getTaskType() {
      return this.taskType;
   }

   public Integer getTaskStatus() {
      return this.taskStatus;
   }

   public List<Integer> getTaskStatusList() {
      return this.taskStatusList;
   }

   public String getPlatform() {
      return this.platform;
   }

   public String getSearchKey() {
      return this.searchKey;
   }

   public void setHostId(final String hostId) {
      this.hostId = hostId;
   }

   public void setHostName(final String hostName) {
      this.hostName = hostName;
   }

   public void setMasterId(final String masterId) {
      this.masterId = masterId;
   }

   public void setTaskType(final String taskType) {
      this.taskType = taskType;
   }

   public void setTaskStatus(final Integer taskStatus) {
      this.taskStatus = taskStatus;
   }

   public void setTaskStatusList(final List<Integer> taskStatusList) {
      this.taskStatusList = taskStatusList;
   }

   public void setPlatform(final String platform) {
      this.platform = platform;
   }

   public void setSearchKey(final String searchKey) {
      this.searchKey = searchKey;
   }
}
