package com.rxinyun.agent.modular.param;

import io.swagger.v3.oas.annotations.media.Schema;

public class AgentOrderPageParam {
   @Schema(
      description = "订单号"
   )
   private String orderNo;
   @Schema(
      description = "会员编号"
   )
   private Long memberNo;
   @Schema(
      description = "订单状态"
   )
   private Integer orderStatus;
   @Schema(
      description = "产品类型"
   )
   private Integer productType;
   @Schema(
      description = "当前页"
   )
   private Integer current;
   @Schema(
      description = "每页条数"
   )
   private Integer size;

   public String getOrderNo() {
      return this.orderNo;
   }

   public Long getMemberNo() {
      return this.memberNo;
   }

   public Integer getOrderStatus() {
      return this.orderStatus;
   }

   public Integer getProductType() {
      return this.productType;
   }

   public Integer getCurrent() {
      return this.current;
   }

   public Integer getSize() {
      return this.size;
   }

   public void setOrderNo(final String orderNo) {
      this.orderNo = orderNo;
   }

   public void setMemberNo(final Long memberNo) {
      this.memberNo = memberNo;
   }

   public void setOrderStatus(final Integer orderStatus) {
      this.orderStatus = orderStatus;
   }

   public void setProductType(final Integer productType) {
      this.productType = productType;
   }

   public void setCurrent(final Integer current) {
      this.current = current;
   }

   public void setSize(final Integer size) {
      this.size = size;
   }
}
