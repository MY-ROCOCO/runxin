package com.rxinyun.admin.order.modular.ecs.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

public class ServerRefundRecordIdParam implements Serializable {
   @NotBlank(
      message = "退款记录ID不能为空"
   )
   @Schema(
      description = "退款记录ID"
   )
   private String id;
   @NotNull(
      message = "审核状态不能为空"
   )
   @Schema(
      description = "审核状态：1同意退款 -1拒绝退款"
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
