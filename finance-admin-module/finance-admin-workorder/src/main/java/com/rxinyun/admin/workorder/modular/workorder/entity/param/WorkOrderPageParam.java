package com.rxinyun.admin.workorder.modular.workorder.entity.param;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

public class WorkOrderPageParam {
   @Schema(
      description = "当前页码"
   )
   private Integer current;
   @Schema(
      description = "每页条数"
   )
   private Integer size;
   @Schema(
      description = "排序字段，字段驼峰名称，如：userName"
   )
   private String sortField;
   @Schema(
      description = "排序方式，升序：ASCEND；降序：DESCEND"
   )
   private String sortOrder;
   @Schema(
      description = "关键字  (工单id  或 标题)"
   )
   private String keyWord;
   @Schema(
      description = "工单状态 0:未解决 1:已解决"
   )
   private Integer statusOut;
   @Schema(
      description = "回复状态  0:用户已回复 1:管理已回复   以下为排序列"
   )
   private String statusReply;
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
      description = "关联产品分类名"
   )
   private String productName;
   @Schema(
      description = "关联产品IP"
   )
   private String productIp;

   public Integer getCurrent() {
      return this.current;
   }

   public Integer getSize() {
      return this.size;
   }

   public String getSortField() {
      return this.sortField;
   }

   public String getSortOrder() {
      return this.sortOrder;
   }

   public String getKeyWord() {
      return this.keyWord;
   }

   public Integer getStatusOut() {
      return this.statusOut;
   }

   public String getStatusReply() {
      return this.statusReply;
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

   public String getProductName() {
      return this.productName;
   }

   public String getProductIp() {
      return this.productIp;
   }

   public void setCurrent(final Integer current) {
      this.current = current;
   }

   public void setSize(final Integer size) {
      this.size = size;
   }

   public void setSortField(final String sortField) {
      this.sortField = sortField;
   }

   public void setSortOrder(final String sortOrder) {
      this.sortOrder = sortOrder;
   }

   public void setKeyWord(final String keyWord) {
      this.keyWord = keyWord;
   }

   public void setStatusOut(final Integer statusOut) {
      this.statusOut = statusOut;
   }

   public void setStatusReply(final String statusReply) {
      this.statusReply = statusReply;
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

   public void setProductName(final String productName) {
      this.productName = productName;
   }

   public void setProductIp(final String productIp) {
      this.productIp = productIp;
   }
}
