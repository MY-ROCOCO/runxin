package com.rxinyun.agent.modular.param;

import io.swagger.v3.oas.annotations.media.Schema;

public class AgentWithdrawPageParam {
   @Schema(
      description = "代理ID，代理端可不传"
   )
   private String agentId;
   @Schema(
      description = "提现单号"
   )
   private String withdrawNo;
   @Schema(
      description = "提现状态"
   )
   private String status;
   @Schema(
      description = "当前页"
   )
   private Integer current;
   @Schema(
      description = "每页条数"
   )
   private Integer size;

   public String getAgentId() {
      return this.agentId;
   }

   public String getWithdrawNo() {
      return this.withdrawNo;
   }

   public String getStatus() {
      return this.status;
   }

   public Integer getCurrent() {
      return this.current;
   }

   public Integer getSize() {
      return this.size;
   }

   public void setAgentId(final String agentId) {
      this.agentId = agentId;
   }

   public void setWithdrawNo(final String withdrawNo) {
      this.withdrawNo = withdrawNo;
   }

   public void setStatus(final String status) {
      this.status = status;
   }

   public void setCurrent(final Integer current) {
      this.current = current;
   }

   public void setSize(final Integer size) {
      this.size = size;
   }
}
