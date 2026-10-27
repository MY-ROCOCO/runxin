package com.rxinyun.member.business.modular.independent.param;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;

public class MemberIndependentIdParam implements Serializable {
   @Schema(
      description = "云服务器ID"
   )
   private String id;

   public String getId() {
      return this.id;
   }

   public void setId(final String id) {
      this.id = id;
   }
}
