package com.rxinyun.agent.modular.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class AgentWithdrawApplyParam {
   @Schema(
      description = "提现金额",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotNull(
      message = "提现金额不能为空"
   )
   @DecimalMin(
      value = "0.01",
      message = "提现金额必须大于0"
   )
   private BigDecimal amount;
   @Schema(
      description = "收款人"
   )
   private String accountName;
   @Schema(
      description = "收款账号"
   )
   private String accountNo;
   @Schema(
      description = "开户行"
   )
   private String bankName;
   @Schema(
      description = "申请备注"
   )
   private String remark;

   public BigDecimal getAmount() {
      return this.amount;
   }

   public String getAccountName() {
      return this.accountName;
   }

   public String getAccountNo() {
      return this.accountNo;
   }

   public String getBankName() {
      return this.bankName;
   }

   public String getRemark() {
      return this.remark;
   }

   public void setAmount(final BigDecimal amount) {
      this.amount = amount;
   }

   public void setAccountName(final String accountName) {
      this.accountName = accountName;
   }

   public void setAccountNo(final String accountNo) {
      this.accountNo = accountNo;
   }

   public void setBankName(final String bankName) {
      this.bankName = bankName;
   }

   public void setRemark(final String remark) {
      this.remark = remark;
   }
}
