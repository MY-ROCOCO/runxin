package com.rxinyun.member.order.modular.ecs.param;

import java.math.BigDecimal;

public class ServerRenewTaskParams {
   private Integer months;
   private BigDecimal renewPrice;

   public Integer getMonths() {
      return this.months;
   }

   public BigDecimal getRenewPrice() {
      return this.renewPrice;
   }

   public void setMonths(final Integer months) {
      this.months = months;
   }

   public void setRenewPrice(final BigDecimal renewPrice) {
      this.renewPrice = renewPrice;
   }
}
