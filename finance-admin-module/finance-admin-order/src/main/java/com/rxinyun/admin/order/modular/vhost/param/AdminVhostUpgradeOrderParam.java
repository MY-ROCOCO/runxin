package com.rxinyun.admin.order.modular.vhost.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class AdminVhostUpgradeOrderParam extends AdminVhostUpgradeParam {
   @NotNull(
      message = "扣费模式不能为空"
   )
   @Schema(
      description = "扣费模式：1不扣除，2自动计算，3其他金额",
      requiredMode = RequiredMode.REQUIRED
   )
   private Integer deductMode;
   @Schema(
      description = "手动扣费金额，deductMode=3 时必填"
   )
   private BigDecimal deductAmount;

   public Integer getDeductMode() {
      return this.deductMode;
   }

   public BigDecimal getDeductAmount() {
      return this.deductAmount;
   }

   public void setDeductMode(final Integer deductMode) {
      this.deductMode = deductMode;
   }

   public void setDeductAmount(final BigDecimal deductAmount) {
      this.deductAmount = deductAmount;
   }
}
