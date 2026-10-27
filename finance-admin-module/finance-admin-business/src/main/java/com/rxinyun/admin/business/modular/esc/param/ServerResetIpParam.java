package com.rxinyun.admin.business.modular.esc.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import java.io.Serializable;

@Schema(
   description = "重置云服务器IP参数"
)
public class ServerResetIpParam implements Serializable {
   @Schema(
      description = "云服务器标识",
      requiredMode = RequiredMode.REQUIRED
   )
   private String id;

   public String getId() {
      return this.id;
   }

   public void setId(final String id) {
      this.id = id;
   }
}
