package com.rxinyun.marketing.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.math.BigDecimal;

public class MemberCouponQueryRequest implements Serializable {
   @Schema(
      description = "会员编号"
   )
   private Long memberNo;
   @Schema(
      description = "产品类型"
   )
   private Integer productType;
   @Schema(
      description = "订单类型"
   )
   private Integer orderType;
   @Schema(
      description = "线路ID"
   )
   private String lineId;
   @Schema(
      description = "当前订单金额"
   )
   private BigDecimal orderAmount;

   public Long getMemberNo() {
      return this.memberNo;
   }

   public Integer getProductType() {
      return this.productType;
   }

   public Integer getOrderType() {
      return this.orderType;
   }

   public String getLineId() {
      return this.lineId;
   }

   public BigDecimal getOrderAmount() {
      return this.orderAmount;
   }

   public void setMemberNo(final Long memberNo) {
      this.memberNo = memberNo;
   }

   public void setProductType(final Integer productType) {
      this.productType = productType;
   }

   public void setOrderType(final Integer orderType) {
      this.orderType = orderType;
   }

   public void setLineId(final String lineId) {
      this.lineId = lineId;
   }

   public void setOrderAmount(final BigDecimal orderAmount) {
      this.orderAmount = orderAmount;
   }
}
