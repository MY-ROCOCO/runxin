package com.rxinyun.member.production.modular.domain.template.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import java.io.Serializable;

public class MemberDomainTemplateIdParam implements Serializable {
   @Schema(
      description = "模板ID",
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
