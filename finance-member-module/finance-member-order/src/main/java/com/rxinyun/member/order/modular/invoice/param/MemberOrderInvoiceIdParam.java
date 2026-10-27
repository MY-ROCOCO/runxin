package com.rxinyun.member.order.modular.invoice.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;

public class MemberOrderInvoiceIdParam {
   @Schema(
      description = "发票ID",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "发票ID不能为空"
   )
   private String id;

   public String getId() {
      return this.id;
   }

   public void setId(final String id) {
      this.id = id;
   }
}
