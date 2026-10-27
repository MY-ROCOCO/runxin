package com.rxinyun.member.order.modular.domain.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

@Schema(
   description = "域名订单创建参数（可一次传入多个域名，单价由后端按域名价格计算接口规则计算）"
)
public class DomainOrderCreateParam {
   @NotBlank(
      message = "请选择审核通过的模板"
   )
   @Schema(
      description = "审核通过的域名模板ID",
      requiredMode = RequiredMode.REQUIRED
   )
   private String templateId;
   @NotEmpty(
      message = "请传入要注册的域名列表"
   )
   @Schema(
      description = "域名列表，可传多项以同时注册多个域名（每项仅传 domainName、tld、years，不传单价）",
      requiredMode = RequiredMode.REQUIRED
   )
   private List<DomainOrderItemParam> items;
   @Schema(
      description = "支付方式 1微信 2支付宝 4余额 5USDT"
   )
   private Integer payType;
   @Schema(
      description = "优惠券编码"
   )
   private String couponCode;

   public String getTemplateId() {
      return this.templateId;
   }

   public List<DomainOrderItemParam> getItems() {
      return this.items;
   }

   public Integer getPayType() {
      return this.payType;
   }

   public String getCouponCode() {
      return this.couponCode;
   }

   public void setTemplateId(final String templateId) {
      this.templateId = templateId;
   }

   public void setItems(final List<DomainOrderItemParam> items) {
      this.items = items;
   }

   public void setPayType(final Integer payType) {
      this.payType = payType;
   }

   public void setCouponCode(final String couponCode) {
      this.couponCode = couponCode;
   }
}
