package com.rxinyun.agent.modular.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

public class AgentBalanceResponse {
   @Schema(
      description = "代理ID"
   )
   private String agentId;
   @Schema(
      description = "代理编码"
   )
   private String agentCode;
   @Schema(
      description = "代理名称"
   )
   private String agentName;
   @Schema(
      description = "当前余额"
   )
   private BigDecimal balance;
   @Schema(
      description = "累计分成"
   )
   private BigDecimal totalCommission;
   @Schema(
      description = "累计退款冲减"
   )
   private BigDecimal totalRefundDeduct;
   @Schema(
      description = "净分成"
   )
   private BigDecimal netCommission;

   public String getAgentId() {
      return this.agentId;
   }

   public String getAgentCode() {
      return this.agentCode;
   }

   public String getAgentName() {
      return this.agentName;
   }

   public BigDecimal getBalance() {
      return this.balance;
   }

   public BigDecimal getTotalCommission() {
      return this.totalCommission;
   }

   public BigDecimal getTotalRefundDeduct() {
      return this.totalRefundDeduct;
   }

   public BigDecimal getNetCommission() {
      return this.netCommission;
   }

   public void setAgentId(final String agentId) {
      this.agentId = agentId;
   }

   public void setAgentCode(final String agentCode) {
      this.agentCode = agentCode;
   }

   public void setAgentName(final String agentName) {
      this.agentName = agentName;
   }

   public void setBalance(final BigDecimal balance) {
      this.balance = balance;
   }

   public void setTotalCommission(final BigDecimal totalCommission) {
      this.totalCommission = totalCommission;
   }

   public void setTotalRefundDeduct(final BigDecimal totalRefundDeduct) {
      this.totalRefundDeduct = totalRefundDeduct;
   }

   public void setNetCommission(final BigDecimal netCommission) {
      this.netCommission = netCommission;
   }
}
