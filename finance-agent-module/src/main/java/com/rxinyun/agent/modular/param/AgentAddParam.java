package com.rxinyun.agent.modular.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;

public class AgentAddParam {
   @Schema(
      description = "代理编码"
   )
   private String agentCode;
   @Schema(
      description = "代理名称",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "代理名称不能为空"
   )
   private String agentName;
   @Schema(
      description = "联系人"
   )
   private String contactName;
   @Schema(
      description = "联系电话"
   )
   private String contactPhone;
   @Schema(
      description = "备注"
   )
   private String remark;

   public String getAgentCode() {
      return this.agentCode;
   }

   public String getAgentName() {
      return this.agentName;
   }

   public String getContactName() {
      return this.contactName;
   }

   public String getContactPhone() {
      return this.contactPhone;
   }

   public String getRemark() {
      return this.remark;
   }

   public void setAgentCode(final String agentCode) {
      this.agentCode = agentCode;
   }

   public void setAgentName(final String agentName) {
      this.agentName = agentName;
   }

   public void setContactName(final String contactName) {
      this.contactName = contactName;
   }

   public void setContactPhone(final String contactPhone) {
      this.contactPhone = contactPhone;
   }

   public void setRemark(final String remark) {
      this.remark = remark;
   }
}
