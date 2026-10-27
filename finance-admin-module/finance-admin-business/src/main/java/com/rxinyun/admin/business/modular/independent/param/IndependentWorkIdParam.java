package com.rxinyun.admin.business.modular.independent.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;

public class IndependentWorkIdParam implements Serializable {
   @NotBlank(
      message = "工单ID不能为空"
   )
   @Schema(
      description = "人工服务工单ID"
   )
   private String id;

   public String getId() {
      return this.id;
   }

   public void setId(final String id) {
      this.id = id;
   }
}
