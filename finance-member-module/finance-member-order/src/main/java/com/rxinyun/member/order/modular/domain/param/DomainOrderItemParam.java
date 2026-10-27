package com.rxinyun.member.order.modular.domain.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(
   description = "域名订单项（可传多项以同时注册多个域名）"
)
public class DomainOrderItemParam {
   @NotBlank(
      message = "域名不能为空"
   )
   @Schema(
      description = "域名全称",
      example = "xinnet.com",
      requiredMode = RequiredMode.REQUIRED
   )
   private String domainName;
   @NotBlank(
      message = "后缀不能为空"
   )
   @Schema(
      description = "后缀，用于后端按配置计价",
      example = "com",
      requiredMode = RequiredMode.REQUIRED
   )
   private String tld;
   @NotNull(
      message = "年限不能为空"
   )
   @Min(
      value = 1L,
      message = "年限至少1年"
   )
   @Schema(
      description = "注册年限",
      requiredMode = RequiredMode.REQUIRED
   )
   private Integer years = 1;

   public String getDomainName() {
      return this.domainName;
   }

   public String getTld() {
      return this.tld;
   }

   public Integer getYears() {
      return this.years;
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
}
