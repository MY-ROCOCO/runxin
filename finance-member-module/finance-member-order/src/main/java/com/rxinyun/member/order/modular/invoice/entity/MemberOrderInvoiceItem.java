package com.rxinyun.member.order.modular.invoice.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.Date;

@TableName(
   value = "ORDER_INVOICE_ITEM",
   autoResultMap = true
)
public class MemberOrderInvoiceItem {
   @Schema(
      description = "主键ID"
   )
   private String id;
   @Schema(
      description = "发票ID"
   )
   private String invoiceId;
   @Schema(
      description = "订单号"
   )
   private String orderNo;
   @Schema(
      description = "会员编号"
   )
   private Long memberNo;
   @Schema(
      description = "该订单申请时实付快照"
   )
   private BigDecimal payPrice;
   @Schema(
      description = "创建时间"
   )
   private Date createTime;

   public String getId() {
      return this.id;
   }

   public String getInvoiceId() {
      return this.invoiceId;
   }

   public String getOrderNo() {
      return this.orderNo;
   }

   public Long getMemberNo() {
      return this.memberNo;
   }

   public BigDecimal getPayPrice() {
      return this.payPrice;
   }

   public Date getCreateTime() {
      return this.createTime;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setInvoiceId(final String invoiceId) {
      this.invoiceId = invoiceId;
   }

   public void setOrderNo(final String orderNo) {
      this.orderNo = orderNo;
   }

   public void setMemberNo(final Long memberNo) {
      this.memberNo = memberNo;
   }

   public void setPayPrice(final BigDecimal payPrice) {
      this.payPrice = payPrice;
   }

   public void setCreateTime(final Date createTime) {
      this.createTime = createTime;
   }
}
