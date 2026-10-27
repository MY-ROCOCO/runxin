package com.rxinyun.agent.modular.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;

public class AgentEditParam {
   @Schema(
      description = "代理ID",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "代理ID不能为空"
   )
   private String id;
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
      description = "状态"
   )
   private String status;
   @Schema(
      description = "备注"
   )
   private String remark;

   public String getId() {
      return this.id;
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

   public String getStatus() {
      return this.status;
   }

   public String getRemark() {
      return this.remark;
   }

   public void setId(final String id) {
      this.id = id;
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

   public void setStatus(final String status) {
      this.status = status;
   }

   public void setRemark(final String remark) {
      this.remark = remark;
   }
}
