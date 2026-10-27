package com.rxinyun.dev.core.pojo;

import io.swagger.v3.oas.annotations.media.Schema;

public class BaseResourcePoolRequest {
   @Schema(
      description = "订单号"
   )
   private String orderNo;
   @Schema(
      description = "会员编号"
   )
   private Long memberNo;
   @Schema(
      description = "资源池ID"
   )
   private String resourcePoolId;
   @Schema(
      description = "请求参数（JSON格式）"
   )
   private String requestParams;
   @Schema(
      description = "操作类型：CREATE/RENEW/UPGRADE_DISK/UPGRADE_BANDWIDTH/UPGRADE_CPU_RAM/UPGRADE_DEFENSE/DELETE"
   )
   private String operationType;
   @Schema(
      description = "A系统服务器ID（续费/升级/删除时使用，对应A系统的 server.id）"
   )
   private String aServerId;
   @Schema(
      description = "续费月数（RENEW 时使用）"
   )
   private Integer months;
   @Schema(
      description = "产品类型：1云服务器 2独立服务器（与 ProductTypeEnum 一致）"
   )
   private Integer productType;

   public String getOrderNo() {
      return this.orderNo;
   }

   public Long getMemberNo() {
      return this.memberNo;
   }

   public String getResourcePoolId() {
      return this.resourcePoolId;
   }

   public String getRequestParams() {
      return this.requestParams;
   }

   public String getOperationType() {
      return this.operationType;
   }

   public String getAServerId() {
      return this.aServerId;
   }

   public Integer getMonths() {
      return this.months;
   }

   public Integer getProductType() {
      return this.productType;
   }

   public void setOrderNo(final String orderNo) {
      this.orderNo = orderNo;
   }

   public void setMemberNo(final Long memberNo) {
      this.memberNo = memberNo;
   }

   public void setResourcePoolId(final String resourcePoolId) {
      this.resourcePoolId = resourcePoolId;
   }

   public void setRequestParams(final String requestParams) {
      this.requestParams = requestParams;
   }

   public void setOperationType(final String operationType) {
      this.operationType = operationType;
   }

   public void setAServerId(final String aServerId) {
      this.aServerId = aServerId;
   }

   public void setMonths(final Integer months) {
      this.months = months;
   }

   public void setProductType(final Integer productType) {
      this.productType = productType;
   }
}
