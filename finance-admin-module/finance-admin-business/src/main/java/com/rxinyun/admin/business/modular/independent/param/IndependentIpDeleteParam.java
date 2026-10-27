package com.rxinyun.admin.business.modular.independent.param;

import io.swagger.v3.oas.annotations.media.Schema;

public class IndependentIpDeleteParam {
   @Schema(
      description = "服务器IP标识"
   )
   private String id;

   public String getId() {
      return this.id;
   }

   public void setId(final String id) {
      this.id = id;
   }
}
