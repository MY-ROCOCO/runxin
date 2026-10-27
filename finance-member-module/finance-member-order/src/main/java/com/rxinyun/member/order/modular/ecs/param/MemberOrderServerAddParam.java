package com.rxinyun.member.order.modular.ecs.param;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;

public class MemberOrderServerAddParam implements Serializable {
   @Schema(
      description = "订单编号"
   )
   private String orderNo;
   @Schema(
      description = "服务器ID"
   )
   private String serverId;
   @Schema(
      description = "服务器名称"
   )
   private String serverName;

   public String getOrderNo() {
      return this.orderNo;
   }

   public String getServerId() {
      return this.serverId;
   }

   public String getServerName() {
      return this.serverName;
   }

   public void setOrderNo(final String orderNo) {
      this.orderNo = orderNo;
   }

   public void setServerId(final String serverId) {
      this.serverId = serverId;
   }

   public void setServerName(final String serverName) {
      this.serverName = serverName;
   }
}
