package com.rxinyun.admin.order.modular.ecs.param;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;

public class OrderIdParam implements Serializable {
   @Schema(
      description = "地域标识"
   )
   private String id;
   @Schema(
      description = "订单编号"
   )
   private String orderNo;

   public String getId() {
      return this.id;
   }

   public String getOrderNo() {
      return this.orderNo;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setOrderNo(final String orderNo) {
      this.orderNo = orderNo;
   }
}
