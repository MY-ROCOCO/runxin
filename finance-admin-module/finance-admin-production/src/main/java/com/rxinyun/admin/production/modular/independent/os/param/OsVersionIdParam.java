package com.rxinyun.admin.production.modular.independent.os.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;

public class OsVersionIdParam implements Serializable {
   @NotBlank(
      message = "标识不能为空"
   )
   @Schema(
      description = "标识"
   )
   private String id;

   public String getId() {
      return this.id;
   }

   public void setId(final String id) {
      this.id = id;
   }
}
