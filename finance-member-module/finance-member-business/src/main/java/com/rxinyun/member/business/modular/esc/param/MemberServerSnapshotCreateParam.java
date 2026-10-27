package com.rxinyun.member.business.modular.esc.param;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;

public class MemberServerSnapshotCreateParam implements Serializable {
   @Schema(
      description = "云服务器ID"
   )
   private String serverId;
   @Schema(
      description = "快照名称（后端自动生成，创建时无需传入）"
   )
   private String snapshotName;

   public String getServerId() {
      return this.serverId;
   }

   public String getSnapshotName() {
      return this.snapshotName;
   }

   public void setServerId(final String serverId) {
      this.serverId = serverId;
   }

   public void setSnapshotName(final String snapshotName) {
      this.snapshotName = snapshotName;
   }
}
