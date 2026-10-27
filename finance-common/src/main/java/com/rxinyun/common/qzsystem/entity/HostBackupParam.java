package com.rxinyun.common.qzsystem.entity;

import io.swagger.v3.oas.annotations.media.Schema;

public class HostBackupParam {
   @Schema(
      description = "云服务器ID"
   )
   private String serverId;
   @Schema(
      description = "云主机唯一标识"
   )
   private String hostName;
   @Schema(
      description = "备份名称"
   )
   private String backupName;
   @Schema(
      description = "快照名称"
   )
   private String snapshotName;
   @Schema(
      description = "备份目录"
   )
   private String backupPath;
   @Schema(
      description = "节点通讯地址"
   )
   private String masterIp;
   @Schema(
      description = "通讯密钥"
   )
   private String apiKey;
   @Schema(
      description = "接口端口"
   )
   private Integer apiPort;

   public String getServerId() {
      return this.serverId;
   }

   public String getHostName() {
      return this.hostName;
   }

   public String getBackupName() {
      return this.backupName;
   }

   public String getSnapshotName() {
      return this.snapshotName;
   }

   public String getBackupPath() {
      return this.backupPath;
   }

   public String getMasterIp() {
      return this.masterIp;
   }

   public String getApiKey() {
      return this.apiKey;
   }

   public Integer getApiPort() {
      return this.apiPort;
   }

   public void setServerId(final String serverId) {
      this.serverId = serverId;
   }

   public void setHostName(final String hostName) {
      this.hostName = hostName;
   }

   public void setBackupName(final String backupName) {
      this.backupName = backupName;
   }

   public void setSnapshotName(final String snapshotName) {
      this.snapshotName = snapshotName;
   }

   public void setBackupPath(final String backupPath) {
      this.backupPath = backupPath;
   }

   public void setMasterIp(final String masterIp) {
      this.masterIp = masterIp;
   }

   public void setApiKey(final String apiKey) {
      this.apiKey = apiKey;
   }

   public void setApiPort(final Integer apiPort) {
      this.apiPort = apiPort;
   }
}
