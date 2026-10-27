package com.rxinyun.admin.production.modular.esc.region.param;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;

public class ServerRegionIdParam implements Serializable {
   @Schema(
      description = "地域标识"
   )
   private String id;

   public String getId() {
      return this.id;
   }

   public void setId(final String id) {
      this.id = id;
   }
}
