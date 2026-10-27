package com.rxinyun.admin.business.modular.esc.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import java.io.Serializable;

@Schema(
   description = "云服务器锁定参数"
)
public class ServerLockParam implements Serializable {
   @Schema(
      description = "云服务器标识",
      requiredMode = RequiredMode.REQUIRED
   )
   private String id;
   @Schema(
      description = "锁定原因",
      requiredMode = RequiredMode.REQUIRED
   )
   private String lockReason;

   public String getId() {
      return this.id;
   }

   public String getLockReason() {
      return this.lockReason;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setLockReason(final String lockReason) {
      this.lockReason = lockReason;
   }
}
