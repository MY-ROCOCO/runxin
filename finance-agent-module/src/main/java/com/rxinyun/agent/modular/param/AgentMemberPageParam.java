package com.rxinyun.agent.modular.param;

import io.swagger.v3.oas.annotations.media.Schema;

public class AgentMemberPageParam {
   @Schema(
      description = "代理ID，代理端可不传"
   )
   private String agentId;
   @Schema(
      description = "会员编号"
   )
   private Long memberNo;
   @Schema(
      description = "会员账号"
   )
   private String account;
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

   public Long getMemberNo() {
      return this.memberNo;
   }

   public String getAccount() {
      return this.account;
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

   public void setMemberNo(final Long memberNo) {
      this.memberNo = memberNo;
   }

   public void setAccount(final String account) {
      this.account = account;
   }

   public void setCurrent(final Integer current) {
      this.current = current;
   }

   public void setSize(final Integer size) {
      this.size = size;
   }
}
