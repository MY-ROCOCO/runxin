package com.rxinyun.member.order.modular.vhost.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class VhostUpgradeTrafficParam {
   @NotBlank(
      message = "虚拟主机不能为空"
   )
   @Schema(
      description = "虚拟主机实例ID",
      requiredMode = RequiredMode.REQUIRED
   )
   private String hostId;
   @NotNull(
      message = "购买流量不能为空"
   )
   @Min(
      value = 1L,
      message = "购买流量必须大于0"
   )
   @Schema(
      description = "本次升级购买的流量，单位G",
      requiredMode = RequiredMode.REQUIRED
   )
   private Integer trafficSize;
   @Schema(
      description = "优惠券编码"
   )
   private String couponCode;

   public String getHostId() {
      return this.hostId;
   }

   public Integer getTrafficSize() {
      return this.trafficSize;
   }

   public String getCouponCode() {
      return this.couponCode;
   }

   public void setHostId(final String hostId) {
      this.hostId = hostId;
   }

   public void setTrafficSize(final Integer trafficSize) {
      this.trafficSize = trafficSize;
   }

   public void setCouponCode(final String couponCode) {
      this.couponCode = couponCode;
   }
}
