package com.rxinyun.member.workorder.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class WorkOrderMessageEditParam {
   @Schema(
      description = "用户ID"
   )
   private Long clientId;
   @Schema(
      description = "回复时间"
   )
   private String createTime;
   @Schema(
      description = "消息对话信息"
   )
   private String messageContent;

   public Long getClientId() {
      return this.clientId;
   }

   public String getCreateTime() {
      return this.createTime;
   }

   public String getMessageContent() {
      return this.messageContent;
   }

   public void setClientId(final Long clientId) {
      this.clientId = clientId;
   }

   public void setCreateTime(final String createTime) {
      this.createTime = createTime;
   }

   public void setMessageContent(final String messageContent) {
      this.messageContent = messageContent;
   }
}
