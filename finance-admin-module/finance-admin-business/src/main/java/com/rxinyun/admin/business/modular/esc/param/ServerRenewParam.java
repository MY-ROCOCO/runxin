package com.rxinyun.admin.business.modular.esc.param;

import io.swagger.v3.oas.annotations.media.Schema;

public class ServerRenewParam {
   @Schema(
      description = "订单编号"
   )
   private String orderNo;
   @Schema(
      description = "服务器标识"
   )
   private String serverId;
   @Schema(
      description = "续费月份"
   )
   private Integer months;

   public String getOrderNo() {
      return this.orderNo;
   }

   public String getServerId() {
      return this.serverId;
   }

   public Integer getMonths() {
      return this.months;
   }

   public void setOrderNo(final String orderNo) {
      this.orderNo = orderNo;
   }

   public void setServerId(final String serverId) {
      this.serverId = serverId;
   }

   public void setMonths(final Integer months) {
      this.months = months;
   }
}
