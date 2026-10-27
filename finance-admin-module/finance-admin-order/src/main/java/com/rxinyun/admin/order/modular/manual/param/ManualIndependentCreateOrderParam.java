package com.rxinyun.admin.order.modular.manual.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

public class ManualIndependentCreateOrderParam implements Serializable {
   @Schema(
      description = "会员ID"
   )
   @NotNull(
      message = "会员ID不能为空"
   )
   private Long memberId;
   @Schema(
      description = "扣费模式：1不扣除，2自动计算，3其他金额"
   )
   @NotNull(
      message = "扣费模式不能为空"
   )
   private Integer deductMode;
   @Schema(
      description = "手动扣费金额，deductMode=3 时必填"
   )
   private BigDecimal deductAmount;
   @Schema(
      description = "计费方式：1包年包月:2按量计费"
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
      message = "机型ID不能为空"
   )
   private String machineId;
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
      description = "防御峰值"
   )
   @NotNull(
      message = "防御不能为空"
   )
   @Min(
      value = 0L,
      message = "防御必须大于0"
   )
   private Integer defense;
   @Schema(
      description = "IP数量"
   )
   @NotNull(
      message = "IP数量不能为空"
   )
   @Min(
      value = 0L,
      message = "IP数量必须大于0"
   )
   private Integer ipSize;
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
      description = "单位 时s，天d，月m，年y"
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

   public Long getMemberId() {
      return this.memberId;
   }

   public Integer getDeductMode() {
      return this.deductMode;
   }

   public BigDecimal getDeductAmount() {
      return this.deductAmount;
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

   public String getSystemClassId() {
      return this.systemClassId;
   }

   public String getSystemId() {
      return this.systemId;
   }

   public String getLoginPassword() {
      return this.loginPassword;
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

   public void setMemberId(final Long memberId) {
      this.memberId = memberId;
   }

   public void setDeductMode(final Integer deductMode) {
      this.deductMode = deductMode;
   }

   public void setDeductAmount(final BigDecimal deductAmount) {
      this.deductAmount = deductAmount;
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

   public void setSystemClassId(final String systemClassId) {
      this.systemClassId = systemClassId;
   }

   public void setSystemId(final String systemId) {
      this.systemId = systemId;
   }

   public void setLoginPassword(final String loginPassword) {
      this.loginPassword = loginPassword;
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
}
