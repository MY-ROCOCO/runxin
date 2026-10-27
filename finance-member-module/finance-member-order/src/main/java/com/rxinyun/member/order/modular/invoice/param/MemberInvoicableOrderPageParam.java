package com.rxinyun.member.order.modular.invoice.param;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;

public class MemberInvoicableOrderPageParam implements Serializable {
   @Schema(
      description = "订单号"
   )
   private String orderNo;
   @Schema(
      description = "产品类型: 1 云服务器 2 独立服务器 3 域名 4 DNS云解析"
   )
   private Integer productType;
   @Schema(
      description = "支付开始时间"
   )
   private String payTimeStart;
   @Schema(
      description = "支付结束时间"
   )
   private String payTimeEnd;
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

   public String getOrderNo() {
      return this.orderNo;
   }

   public Integer getProductType() {
      return this.productType;
   }

   public String getPayTimeStart() {
      return this.payTimeStart;
   }

   public String getPayTimeEnd() {
      return this.payTimeEnd;
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

   public void setOrderNo(final String orderNo) {
      this.orderNo = orderNo;
   }

   public void setProductType(final Integer productType) {
      this.productType = productType;
   }

   public void setPayTimeStart(final String payTimeStart) {
      this.payTimeStart = payTimeStart;
   }

   public void setPayTimeEnd(final String payTimeEnd) {
      this.payTimeEnd = payTimeEnd;
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
