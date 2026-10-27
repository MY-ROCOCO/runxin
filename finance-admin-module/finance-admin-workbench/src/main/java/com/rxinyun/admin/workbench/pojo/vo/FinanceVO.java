package com.rxinyun.admin.workbench.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

public class FinanceVO {
   @Schema(
      description = "充值金额"
   )
   private BigDecimal rechargeMoney = new BigDecimal(0);
   @Schema(
      description = "消费金额"
   )
   private BigDecimal orderPayMoney = new BigDecimal(0);
   @Schema(
      description = "订单数"
   )
   private Long orderNum;
   @Schema(
      description = "退订金额"
   )
   private BigDecimal refundMoney = new BigDecimal(0);

   public FinanceVO() {
      this.orderNum = 0L;
   }

   public BigDecimal getRechargeMoney() {
      return this.rechargeMoney;
   }

   public BigDecimal getOrderPayMoney() {
      return this.orderPayMoney;
   }

   public Long getOrderNum() {
      return this.orderNum;
   }

   public BigDecimal getRefundMoney() {
      return this.refundMoney;
   }

   public void setRechargeMoney(final BigDecimal rechargeMoney) {
      this.rechargeMoney = rechargeMoney;
   }

   public void setOrderPayMoney(final BigDecimal orderPayMoney) {
      this.orderPayMoney = orderPayMoney;
   }

   public void setOrderNum(final Long orderNum) {
      this.orderNum = orderNum;
   }

   public void setRefundMoney(final BigDecimal refundMoney) {
      this.refundMoney = refundMoney;
   }
}
