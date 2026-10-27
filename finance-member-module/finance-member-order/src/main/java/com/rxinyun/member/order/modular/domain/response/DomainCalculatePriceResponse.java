package com.rxinyun.member.order.modular.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Schema(
   description = "域名价格计算结果"
)
public class DomainCalculatePriceResponse {
   @Schema(
      description = "单项明细"
   )
   private List<DomainCalculatePriceItemVO> items = new ArrayList<>();
   @Schema(
      description = "订单总价"
   )
   private BigDecimal totalPrice = BigDecimal.ZERO;
   @Schema(
      description = "优惠价格"
   )
   private BigDecimal couponPrice = BigDecimal.ZERO;
   @Schema(
      description = "实付金额（预留优惠后）"
   )
   private BigDecimal payPrice = BigDecimal.ZERO;

   public List<DomainCalculatePriceItemVO> getItems() {
      return this.items;
   }

   public BigDecimal getTotalPrice() {
      return this.totalPrice;
   }

   public BigDecimal getCouponPrice() {
      return this.couponPrice;
   }

   public BigDecimal getPayPrice() {
      return this.payPrice;
   }

   public void setItems(final List<DomainCalculatePriceItemVO> items) {
      this.items = items;
   }

   public void setTotalPrice(final BigDecimal totalPrice) {
      this.totalPrice = totalPrice;
   }

   public void setCouponPrice(final BigDecimal couponPrice) {
      this.couponPrice = couponPrice;
   }

   public void setPayPrice(final BigDecimal payPrice) {
      this.payPrice = payPrice;
   }
}
