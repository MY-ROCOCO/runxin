package com.rxinyun.admin.order.modular.manual.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class ManualCloudCreateOrderParam implements Serializable {
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
      description = "线路ID"
   )
   @NotBlank(
      message = "线路ID不能为空"
   )
   private String lineId;
   @Schema(
      description = "CPU"
   )
   @Min(
      value = 1L,
      message = "CPU必须大于0"
   )
   private Integer cpu;
   @Schema(
      description = "内存"
   )
   @Min(
      value = 1L,
      message = "内存必须大于0"
   )
   private Integer ram;
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
      description = "公网带宽"
   )
   @Min(
      value = 1L,
      message = "公网带宽必须大于0"
   )
   private Integer bandwidth;
   @Schema(
      description = "防御峰值"
   )
   @Min(
      value = 0L,
      message = "防御峰值必须大于0"
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

   public String getLineId() {
      return this.lineId;
   }

   public Integer getCpu() {
      return this.cpu;
   }

   public Integer getRam() {
      return this.ram;
   }

   public Integer getSystemDisk() {
      return this.systemDisk;
   }

   public List<Integer> getDataDisks() {
      return this.dataDisks;
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

   public void setLineId(final String lineId) {
      this.lineId = lineId;
   }

   public void setCpu(final Integer cpu) {
      this.cpu = cpu;
   }

   public void setRam(final Integer ram) {
      this.ram = ram;
   }

   public void setSystemDisk(final Integer systemDisk) {
      this.systemDisk = systemDisk;
   }

   public void setDataDisks(final List<Integer> dataDisks) {
      this.dataDisks = dataDisks;
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
