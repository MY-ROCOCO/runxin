package com.rxinyun.admin.member.modular.profile.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotNull;

public class AdminMemberContactProfileIdParam {
   @Schema(
      description = "主键",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotNull(
      message = "ID不能为空"
   )
   private Long id;

   public Long getId() {
      return this.id;
   }

   public void setId(final Long id) {
      this.id = id;
   }
}
