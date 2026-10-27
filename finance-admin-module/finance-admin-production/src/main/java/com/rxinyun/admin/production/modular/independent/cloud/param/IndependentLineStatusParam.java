package com.rxinyun.admin.production.modular.independent.cloud.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public class IndependentLineStatusParam {
   @Schema(
      description = "线路ID"
   )
   private String id;
   @NotBlank
   @Schema(
      description = "线路状态 输入当前状态 (0不显示  1显示)"
   )
   private Integer status;

   public String getId() {
      return this.id;
   }

   public Integer getStatus() {
      return this.status;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setStatus(final Integer status) {
      this.status = status;
   }
}
