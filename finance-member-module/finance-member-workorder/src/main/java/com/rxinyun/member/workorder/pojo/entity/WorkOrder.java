package com.rxinyun.member.workorder.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@TableName(
   value = "WORK_ORDER",
   autoResultMap = true
)
public class WorkOrder {
   @Schema(
      description = "接单管理员ID"
   )
   private String adminId;
   @Schema(
      description = "会员ID"
   )
   private Long clientId;
   @Schema(
      description = "工单类型ID"
   )
   private String typeId;
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
      description = "工单结束时间  可能为空"
   )
   private LocalDateTime orderEndTime;
   @Schema(
      description = "工单回复时间  可能为空"
   )
   private LocalDateTime orderAnswerTime;
   @Schema(
      description = "工单内容"
   )
   private String content;
   @Schema(
      description = "关联产品分类名"
   )
   private String productName;
   @Schema(
      description = "关联产品IP"
   )
   private String productIp;

   public String getAdminId() {
      return this.adminId;
   }

   public Long getClientId() {
      return this.clientId;
   }

   public String getTypeId() {
      return this.typeId;
   }

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

   public LocalDateTime getOrderEndTime() {
      return this.orderEndTime;
   }

   public LocalDateTime getOrderAnswerTime() {
      return this.orderAnswerTime;
   }

   public String getContent() {
      return this.content;
   }

   public String getProductName() {
      return this.productName;
   }

   public String getProductIp() {
      return this.productIp;
   }

   public void setAdminId(final String adminId) {
      this.adminId = adminId;
   }

   public void setClientId(final Long clientId) {
      this.clientId = clientId;
   }

   public void setTypeId(final String typeId) {
      this.typeId = typeId;
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

   public void setOrderEndTime(final LocalDateTime orderEndTime) {
      this.orderEndTime = orderEndTime;
   }

   public void setOrderAnswerTime(final LocalDateTime orderAnswerTime) {
      this.orderAnswerTime = orderAnswerTime;
   }

   public void setContent(final String content) {
      this.content = content;
   }

   public void setProductName(final String productName) {
      this.productName = productName;
   }

   public void setProductIp(final String productIp) {
      this.productIp = productIp;
   }
}
