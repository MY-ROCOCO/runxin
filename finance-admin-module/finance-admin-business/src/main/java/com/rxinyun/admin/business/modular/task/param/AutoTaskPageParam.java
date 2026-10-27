package com.rxinyun.admin.business.modular.task.param;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.List;

@Schema(
   description = "自动化任务分页查询参数"
)
public class AutoTaskPageParam implements Serializable {
   @Schema(
      description = "服务器ID"
   )
   private String serverId;
   @Schema(
      description = "云服务器IP（公网/私网/NAT，模糊匹配）"
   )
   private String serverIp;
   @Schema(
      description = "宿主机ID"
   )
   private String masterId;
   @Schema(
      description = "任务类型"
   )
   private String taskType;
   @Schema(
      description = "任务状态: 1:待执行 2:执行中 3:执行完成 -1:执行失败"
   )
   private Integer taskStatus;
   @Schema(
      description = "任务状态列表"
   )
   private List<Integer> taskStatusList;
   @Schema(
      description = "操作平台: B 管理员操作, C 会员操作"
   )
   private String platform;
   @Schema(
      description = "当前页码"
   )
   private Integer current;
   @Schema(
      description = "每页条数"
   )
   private Integer size;
   @Schema(
      description = "排序字段"
   )
   private String sortField;
   @Schema(
      description = "排序方式，升序：ASCEND；降序：DESCEND"
   )
   private String sortOrder;
   @Schema(
      description = "搜索关键词"
   )
   private String searchKey;

   public String getServerId() {
      return this.serverId;
   }

   public String getServerIp() {
      return this.serverIp;
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

   public Integer getCurrent() {
      return this.current;
   }

   public Integer getSize() {
      return this.size;
   }

   public String getSortField() {
      return this.sortField;
   }

   public String getSortOrder() {
      return this.sortOrder;
   }

   public String getSearchKey() {
      return this.searchKey;
   }

   public void setServerId(final String serverId) {
      this.serverId = serverId;
   }

   public void setServerIp(final String serverIp) {
      this.serverIp = serverIp;
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

   public void setCurrent(final Integer current) {
      this.current = current;
   }

   public void setSize(final Integer size) {
      this.size = size;
   }

   public void setSortField(final String sortField) {
      this.sortField = sortField;
   }

   public void setSortOrder(final String sortOrder) {
      this.sortOrder = sortOrder;
   }

   public void setSearchKey(final String searchKey) {
      this.searchKey = searchKey;
   }
}
