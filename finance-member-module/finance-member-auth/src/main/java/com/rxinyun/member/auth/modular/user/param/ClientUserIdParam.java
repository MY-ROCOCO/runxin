package com.rxinyun.member.auth.modular.user.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class ClientUserIdParam {
   @Schema(
      description = "id",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotNull(
      message = "ID 不能为空"
   )
   @Min(
      value = 1L,
      message = "ID 必须大于0"
   )
   private Long id;

   public Long getId() {
      return this.id;
   }

   public void setId(final Long id) {
      this.id = id;
   }
}
