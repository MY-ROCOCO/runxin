package com.rxinyun.admin.order.modular.independent.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

public class IndependentIpUpgradeOrderParam implements Serializable {
   @Schema(
      description = "IP数量"
   )
   @NotNull(
      message = "IP数量不能为空"
   )
   private Integer ipSize;
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

   public Integer getIpSize() {
      return this.ipSize;
   }

   public Integer getPayType() {
      return this.payType;
   }

   public String getServerId() {
      return this.serverId;
   }

   public void setIpSize(final Integer ipSize) {
      this.ipSize = ipSize;
   }

   public void setPayType(final Integer payType) {
      this.payType = payType;
   }

   public void setServerId(final String serverId) {
      this.serverId = serverId;
   }
}
