package com.rxinyun.admin.member.modular.user.param;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

public class ClientUserDataStatisticsParam {
   @Schema(
      description = "充值金额"
   )
   private BigDecimal rechargeAmount;
   @Schema(
      description = "消费金额"
   )
   private BigDecimal consumeAmount;
   @Schema(
      description = "提交的工单数量"
   )
   private Long workOrderNumber;

   public BigDecimal getRechargeAmount() {
      return this.rechargeAmount;
   }

   public BigDecimal getConsumeAmount() {
      return this.consumeAmount;
   }

   public Long getWorkOrderNumber() {
      return this.workOrderNumber;
   }

   public void setRechargeAmount(final BigDecimal rechargeAmount) {
      this.rechargeAmount = rechargeAmount;
   }

   public void setConsumeAmount(final BigDecimal consumeAmount) {
      this.consumeAmount = consumeAmount;
   }

   public void setWorkOrderNumber(final Long workOrderNumber) {
      this.workOrderNumber = workOrderNumber;
   }
}
