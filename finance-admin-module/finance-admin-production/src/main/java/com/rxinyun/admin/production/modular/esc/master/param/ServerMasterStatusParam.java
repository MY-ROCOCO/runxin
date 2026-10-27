package com.rxinyun.admin.production.modular.esc.master.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import java.io.Serializable;

public class ServerMasterStatusParam implements Serializable {
   @Schema(
      description = "宿主机ID",
      requiredMode = RequiredMode.REQUIRED
   )
   private String masterId;

   public String getMasterId() {
      return this.masterId;
   }

   public void setMasterId(final String masterId) {
      this.masterId = masterId;
   }
}
