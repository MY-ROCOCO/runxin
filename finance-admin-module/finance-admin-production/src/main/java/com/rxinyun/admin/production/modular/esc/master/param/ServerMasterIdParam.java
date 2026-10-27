package com.rxinyun.admin.production.modular.esc.master.param;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;

public class ServerMasterIdParam implements Serializable {
   @Schema(
      description = "区域标识"
   )
   private String id;

   public String getId() {
      return this.id;
   }

   public void setId(final String id) {
      this.id = id;
   }
}
