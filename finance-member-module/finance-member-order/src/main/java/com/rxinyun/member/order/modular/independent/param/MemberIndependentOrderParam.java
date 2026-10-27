package com.rxinyun.member.order.modular.independent.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

public class MemberIndependentOrderParam implements Serializable {
   @Schema(
      description = "订单编号"
   )
   private String orderNo;
   @Schema(
      description = "计费方式：1：包年包月:2：按量计费 "
   )
   @NotNull(
      message = "计费方式不能为空"
   )
   private Integer billingMode;
   @Schema(
      description = "地域ID"
   )
   @NotBlank(
      message = "地域id不能为空"
   )
   private String regionId;
   @Schema(
      description = "线路ID"
   )
   @NotBlank(
      message = "线路ID不能为空"
   )
   private String lineId;
   @Schema(
      description = "机型ID"
   )
   @NotBlank(
      message = "机型id不能为空"
   )
   private String machineId;
   @Schema(
      description = "公网带宽"
   )
   @Min(
      value = 1L,
      message = "带宽必须大于0"
   )
   private Integer bandwidth;
   @Schema(
      description = "防御峰值"
   )
   @Min(
      value = 0L,
      message = "防御必须大于0"
   )
   private Integer defense;
   @Schema(
      description = "IP数量"
   )
   @Min(
      value = 0L,
      message = "IP数量必须大于0"
   )
   private Integer ipSize;
   @Schema(
      description = "登录密码"
   )
   private String loginPassword;
   @Schema(
      description = "确认密码"
   )
   private String loginPassword2;
   @Schema(
      description = "单位 时 s，天 d，月 m，年 y"
   )
   @NotBlank(
      message = "单位不能为空"
   )
   private String unit;
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
      description = "购买数量"
   )
   @NotNull(
      message = "购买数量不能为空"
   )
   @Min(
      value = 1L,
      message = "购买数量必须大于0"
   )
   private Integer number;
   @Schema(
      description = "支付方式(1: 微信, 2: 支付宝, 3: 积分, 4: 余额, 5: USDT)"
   )
   private Integer payType;
   @Schema(
      description = "升级的服务器ID"
   )
   private String serverId;
   @Schema(
      description = "系统分类ID"
   )
   @NotBlank(
      message = "系统分类ID不能为空"
   )
   private String systemClassId;
   @Schema(
      description = "系统版本ID"
   )
   @NotBlank(
      message = "系统版本ID不能为空"
   )
   private String systemId;
   @Schema(
      description = "增值服务"
   )
   private Integer valueAddedGroupId;
   @Schema(
      description = "优惠券编码"
   )
   private String couponCode;

   public String getOrderNo() {
      return this.orderNo;
   }

   public Integer getBillingMode() {
      return this.billingMode;
   }

   public String getRegionId() {
      return this.regionId;
   }

   public String getLineId() {
      return this.lineId;
   }

   public String getMachineId() {
      return this.machineId;
   }

   public Integer getBandwidth() {
      return this.bandwidth;
   }

   public Integer getDefense() {
      return this.defense;
   }

   public Integer getIpSize() {
      return this.ipSize;
   }

   public String getLoginPassword() {
      return this.loginPassword;
   }

   public String getLoginPassword2() {
      return this.loginPassword2;
   }

   public String getUnit() {
      return this.unit;
   }

   public Integer getDuration() {
      return this.duration;
   }

   public Integer getNumber() {
      return this.number;
   }

   public Integer getPayType() {
      return this.payType;
   }

   public String getServerId() {
      return this.serverId;
   }

   public String getSystemClassId() {
      return this.systemClassId;
   }

   public String getSystemId() {
      return this.systemId;
   }

   public Integer getValueAddedGroupId() {
      return this.valueAddedGroupId;
   }

   public String getCouponCode() {
      return this.couponCode;
   }

   public void setOrderNo(final String orderNo) {
      this.orderNo = orderNo;
   }

   public void setBillingMode(final Integer billingMode) {
      this.billingMode = billingMode;
   }

   public void setRegionId(final String regionId) {
      this.regionId = regionId;
   }

   public void setLineId(final String lineId) {
      this.lineId = lineId;
   }

   public void setMachineId(final String machineId) {
      this.machineId = machineId;
   }

   public void setBandwidth(final Integer bandwidth) {
      this.bandwidth = bandwidth;
   }

   public void setDefense(final Integer defense) {
      this.defense = defense;
   }

   public void setIpSize(final Integer ipSize) {
      this.ipSize = ipSize;
   }

   public void setLoginPassword(final String loginPassword) {
      this.loginPassword = loginPassword;
   }

   public void setLoginPassword2(final String loginPassword2) {
      this.loginPassword2 = loginPassword2;
   }

   public void setUnit(final String unit) {
      this.unit = unit;
   }

   public void setDuration(final Integer duration) {
      this.duration = duration;
   }

   public void setNumber(final Integer number) {
      this.number = number;
   }

   public void setPayType(final Integer payType) {
      this.payType = payType;
   }

   public void setServerId(final String serverId) {
      this.serverId = serverId;
   }

   public void setSystemClassId(final String systemClassId) {
      this.systemClassId = systemClassId;
   }

   public void setSystemId(final String systemId) {
      this.systemId = systemId;
   }

   public void setValueAddedGroupId(final Integer valueAddedGroupId) {
      this.valueAddedGroupId = valueAddedGroupId;
   }

   public void setCouponCode(final String couponCode) {
      this.couponCode = couponCode;
   }
}
