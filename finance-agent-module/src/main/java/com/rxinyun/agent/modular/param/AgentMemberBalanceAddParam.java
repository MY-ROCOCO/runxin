package com.rxinyun.agent.modular.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class AgentMemberBalanceAddParam {
   @Schema(
      description = "会员编号",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotNull(
      message = "会员编号不能为空"
   )
   private Long memberNo;
   @Schema(
      description = "充值金额",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotNull(
      message = "充值金额不能为空"
   )
   @DecimalMin(
      value = "0.01",
      message = "充值金额必须大于0"
   )
   private BigDecimal amount;
   @Schema(
      description = "备注"
   )
   private String remark;

   public Long getMemberNo() {
      return this.memberNo;
   }

   public BigDecimal getAmount() {
      return this.amount;
   }

   public String getRemark() {
      return this.remark;
   }

   public void setMemberNo(final Long memberNo) {
      this.memberNo = memberNo;
   }

   public void setAmount(final BigDecimal amount) {
      this.amount = amount;
   }

   public void setRemark(final String remark) {
      this.remark = remark;
   }
}
