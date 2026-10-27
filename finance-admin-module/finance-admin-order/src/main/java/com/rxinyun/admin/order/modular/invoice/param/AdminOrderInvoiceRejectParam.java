package com.rxinyun.admin.order.modular.invoice.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;

public class AdminOrderInvoiceRejectParam {
   @Schema(
      description = "发票ID",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "发票ID不能为空"
   )
   private String id;
   @Schema(
      description = "拒绝原因",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "拒绝原因不能为空"
   )
   private String rejectReason;

   public String getId() {
      return this.id;
   }

   public String getRejectReason() {
      return this.rejectReason;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setRejectReason(final String rejectReason) {
      this.rejectReason = rejectReason;
   }
}
