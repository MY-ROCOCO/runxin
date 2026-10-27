package com.rxinyun.admin.member.modular.user.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotNull;

public class ClientSwitchLoginTicketParam {
   @Schema(
      description = "会员ID",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotNull(
      message = "会员ID不能为空"
   )
   private Long memberId;

   public Long getMemberId() {
      return this.memberId;
   }

   public void setMemberId(final Long memberId) {
      this.memberId = memberId;
   }
}
