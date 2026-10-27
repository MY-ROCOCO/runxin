package com.rxinyun.admin.workorder.modular.workorder.entity.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public class WorkOrderMessageAddParam {
   @NotBlank
   @Schema(
      description = "工单ID"
   )
   private String workOrderId;
   @NotBlank
   @Schema(
      description = "管理员ID"
   )
   private String adminId;
   @NotBlank
   @Schema(
      description = "消息对话信息"
   )
   private String messageContent;

   public String getWorkOrderId() {
      return this.workOrderId;
   }

   public String getAdminId() {
      return this.adminId;
   }

   public String getMessageContent() {
      return this.messageContent;
   }

   public void setWorkOrderId(final String workOrderId) {
      this.workOrderId = workOrderId;
   }

   public void setAdminId(final String adminId) {
      this.adminId = adminId;
   }

   public void setMessageContent(final String messageContent) {
      this.messageContent = messageContent;
   }
}
