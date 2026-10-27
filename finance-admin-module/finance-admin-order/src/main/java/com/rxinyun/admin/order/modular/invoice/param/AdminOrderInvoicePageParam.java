package com.rxinyun.admin.order.modular.invoice.param;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;

public class AdminOrderInvoicePageParam implements Serializable {
   @Schema(
      description = "发票申请号"
   )
   private String invoiceNo;
   @Schema(
      description = "订单号"
   )
   private String orderNo;
   @Schema(
      description = "会员编号"
   )
   private Long memberNo;
   @Schema(
      description = "状态：0待审核 1已开具 -1已拒绝"
   )
   private Integer status;
   @Schema(
      description = "申请开始时间"
   )
   private String applyTimeStart;
   @Schema(
      description = "申请结束时间"
   )
   private String applyTimeEnd;
   @Schema(
      description = "当前页码"
   )
   private Integer current;
   @Schema(
      description = "每页条数"
   )
   private Integer size;
   @Schema(
      description = "排序字段"
   )
   private String sortField;
   @Schema(
      description = "排序方式，升序：ASCEND；降序：DESCEND"
   )
   private String sortOrder;

   public String getInvoiceNo() {
      return this.invoiceNo;
   }

   public String getOrderNo() {
      return this.orderNo;
   }

   public Long getMemberNo() {
      return this.memberNo;
   }

   public Integer getStatus() {
      return this.status;
   }

   public String getApplyTimeStart() {
      return this.applyTimeStart;
   }

   public String getApplyTimeEnd() {
      return this.applyTimeEnd;
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

   public void setInvoiceNo(final String invoiceNo) {
      this.invoiceNo = invoiceNo;
   }

   public void setOrderNo(final String orderNo) {
      this.orderNo = orderNo;
   }

   public void setMemberNo(final Long memberNo) {
      this.memberNo = memberNo;
   }

   public void setStatus(final Integer status) {
      this.status = status;
   }

   public void setApplyTimeStart(final String applyTimeStart) {
      this.applyTimeStart = applyTimeStart;
   }

   public void setApplyTimeEnd(final String applyTimeEnd) {
      this.applyTimeEnd = applyTimeEnd;
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
}
