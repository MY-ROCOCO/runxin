package com.rxinyun.admin.workorder.modular.workorder.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

public class WorkOrderMessageVO {
   @Schema(
      description = "消息ID"
   )
   private String id;
   @Schema(
      description = "工单ID"
   )
   private String workOrderId;
   @Schema(
      description = "用户ID"
   )
   private Long clientId;
   @Schema(
      description = "用户昵称"
   )
   private String clientNickName;
   @Schema(
      description = "管理员ID"
   )
   private String adminId;
   @Schema(
      description = "管理员昵称"
   )
   private String adminNickName;
   @Schema(
      description = "回复时间"
   )
   private LocalDateTime createTime;
   @Schema(
      description = "消息对话信息"
   )
   private String messageContent;
   @Schema(
      description = "判断是谁发送的消息 (0用户端  1管理端)"
   )
   private Integer status;

   public String getId() {
      return this.id;
   }

   public String getWorkOrderId() {
      return this.workOrderId;
   }

   public Long getClientId() {
      return this.clientId;
   }

   public String getClientNickName() {
      return this.clientNickName;
   }

   public String getAdminId() {
      return this.adminId;
   }

   public String getAdminNickName() {
      return this.adminNickName;
   }

   public LocalDateTime getCreateTime() {
      return this.createTime;
   }

   public String getMessageContent() {
      return this.messageContent;
   }

   public Integer getStatus() {
      return this.status;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setWorkOrderId(final String workOrderId) {
      this.workOrderId = workOrderId;
   }

   public void setClientId(final Long clientId) {
      this.clientId = clientId;
   }

   public void setClientNickName(final String clientNickName) {
      this.clientNickName = clientNickName;
   }

   public void setAdminId(final String adminId) {
      this.adminId = adminId;
   }

   public void setAdminNickName(final String adminNickName) {
      this.adminNickName = adminNickName;
   }

   public void setCreateTime(final LocalDateTime createTime) {
      this.createTime = createTime;
   }

   public void setMessageContent(final String messageContent) {
      this.messageContent = messageContent;
   }

   public void setStatus(final Integer status) {
      this.status = status;
   }
}
