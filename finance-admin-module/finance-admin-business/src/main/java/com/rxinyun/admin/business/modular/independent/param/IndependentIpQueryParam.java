package com.rxinyun.admin.business.modular.independent.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import java.io.Serializable;

@Schema(
   description = "独立服务器IP查询参数"
)
public class IndependentIpQueryParam implements Serializable {
   @Schema(
      description = "独立服务器ID",
      requiredMode = RequiredMode.REQUIRED
   )
   private String serverId;
   @Schema(
      description = "服务器IP类型：公网 PUBLIC，私网 PRIVATE"
   )
   private String type;

   public String getServerId() {
      return this.serverId;
   }

   public String getType() {
      return this.type;
   }

   public void setServerId(final String serverId) {
      this.serverId = serverId;
   }

   public void setType(final String type) {
      this.type = type;
   }
}
