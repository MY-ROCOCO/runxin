package com.rxinyun.member.order.modular.ecs.param;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;

public class MemberOrderPageParam implements Serializable {
   @Schema(
      description = "订单ID"
   )
   private Long id;
   @Schema(
      description = "订单编号"
   )
   private String orderNo;
   @Schema(
      description = "订单类型"
   )
   private String orderType;
   @Schema(
      description = "订单状态"
   )
   private Integer orderStatus;
   @Schema(
      description = "支付类型"
   )
   private Integer payType;
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
      description = "账号、姓名关键词"
   )
   private String searchKey;

   public Long getId() {
      return this.id;
   }

   public String getOrderNo() {
      return this.orderNo;
   }

   public String getOrderType() {
      return this.orderType;
   }

   public Integer getOrderStatus() {
      return this.orderStatus;
   }

   public Integer getPayType() {
      return this.payType;
   }

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

   public String getSearchKey() {
      return this.searchKey;
   }

   public void setId(final Long id) {
      this.id = id;
   }

   public void setOrderNo(final String orderNo) {
      this.orderNo = orderNo;
   }

   public void setOrderType(final String orderType) {
      this.orderType = orderType;
   }

   public void setOrderStatus(final Integer orderStatus) {
      this.orderStatus = orderStatus;
   }

   public void setPayType(final Integer payType) {
      this.payType = payType;
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

   public void setSearchKey(final String searchKey) {
      this.searchKey = searchKey;
   }
}
