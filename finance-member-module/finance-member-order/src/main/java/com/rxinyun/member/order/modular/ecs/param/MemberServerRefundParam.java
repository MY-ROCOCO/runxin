package com.rxinyun.member.order.modular.ecs.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;

public class MemberServerRefundParam {
   @NotBlank(
      message = "服务器ID不能为空"
   )
   @Schema(
      description = "服务器ID（云服务器、独立服务器或虚拟主机）",
      requiredMode = RequiredMode.REQUIRED
   )
   private String serverId;
   @Schema(
      description = "产品类型：1 云服务器 2 独立服务器 5 虚拟主机；不传则按 serverId 自动解析"
   )
   private Integer productType;
   @Schema(
      description = "退款备注"
   )
   private String reason;

   public String getServerId() {
      return this.serverId;
   }

   public Integer getProductType() {
      return this.productType;
   }

   public String getReason() {
      return this.reason;
   }

   public void setServerId(final String serverId) {
      this.serverId = serverId;
   }

   public void setProductType(final Integer productType) {
      this.productType = productType;
   }

   public void setReason(final String reason) {
      this.reason = reason;
   }
}
