package com.rxinyun.member.order.modular.vhost.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class VhostCalculatePriceParam {
   @NotBlank(
      message = "版本不能为空"
   )
   @Schema(
      description = "虚拟主机版本ID",
      requiredMode = RequiredMode.REQUIRED
   )
   private String versionId;
   @NotBlank(
      message = "线路不能为空"
   )
   @Schema(
      description = "虚拟主机线路ID",
      requiredMode = RequiredMode.REQUIRED
   )
   private String lineId;
   @NotNull(
      message = "购买月数不能为空"
   )
   @Min(
      value = 1L,
      message = "购买月数必须大于0"
   )
   @Schema(
      description = "购买月数",
      requiredMode = RequiredMode.REQUIRED
   )
   private Integer months;
   @NotNull(
      message = "购买流量不能为空"
   )
   @Min(
      value = 0L,
      message = "购买流量不能小于0"
   )
   @Schema(
      description = "购买流量，单位G",
      requiredMode = RequiredMode.REQUIRED
   )
   private Integer trafficSize;
   @NotNull(
      message = "购买数量不能为空"
   )
   @Min(
      value = 1L,
      message = "购买数量必须大于0"
   )
   @Schema(
      description = "购买数量",
      requiredMode = RequiredMode.REQUIRED
   )
   private Integer number;
   @NotBlank(
      message = "PHP版本不能为空"
   )
   @Schema(
      description = "下单选择的PHP版本，需在套餐支持列表中；纯静态传 00",
      requiredMode = RequiredMode.REQUIRED
   )
   private String phpVersion;
   @Schema(
      description = "优惠券编码"
   )
   private String couponCode;

   public String getVersionId() {
      return this.versionId;
   }

   public String getLineId() {
      return this.lineId;
   }

   public Integer getMonths() {
      return this.months;
   }

   public Integer getTrafficSize() {
      return this.trafficSize;
   }

   public Integer getNumber() {
      return this.number;
   }

   public String getPhpVersion() {
      return this.phpVersion;
   }

   public String getCouponCode() {
      return this.couponCode;
   }

   public void setVersionId(final String versionId) {
      this.versionId = versionId;
   }

   public void setLineId(final String lineId) {
      this.lineId = lineId;
   }

   public void setMonths(final Integer months) {
      this.months = months;
   }

   public void setTrafficSize(final Integer trafficSize) {
      this.trafficSize = trafficSize;
   }

   public void setNumber(final Integer number) {
      this.number = number;
   }

   public void setPhpVersion(final String phpVersion) {
      this.phpVersion = phpVersion;
   }

   public void setCouponCode(final String couponCode) {
      this.couponCode = couponCode;
   }
}
