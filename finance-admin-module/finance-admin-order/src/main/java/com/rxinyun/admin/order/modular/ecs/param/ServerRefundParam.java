package com.rxinyun.admin.order.modular.ecs.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public class ServerRefundParam {
   @NotBlank
   @Schema(
      description = "服务器 ID（云、独立或虚拟主机）"
   )
   private String serverId;
   @NotBlank
   @Schema(
      description = "FULL=全额退实付之和；PRORATED 或 BY_DAY=按剩余时长比例"
   )
   private String refundMode;
   @Schema(
      description = "1 云服务器 2 独立服务器 5 虚拟主机；不传则按云、独立、虚拟主机依次解析"
   )
   private Integer productType;
   @Schema(
      description = "备注"
   )
   private String reason;
   @Schema(
      description = "会员编号；资源池开放接口透传时可带入，用于按会员维度校验退款订单"
   )
   private Long memberNo;

   public String getServerId() {
      return this.serverId;
   }

   public String getRefundMode() {
      return this.refundMode;
   }

   public Integer getProductType() {
      return this.productType;
   }

   public String getReason() {
      return this.reason;
   }

   public Long getMemberNo() {
      return this.memberNo;
   }

   public void setServerId(final String serverId) {
      this.serverId = serverId;
   }

   public void setRefundMode(final String refundMode) {
      this.refundMode = refundMode;
   }

   public void setProductType(final Integer productType) {
      this.productType = productType;
   }

   public void setReason(final String reason) {
      this.reason = reason;
   }

   public void setMemberNo(final Long memberNo) {
      this.memberNo = memberNo;
   }
}
