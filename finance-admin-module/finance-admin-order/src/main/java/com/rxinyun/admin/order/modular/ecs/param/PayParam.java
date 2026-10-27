package com.rxinyun.admin.order.modular.ecs.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

public class PayParam implements Serializable {
   @Schema(
      description = "订单编号"
   )
   @NotBlank(
      message = "订单编号不能为空"
   )
   private String orderNo;
   @Schema(
      description = "地域ID"
   )
   private String regionId;
   @Schema(
      description = "可用区ID"
   )
   private String availableId;
   @Schema(
      description = "线路ID"
   )
   @NotBlank(
      message = "线路ID不能为空"
   )
   private String lineId;
   @Schema(
      description = "CPU"
   )
   @NotBlank(
      message = "CPU不能为空"
   )
   private Integer cpu;
   @Schema(
      description = "内存"
   )
   @NotBlank(
      message = "内存不能为空"
   )
   private Integer ram;
   @Schema(
      description = "SSD数据盘"
   )
   private List<Integer> harddisks;
   @Schema(
      description = "公网带宽"
   )
   private Integer bandwidth;
   @Schema(
      description = "防御峰值"
   )
   private Integer defense;
   @Schema(
      description = "IP数量"
   )
   @NotNull(
      message = "IP数量不能为空"
   )
   @Min(
      value = 1L,
      message = "IP数量必须大于0"
   )
   private Integer ipNum;
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
      description = "登录密码"
   )
   private String loginPassword;
   @Schema(
      description = "确认密码"
   )
   private String loginPassword2;
   @Schema(
      description = "购买时长"
   )
   @NotBlank(
      message = "购买时长不能为空"
   )
   private Integer months;
   @Schema(
      description = "购买数量"
   )
   @NotBlank(
      message = "购买数量不能为空"
   )
   private Integer number;
   @Schema(
      description = "增值服务"
   )
   private Integer valueAddedGroupId;
   @Schema(
      description = "优惠券编码"
   )
   private String couponCode;
   @Schema(
      description = "支付方式(1: 微信, 2: 支付宝, 3: 积分, 4: 余额, 5: USDT)"
   )
   @NotBlank(
      message = "支付方式不能为空"
   )
   private Integer payType;

   public String getOrderNo() {
      return this.orderNo;
   }

   public String getRegionId() {
      return this.regionId;
   }

   public String getAvailableId() {
      return this.availableId;
   }

   public String getLineId() {
      return this.lineId;
   }

   public Integer getCpu() {
      return this.cpu;
   }

   public Integer getRam() {
      return this.ram;
   }

   public List<Integer> getHarddisks() {
      return this.harddisks;
   }

   public Integer getBandwidth() {
      return this.bandwidth;
   }

   public Integer getDefense() {
      return this.defense;
   }

   public Integer getIpNum() {
      return this.ipNum;
   }

   public String getSystemClassId() {
      return this.systemClassId;
   }

   public String getSystemId() {
      return this.systemId;
   }

   public String getLoginPassword() {
      return this.loginPassword;
   }

   public String getLoginPassword2() {
      return this.loginPassword2;
   }

   public Integer getMonths() {
      return this.months;
   }

   public Integer getNumber() {
      return this.number;
   }

   public Integer getValueAddedGroupId() {
      return this.valueAddedGroupId;
   }

   public String getCouponCode() {
      return this.couponCode;
   }

   public Integer getPayType() {
      return this.payType;
   }

   public void setOrderNo(final String orderNo) {
      this.orderNo = orderNo;
   }

   public void setRegionId(final String regionId) {
      this.regionId = regionId;
   }

   public void setAvailableId(final String availableId) {
      this.availableId = availableId;
   }

   public void setLineId(final String lineId) {
      this.lineId = lineId;
   }

   public void setCpu(final Integer cpu) {
      this.cpu = cpu;
   }

   public void setRam(final Integer ram) {
      this.ram = ram;
   }

   public void setHarddisks(final List<Integer> harddisks) {
      this.harddisks = harddisks;
   }

   public void setBandwidth(final Integer bandwidth) {
      this.bandwidth = bandwidth;
   }

   public void setDefense(final Integer defense) {
      this.defense = defense;
   }

   public void setIpNum(final Integer ipNum) {
      this.ipNum = ipNum;
   }

   public void setSystemClassId(final String systemClassId) {
      this.systemClassId = systemClassId;
   }

   public void setSystemId(final String systemId) {
      this.systemId = systemId;
   }

   public void setLoginPassword(final String loginPassword) {
      this.loginPassword = loginPassword;
   }

   public void setLoginPassword2(final String loginPassword2) {
      this.loginPassword2 = loginPassword2;
   }

   public void setMonths(final Integer months) {
      this.months = months;
   }

   public void setNumber(final Integer number) {
      this.number = number;
   }

   public void setValueAddedGroupId(final Integer valueAddedGroupId) {
      this.valueAddedGroupId = valueAddedGroupId;
   }

   public void setCouponCode(final String couponCode) {
      this.couponCode = couponCode;
   }

   public void setPayType(final Integer payType) {
      this.payType = payType;
   }
}
