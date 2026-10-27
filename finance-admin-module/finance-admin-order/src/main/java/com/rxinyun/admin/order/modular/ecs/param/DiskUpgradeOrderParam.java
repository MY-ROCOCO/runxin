package com.rxinyun.admin.order.modular.ecs.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

public class DiskUpgradeOrderParam implements Serializable {
   @Schema(
      description = "系统盘"
   )
   @NotNull(
      message = "系统盘不能为空"
   )
   private Integer systemDisk;
   @Schema(
      description = "数据盘"
   )
   @NotNull(
      message = "硬盘列表不能为空"
   )
   private List<Integer> dataDisks;
   @Schema(
      description = "是否使用优惠券 (1: 是, 0: 否)"
   )
   private Integer orderCoupon;
   @Schema(
      description = "优惠券编码"
   )
   private String couponCode;
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

   public Integer getSystemDisk() {
      return this.systemDisk;
   }

   public List<Integer> getDataDisks() {
      return this.dataDisks;
   }

   public Integer getOrderCoupon() {
      return this.orderCoupon;
   }

   public String getCouponCode() {
      return this.couponCode;
   }

   public Integer getPayType() {
      return this.payType;
   }

   public String getServerId() {
      return this.serverId;
   }

   public void setSystemDisk(final Integer systemDisk) {
      this.systemDisk = systemDisk;
   }

   public void setDataDisks(final List<Integer> dataDisks) {
      this.dataDisks = dataDisks;
   }

   public void setOrderCoupon(final Integer orderCoupon) {
      this.orderCoupon = orderCoupon;
   }

   public void setCouponCode(final String couponCode) {
      this.couponCode = couponCode;
   }

   public void setPayType(final Integer payType) {
      this.payType = payType;
   }

   public void setServerId(final String serverId) {
      this.serverId = serverId;
   }
}
