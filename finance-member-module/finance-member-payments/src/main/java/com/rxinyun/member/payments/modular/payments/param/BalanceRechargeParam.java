package com.rxinyun.member.payments.modular.payments.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

public class BalanceRechargeParam {
   @Schema(
      description = "充值金额"
   )
   @Min(
      value = 1L,
      message = "充值金额必须大于0"
   )
   private BigDecimal amount;
   @Schema(
      description = "支付方式(1: 微信, 2: 支付宝, 3: 积分, 4: 余额, 5: USDT)"
   )
   @NotBlank(
      message = "支付方式不能为空"
   )
   private Integer payType;
   @Schema(
      description = "支付平台(OA:公众号, PC:电脑端, H5:手机端, MP:小程序, APP:APP)"
   )
   @NotBlank(
      message = "支付平台不能为空"
   )
   private String platform;

   public BigDecimal getAmount() {
      return this.amount;
   }

   public Integer getPayType() {
      return this.payType;
   }

   public String getPlatform() {
      return this.platform;
   }

   public void setAmount(final BigDecimal amount) {
      this.amount = amount;
   }

   public void setPayType(final Integer payType) {
      this.payType = payType;
   }

   public void setPlatform(final String platform) {
      this.platform = platform;
   }
}
