package com.rxinyun.admin.dev.modular.api.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(
   description = "资源池ID参数"
)
public class ResourcePoolIdParam {
   @Schema(
      description = "主键ID"
   )
   @NotBlank(
      message = "ID不能为空"
   )
   private String id;

   public String getId() {
      return this.id;
   }

   public void setId(final String id) {
      this.id = id;
   }
}
