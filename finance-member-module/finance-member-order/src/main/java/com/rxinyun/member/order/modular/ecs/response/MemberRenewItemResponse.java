package com.rxinyun.member.order.modular.ecs.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class MemberRenewItemResponse implements Serializable {
   @Schema(
      description = "服务器ID"
   )
   private String serverId;
   @Schema(
      description = "服务器名称"
   )
   private String serverName;
   @Schema(
      description = "线路ID"
   )
   private String lineId;
   @Schema(
      description = "当前到期时间"
   )
   private Date endTime;
   @Schema(
      description = "月续费价格"
   )
   private BigDecimal renewPrice;
   @Schema(
      description = "该项原价"
   )
   private BigDecimal totalPrice;
   @Schema(
      description = "该项实付"
   )
   private BigDecimal payPrice;

   public String getServerId() {
      return this.serverId;
   }

   public String getServerName() {
      return this.serverName;
   }

   public String getLineId() {
      return this.lineId;
   }

   public Date getEndTime() {
      return this.endTime;
   }

   public BigDecimal getRenewPrice() {
      return this.renewPrice;
   }

   public BigDecimal getTotalPrice() {
      return this.totalPrice;
   }

   public BigDecimal getPayPrice() {
      return this.payPrice;
   }

   public void setServerId(final String serverId) {
      this.serverId = serverId;
   }

   public void setServerName(final String serverName) {
      this.serverName = serverName;
   }

   public void setLineId(final String lineId) {
      this.lineId = lineId;
   }

   public void setEndTime(final Date endTime) {
      this.endTime = endTime;
   }

   public void setRenewPrice(final BigDecimal renewPrice) {
      this.renewPrice = renewPrice;
   }

   public void setTotalPrice(final BigDecimal totalPrice) {
      this.totalPrice = totalPrice;
   }

   public void setPayPrice(final BigDecimal payPrice) {
      this.payPrice = payPrice;
   }
}
