package com.rxinyun.admin.order.modular.manual.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

public class ManualCloudCoreUpgradeOrderParam implements Serializable {
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
      description = "升级的服务器ID"
   )
   @NotBlank(
      message = "升级的服务器ID不能为空"
   )
   private String serverId;

   public Long getMemberId() {
      return this.memberId;
   }

   public Integer getDeductMode() {
      return this.deductMode;
   }

   public BigDecimal getDeductAmount() {
      return this.deductAmount;
   }

   public Integer getCpu() {
      return this.cpu;
   }

   public Integer getRam() {
      return this.ram;
   }

   public String getServerId() {
      return this.serverId;
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

   public void setCpu(final Integer cpu) {
      this.cpu = cpu;
   }

   public void setRam(final Integer ram) {
      this.ram = ram;
   }

   public void setServerId(final String serverId) {
      this.serverId = serverId;
   }
}
