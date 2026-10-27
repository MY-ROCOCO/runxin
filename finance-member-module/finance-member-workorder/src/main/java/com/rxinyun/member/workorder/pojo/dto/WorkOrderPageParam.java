package com.rxinyun.member.workorder.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
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
   @NotBlank
   @Schema(
      description = "工单ID"
   )
   private String id;
   @NotBlank
   @Schema(
      description = "工单类型ID"
   )
   private String typeId;
   @Schema(
      description = "工单标题"
   )
   private String title;
   @NotBlank
   @Schema(
      description = "会员ID"
   )
   private Long clientId;
   @Schema(
      description = "回复状态 0:未回复 1:已回复 "
   )
   private Integer statusReply;
   @Schema(
      description = "工单状态 0:未解决 1:已解决 "
   )
   private Integer statusOut;
   @Schema(
      description = "关键字 (工单id  或  标题)"
   )
   private String keyWord;
   @Schema(
      description = "工单创建时间"
   )
   private LocalDateTime orderCreateTime;
   @Schema(
      description = "工单结束时间  可能为空  以下为排序字段"
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

   public String getId() {
      return this.id;
   }

   public String getTypeId() {
      return this.typeId;
   }

   public String getTitle() {
      return this.title;
   }

   public Long getClientId() {
      return this.clientId;
   }

   public Integer getStatusReply() {
      return this.statusReply;
   }

   public Integer getStatusOut() {
      return this.statusOut;
   }

   public String getKeyWord() {
      return this.keyWord;
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

   public void setId(final String id) {
      this.id = id;
   }

   public void setTypeId(final String typeId) {
      this.typeId = typeId;
   }

   public void setTitle(final String title) {
      this.title = title;
   }

   public void setClientId(final Long clientId) {
      this.clientId = clientId;
   }

   public void setStatusReply(final Integer statusReply) {
      this.statusReply = statusReply;
   }

   public void setStatusOut(final Integer statusOut) {
      this.statusOut = statusOut;
   }

   public void setKeyWord(final String keyWord) {
      this.keyWord = keyWord;
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
}
