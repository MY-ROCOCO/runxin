package com.rxinyun.admin.business.modular.independent.param;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import java.io.Serializable;

@Schema(
   description = "独立服务器锁定参数"
)
public class IndependentLockParam implements Serializable {
   @Schema(
      description = "独立服务器ID",
      requiredMode = RequiredMode.REQUIRED
   )
   @JsonAlias({"serverId"})
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

   @JsonAlias({"serverId"})
   public void setId(final String id) {
      this.id = id;
   }

   public void setLockReason(final String lockReason) {
      this.lockReason = lockReason;
   }
}
