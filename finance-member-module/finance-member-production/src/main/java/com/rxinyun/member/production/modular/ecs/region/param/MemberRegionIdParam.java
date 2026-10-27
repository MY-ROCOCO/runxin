package com.rxinyun.member.production.modular.ecs.region.param;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;

public class MemberRegionIdParam implements Serializable {
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
