package com.rxinyun.workorder.pojo;

import io.swagger.v3.oas.annotations.media.Schema;

public class WorkOrderStatisticsResponse {
   @Schema(
      description = "未接工单数量"
   )
   private Long noAccepted;
   @Schema(
      description = "沟通中工单数量"
   )
   private Long communicate;
   @Schema(
      description = "已解决工单数量"
   )
   private Long accepted;

   public WorkOrderStatisticsResponse() {
      this.setNoAccepted(0L);
      this.setCommunicate(0L);
      this.setAccepted(0L);
   }

   public Long getNoAccepted() {
      return this.noAccepted;
   }

   public Long getCommunicate() {
      return this.communicate;
   }

   public Long getAccepted() {
      return this.accepted;
   }

   public void setNoAccepted(final Long noAccepted) {
      this.noAccepted = noAccepted;
   }

   public void setCommunicate(final Long communicate) {
      this.communicate = communicate;
   }

   public void setAccepted(final Long accepted) {
      this.accepted = accepted;
   }
}
