package com.rxinyun.admin.business.modular.independent.param;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.List;

public class IndependentIdListParam implements Serializable {
   @Schema(
      description = "独立服务器ID"
   )
   private List<String> serverIds;

   public List<String> getServerIds() {
      return this.serverIds;
   }

   public void setServerIds(final List<String> serverIds) {
      this.serverIds = serverIds;
   }
}
