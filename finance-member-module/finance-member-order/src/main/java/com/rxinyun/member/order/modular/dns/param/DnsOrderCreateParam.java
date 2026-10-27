package com.rxinyun.member.order.modular.dns.param;

import io.swagger.v3.oas.annotations.media.Schema;

public class DnsOrderCreateParam extends DnsCalculatePriceParam {
   @Schema(
      description = "支付方式：1微信 2支付宝 3积分 4余额 5USDT，创建订单时可不传，支付时再选择"
   )
   private Integer payType;

   public Integer getPayType() {
      return this.payType;
   }

   public void setPayType(final Integer payType) {
      this.payType = payType;
   }
}
