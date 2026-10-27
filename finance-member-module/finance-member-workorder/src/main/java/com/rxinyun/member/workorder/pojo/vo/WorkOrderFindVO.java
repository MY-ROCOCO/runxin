package com.rxinyun.member.workorder.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

public class WorkOrderFindVO {
   @Schema(
      description = "工单ID"
   )
   private String id;
   @Schema(
      description = "工单标题"
   )
   private String title;
   @Schema(
      description = "回复状态 0:未回复 1:已回复"
   )
   private Integer statusReply;
   @Schema(
      description = "工单状态 0:未解决 1:已解决"
   )
   private Integer statusOut;
   @Schema(
      description = "工单创建时间"
   )
   private LocalDateTime orderCreateTime;
   @Schema(
      description = "关联产品分类名"
   )
   private String productName;
   @Schema(
      description = "关联产品IP"
   )
   private String productIp;

   public String getId() {
      return this.id;
   }

   public String getTitle() {
      return this.title;
   }

   public Integer getStatusReply() {
      return this.statusReply;
   }

   public Integer getStatusOut() {
      return this.statusOut;
   }

   public LocalDateTime getOrderCreateTime() {
      return this.orderCreateTime;
   }

   public String getProductName() {
      return this.productName;
   }

   public String getProductIp() {
      return this.productIp;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setTitle(final String title) {
      this.title = title;
   }

   public void setStatusReply(final Integer statusReply) {
      this.statusReply = statusReply;
   }

   public void setStatusOut(final Integer statusOut) {
      this.statusOut = statusOut;
   }

   public void setOrderCreateTime(final LocalDateTime orderCreateTime) {
      this.orderCreateTime = orderCreateTime;
   }

   public void setProductName(final String productName) {
      this.productName = productName;
   }

   public void setProductIp(final String productIp) {
      this.productIp = productIp;
   }
}
