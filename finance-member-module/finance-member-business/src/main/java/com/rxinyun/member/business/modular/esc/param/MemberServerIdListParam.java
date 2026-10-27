package com.rxinyun.member.business.modular.esc.param;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.List;

public class MemberServerIdListParam implements Serializable {
   @Schema(
      description = "云服务器ID"
   )
   private List<String> serverIds;

   public List<String> getServerIds() {
      return this.serverIds;
   }

   public void setServerIds(final List<String> serverIds) {
      this.serverIds = serverIds;
   }
}
