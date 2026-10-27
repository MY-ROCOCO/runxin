package com.rxinyun.admin.dev.modular.api.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(
   description = "资源池查询产品参数"
)
public class ResourcePoolQueryParam {
   @Schema(
      description = "资源池ID",
      required = true
   )
   @NotBlank(
      message = "资源池ID不能为空"
   )
   private String resourcePoolId;
   @Schema(
      description = "产品类型：cloud-server-region-云服务器地域，cloud-server-available-region-云服务器可用区，cloud-server-云服务器线路，independent-server-region-独立服务器地域，independent-server-line-独立服务器线路，independent-server-独立服务器机型，cloud-server-os-class-云服务器操作系统类别，cloud-server-os-version-云服务器操作系统版本，independent-server-os-class-独立服务器操作系统类别，independent-server-os-version-独立服务器操作系统版本",
      required = true
   )
   @NotBlank(
      message = "产品类型不能为空"
   )
   private String productType;
   @Schema(
      description = "线路ID（查询操作系统版本时需要，可选）"
   )
   private String lineId;

   public String getResourcePoolId() {
      return this.resourcePoolId;
   }

   public String getProductType() {
      return this.productType;
   }

   public String getLineId() {
      return this.lineId;
   }

   public void setResourcePoolId(final String resourcePoolId) {
      this.resourcePoolId = resourcePoolId;
   }

   public void setProductType(final String productType) {
      this.productType = productType;
   }

   public void setLineId(final String lineId) {
      this.lineId = lineId;
   }
}
