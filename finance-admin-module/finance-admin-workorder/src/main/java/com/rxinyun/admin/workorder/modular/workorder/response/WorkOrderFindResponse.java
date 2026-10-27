package com.rxinyun.admin.workorder.modular.workorder.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.LocalDateTime;

public class WorkOrderFindResponse implements Serializable {
   @Schema(
      description = "工单标题"
   )
   private String title;
   @Schema(
      description = "关联产品分类"
   )
   private String productName;
   @Schema(
      description = "会员ID"
   )
   private Long clientId;
   @Schema(
      description = "联系电话"
   )
   private String phone;
   @Schema(
      description = "工单创建时间"
   )
   private LocalDateTime orderCreateTime;
   @Schema(
      description = "工单结束日期"
   )
   private LocalDateTime orderEndTime;
   @Schema(
      description = "工单回复日期"
   )
   private LocalDateTime orderAnswerTime;
   @Schema(
      description = "接单客服昵称"
   )
   private String nickname;
   @Schema(
      description = "工单状态 0:未解决 1:已解决"
   )
   private Integer statusOut;
   @Schema(
      description = "关联产品IP"
   )
   private String productIp;

   public void setTitle(final String title) {
      this.title = title;
   }

   public void setProductName(final String productName) {
      this.productName = productName;
   }

   public void setClientId(final Long clientId) {
      this.clientId = clientId;
   }

   public void setPhone(final String phone) {
      this.phone = phone;
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

   public void setNickname(final String nickname) {
      this.nickname = nickname;
   }

   public void setStatusOut(final Integer statusOut) {
      this.statusOut = statusOut;
   }

   public void setProductIp(final String productIp) {
      this.productIp = productIp;
   }

   public String getTitle() {
      return this.title;
   }

   public String getProductName() {
      return this.productName;
   }

   public Long getClientId() {
      return this.clientId;
   }

   public String getPhone() {
      return this.phone;
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

   public String getNickname() {
      return this.nickname;
   }

   public Integer getStatusOut() {
      return this.statusOut;
   }

   public String getProductIp() {
      return this.productIp;
   }
}
