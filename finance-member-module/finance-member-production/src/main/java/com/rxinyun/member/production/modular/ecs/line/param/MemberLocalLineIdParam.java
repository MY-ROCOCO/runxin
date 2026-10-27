package com.rxinyun.member.production.modular.ecs.line.param;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;

public class MemberLocalLineIdParam implements Serializable {
   @Schema(
      description = "线路ID"
   )
   private String id;

   public String getId() {
      return this.id;
   }

   public void setId(final String id) {
      this.id = id;
   }
}
