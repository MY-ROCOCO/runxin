package com.rxinyun.admin.order.modular.independent.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

public class IndependentBandwidthUpgradeOrderParam implements Serializable {
   @Schema(
      description = "公网带宽"
   )
   @NotNull(
      message = "带宽不能为空"
   )
   @Min(
      value = 1L,
      message = "带宽必须大于0"
   )
   private Integer bandwidth;
   @Schema(
      description = "支付方式(1: 微信, 2: 支付宝, 3: 积分, 4: 余额, 5: USDT)"
   )
   private Integer payType;
   @Schema(
      description = "升级的服务器ID"
   )
   @NotBlank(
      message = "升级的服务器ID不能为空"
   )
   private String serverId;

   public Integer getBandwidth() {
      return this.bandwidth;
   }

   public Integer getPayType() {
      return this.payType;
   }

   public String getServerId() {
      return this.serverId;
   }

   public void setBandwidth(final Integer bandwidth) {
      this.bandwidth = bandwidth;
   }

   public void setPayType(final Integer payType) {
      this.payType = payType;
   }

   public void setServerId(final String serverId) {
      this.serverId = serverId;
   }
}
