package com.rxinyun.member.order.modular.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(
   description = "域名计价单项结果"
)
public class DomainCalculatePriceItemVO {
   @Schema(
      description = "域名全称"
   )
   private String domainName;
   @Schema(
      description = "后缀"
   )
   private String tld;
   @Schema(
      description = "注册年限"
   )
   private Integer years;
   @Schema(
      description = "单价（元/年）"
   )
   private BigDecimal unitPrice;
   @Schema(
      description = "小计"
   )
   private BigDecimal itemTotalPrice;

   public String getDomainName() {
      return this.domainName;
   }

   public String getTld() {
      return this.tld;
   }

   public Integer getYears() {
      return this.years;
   }

   public BigDecimal getUnitPrice() {
      return this.unitPrice;
   }

   public BigDecimal getItemTotalPrice() {
      return this.itemTotalPrice;
   }

   public void setDomainName(final String domainName) {
      this.domainName = domainName;
   }

   public void setTld(final String tld) {
      this.tld = tld;
   }

   public void setYears(final Integer years) {
      this.years = years;
   }

   public void setUnitPrice(final BigDecimal unitPrice) {
      this.unitPrice = unitPrice;
   }

   public void setItemTotalPrice(final BigDecimal itemTotalPrice) {
      this.itemTotalPrice = itemTotalPrice;
   }
}
