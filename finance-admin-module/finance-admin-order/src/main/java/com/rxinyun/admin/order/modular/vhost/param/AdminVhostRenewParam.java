package com.rxinyun.admin.order.modular.vhost.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class AdminVhostRenewParam {
   @NotNull(
      message = "会员ID不能为空"
   )
   @Schema(
      description = "会员ID",
      requiredMode = RequiredMode.REQUIRED
   )
   private Long memberId;
   @NotBlank(
      message = "虚拟主机不能为空"
   )
   @Schema(
      description = "虚拟主机实例ID",
      requiredMode = RequiredMode.REQUIRED
   )
   private String hostId;
   @NotNull(
      message = "续费月数不能为空"
   )
   @Min(
      value = 1L,
      message = "续费月数必须大于0"
   )
   @Schema(
      description = "续费月数",
      requiredMode = RequiredMode.REQUIRED
   )
   private Integer months;

   public Long getMemberId() {
      return this.memberId;
   }

   public String getHostId() {
      return this.hostId;
   }

   public Integer getMonths() {
      return this.months;
   }

   public void setMemberId(final Long memberId) {
      this.memberId = memberId;
   }

   public void setHostId(final String hostId) {
      this.hostId = hostId;
   }

   public void setMonths(final Integer months) {
      this.months = months;
   }
}
