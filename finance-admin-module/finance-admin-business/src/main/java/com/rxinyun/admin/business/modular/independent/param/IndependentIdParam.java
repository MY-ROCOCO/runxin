package com.rxinyun.admin.business.modular.independent.param;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import java.io.Serializable;

public class IndependentIdParam implements Serializable {
   @Schema(
      description = "独立服务器ID",
      requiredMode = RequiredMode.REQUIRED
   )
   @JsonAlias({"serverId"})
   private String id;

   public String getId() {
      return this.id;
   }

   @JsonAlias({"serverId"})
   public void setId(final String id) {
      this.id = id;
   }
}
