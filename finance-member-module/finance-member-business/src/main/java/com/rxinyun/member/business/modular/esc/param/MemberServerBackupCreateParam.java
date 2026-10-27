package com.rxinyun.member.business.modular.esc.param;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;

public class MemberServerBackupCreateParam implements Serializable {
   @Schema(
      description = "云服务器ID"
   )
   private String serverId;
   @Schema(
      description = "备份名称"
   )
   private String backupName;

   public String getServerId() {
      return this.serverId;
   }

   public String getBackupName() {
      return this.backupName;
   }

   public void setServerId(final String serverId) {
      this.serverId = serverId;
   }

   public void setBackupName(final String backupName) {
      this.backupName = backupName;
   }
}
