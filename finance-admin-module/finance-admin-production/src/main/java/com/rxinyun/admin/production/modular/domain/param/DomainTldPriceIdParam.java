package com.rxinyun.admin.production.modular.domain.param;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;

public class DomainTldPriceIdParam implements Serializable {
   @Schema(
      description = "主键ID"
   )
   private String id;

   public String getId() {
      return this.id;
   }

   public void setId(final String id) {
      this.id = id;
   }
}
