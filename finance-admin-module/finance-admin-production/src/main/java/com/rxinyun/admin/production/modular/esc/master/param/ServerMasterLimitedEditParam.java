package com.rxinyun.admin.production.modular.esc.master.param;

import io.swagger.v3.oas.annotations.media.Schema;

public class ServerMasterLimitedEditParam {
   @Schema(
      description = "宿主机标识"
   )
   private String id;
   @Schema(
      description = "公网交换机名称"
   )
   private String publicSwitchName;
   @Schema(
      description = "私网交换机名称"
   )
   private String privateSwitchName;
   @Schema(
      description = "镜像文件存放目录"
   )
   private String mirrorPath;
   @Schema(
      description = "任务并发数"
   )
   private int taskConcurrency;

   public String getId() {
      return this.id;
   }

   public String getPublicSwitchName() {
      return this.publicSwitchName;
   }

   public String getPrivateSwitchName() {
      return this.privateSwitchName;
   }

   public String getMirrorPath() {
      return this.mirrorPath;
   }

   public int getTaskConcurrency() {
      return this.taskConcurrency;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setPublicSwitchName(final String publicSwitchName) {
      this.publicSwitchName = publicSwitchName;
   }

   public void setPrivateSwitchName(final String privateSwitchName) {
      this.privateSwitchName = privateSwitchName;
   }

   public void setMirrorPath(final String mirrorPath) {
      this.mirrorPath = mirrorPath;
   }

   public void setTaskConcurrency(final int taskConcurrency) {
      this.taskConcurrency = taskConcurrency;
   }
}
