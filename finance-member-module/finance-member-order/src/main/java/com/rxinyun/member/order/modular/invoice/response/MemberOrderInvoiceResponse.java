package com.rxinyun.member.order.modular.invoice.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class MemberOrderInvoiceResponse {
   @Schema(
      description = "主键ID"
   )
   private String id;
   @Schema(
      description = "发票申请号"
   )
   private String invoiceNo;
   @Schema(
      description = "发票抬头"
   )
   private String title;
   @Schema(
      description = "税号"
   )
   private String taxNo;
   @Schema(
      description = "接收邮箱"
   )
   private String email;
   @Schema(
      description = "开票金额"
   )
   private BigDecimal invoiceAmount;
   @Schema(
      description = "明细订单笔数"
   )
   private Integer orderCount;
   @Schema(
      description = "状态：0待审核 1已开具 -1已拒绝"
   )
   private Integer status;
   @Schema(
      description = "拒绝原因"
   )
   private String rejectReason;
   @Schema(
      description = "申请时间"
   )
   private Date applyTime;
   @Schema(
      description = "开具时间"
   )
   private Date issueTime;
   @Schema(
      description = "创建时间"
   )
   private Date createTime;
   @Schema(
      description = "订单明细"
   )
   private List<MemberOrderInvoiceItemResponse> items;

   public String getId() {
      return this.id;
   }

   public String getInvoiceNo() {
      return this.invoiceNo;
   }

   public String getTitle() {
      return this.title;
   }

   public String getTaxNo() {
      return this.taxNo;
   }

   public String getEmail() {
      return this.email;
   }

   public BigDecimal getInvoiceAmount() {
      return this.invoiceAmount;
   }

   public Integer getOrderCount() {
      return this.orderCount;
   }

   public Integer getStatus() {
      return this.status;
   }

   public String getRejectReason() {
      return this.rejectReason;
   }

   public Date getApplyTime() {
      return this.applyTime;
   }

   public Date getIssueTime() {
      return this.issueTime;
   }

   public Date getCreateTime() {
      return this.createTime;
   }

   public List<MemberOrderInvoiceItemResponse> getItems() {
      return this.items;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setInvoiceNo(final String invoiceNo) {
      this.invoiceNo = invoiceNo;
   }

   public void setTitle(final String title) {
      this.title = title;
   }

   public void setTaxNo(final String taxNo) {
      this.taxNo = taxNo;
   }

   public void setEmail(final String email) {
      this.email = email;
   }

   public void setInvoiceAmount(final BigDecimal invoiceAmount) {
      this.invoiceAmount = invoiceAmount;
   }

   public void setOrderCount(final Integer orderCount) {
      this.orderCount = orderCount;
   }

   public void setStatus(final Integer status) {
      this.status = status;
   }

   public void setRejectReason(final String rejectReason) {
      this.rejectReason = rejectReason;
   }

   public void setApplyTime(final Date applyTime) {
      this.applyTime = applyTime;
   }

   public void setIssueTime(final Date issueTime) {
      this.issueTime = issueTime;
   }

   public void setCreateTime(final Date createTime) {
      this.createTime = createTime;
   }

   public void setItems(final List<MemberOrderInvoiceItemResponse> items) {
      this.items = items;
   }
}
