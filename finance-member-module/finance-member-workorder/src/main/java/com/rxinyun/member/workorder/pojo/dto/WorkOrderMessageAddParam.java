package com.rxinyun.member.workorder.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public class WorkOrderMessageAddParam {
   @NotBlank
   @Schema(
      description = "用户ID"
   )
   private Long clientId;
   @NotBlank
   @Schema(
      description = "工单ID"
   )
   private String workOrderId;
   @NotBlank
   @Schema(
      description = "消息对话信息"
   )
   private String messageContent;

   public Long getClientId() {
      return this.clientId;
   }

   public String getWorkOrderId() {
      return this.workOrderId;
   }

   public String getMessageContent() {
      return this.messageContent;
   }

   public void setClientId(final Long clientId) {
      this.clientId = clientId;
   }

   public void setWorkOrderId(final String workOrderId) {
      this.workOrderId = workOrderId;
   }

   public void setMessageContent(final String messageContent) {
      this.messageContent = messageContent;
   }
}
