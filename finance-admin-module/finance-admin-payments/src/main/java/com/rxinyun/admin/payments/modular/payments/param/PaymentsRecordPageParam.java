package com.rxinyun.admin.payments.modular.payments.param;

import io.swagger.v3.oas.annotations.media.Schema;

public class PaymentsRecordPageParam {
   @Schema(
      description = "会员ID"
   )
   private String memberNo;
   @Schema(
      description = "支付方式(1: 微信, 2: 支付宝, 3: 积分, 4: 余额, 5: USDT, 6: 财务调整增加, 7: 财务调整扣减)"
   )
   private Integer payType;
   @Schema(
      description = "订单号"
   )
   private String orderNo;
   @Schema(
      description = "交易流水号"
   )
   private String tradeNo;
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

   public String getMemberNo() {
      return this.memberNo;
   }

   public Integer getPayType() {
      return this.payType;
   }

   public String getOrderNo() {
      return this.orderNo;
   }

   public String getTradeNo() {
      return this.tradeNo;
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

   public void setMemberNo(final String memberNo) {
      this.memberNo = memberNo;
   }

   public void setPayType(final Integer payType) {
      this.payType = payType;
   }

   public void setOrderNo(final String orderNo) {
      this.orderNo = orderNo;
   }

   public void setTradeNo(final String tradeNo) {
      this.tradeNo = tradeNo;
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
