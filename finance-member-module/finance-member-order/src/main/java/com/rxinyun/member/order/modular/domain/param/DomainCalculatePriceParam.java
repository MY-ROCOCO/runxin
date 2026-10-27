package com.rxinyun.member.order.modular.domain.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

@Schema(
   description = "域名价格计算入参"
)
public class DomainCalculatePriceParam {
   @NotEmpty(
      message = "请传入要计价的域名列表"
   )
   @Schema(
      description = "域名列表（域名、后缀、年限）",
      requiredMode = RequiredMode.REQUIRED
   )
   private List<DomainCalculatePriceItemParam> items;
   @Schema(
      description = "优惠券编码"
   )
   private String couponCode;

   public List<DomainCalculatePriceItemParam> getItems() {
      return this.items;
   }

   public String getCouponCode() {
      return this.couponCode;
   }

   public void setItems(final List<DomainCalculatePriceItemParam> items) {
      this.items = items;
   }

   public void setCouponCode(final String couponCode) {
      this.couponCode = couponCode;
   }
}
