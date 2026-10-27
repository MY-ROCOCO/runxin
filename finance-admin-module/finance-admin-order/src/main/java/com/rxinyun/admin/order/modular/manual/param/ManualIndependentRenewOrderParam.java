package com.rxinyun.admin.order.modular.manual.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class ManualIndependentRenewOrderParam implements Serializable {
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
      description = "续费时长（月）"
   )
   @NotNull(
      message = "续费时长不能为空"
   )
   @Min(
      value = 1L,
      message = "续费时长必须大于0"
   )
   private Integer duration;
   @Schema(
      description = "续费的服务器ID（单台）"
   )
   private String serverId;
   @Schema(
      description = "批量续费的服务器ID列表"
   )
   private List<String> serverIds;

   public Long getMemberId() {
      return this.memberId;
   }

   public Integer getDeductMode() {
      return this.deductMode;
   }

   public BigDecimal getDeductAmount() {
      return this.deductAmount;
   }

   public Integer getDuration() {
      return this.duration;
   }

   public String getServerId() {
      return this.serverId;
   }

   public List<String> getServerIds() {
      return this.serverIds;
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

   public void setDuration(final Integer duration) {
      this.duration = duration;
   }

   public void setServerId(final String serverId) {
      this.serverId = serverId;
   }

   public void setServerIds(final List<String> serverIds) {
      this.serverIds = serverIds;
   }
}
