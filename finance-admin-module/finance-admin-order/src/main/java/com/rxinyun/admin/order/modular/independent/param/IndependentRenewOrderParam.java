package com.rxinyun.admin.order.modular.independent.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

public class IndependentRenewOrderParam implements Serializable {
   @Schema(
      description = "订单编号"
   )
   private String orderNo;
   @Schema(
      description = "购买时长"
   )
   @NotNull(
      message = "购买时长不能为空"
   )
   @Min(
      value = 1L,
      message = "购买时长必须大于0"
   )
   private Integer duration;
   @Schema(
      description = "支付方式(1: 微信, 2: 支付宝, 3: 积分, 4: 余额, 5: USDT)"
   )
   private Integer payType;
   @Schema(
      description = "续费的服务器ID（单台）"
   )
   private String serverId;
   @Schema(
      description = "批量续费的服务器ID列表"
   )
   private List<String> serverIds;

   public String getOrderNo() {
      return this.orderNo;
   }

   public Integer getDuration() {
      return this.duration;
   }

   public Integer getPayType() {
      return this.payType;
   }

   public String getServerId() {
      return this.serverId;
   }

   public List<String> getServerIds() {
      return this.serverIds;
   }

   public void setOrderNo(final String orderNo) {
      this.orderNo = orderNo;
   }

   public void setDuration(final Integer duration) {
      this.duration = duration;
   }

   public void setPayType(final Integer payType) {
      this.payType = payType;
   }

   public void setServerId(final String serverId) {
      this.serverId = serverId;
   }

   public void setServerIds(final List<String> serverIds) {
      this.serverIds = serverIds;
   }
}
