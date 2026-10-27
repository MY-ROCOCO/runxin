package com.rxinyun.agent.modular.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class AgentBindParam {
   @Schema(
      description = "代理ID",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "代理ID不能为空"
   )
   private String agentId;
   @Schema(
      description = "会员编号",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotNull(
      message = "会员编号不能为空"
   )
   private Long memberNo;

   public String getAgentId() {
      return this.agentId;
   }

   public Long getMemberNo() {
      return this.memberNo;
   }

   public void setAgentId(final String agentId) {
      this.agentId = agentId;
   }

   public void setMemberNo(final Long memberNo) {
      this.memberNo = memberNo;
   }
}
