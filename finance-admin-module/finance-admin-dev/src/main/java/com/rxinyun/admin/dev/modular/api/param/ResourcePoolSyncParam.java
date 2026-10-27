package com.rxinyun.admin.dev.modular.api.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(
   description = "资源池同步产品参数"
)
public class ResourcePoolSyncParam {
   @Schema(
      description = "资源池ID",
      required = true
   )
   @NotBlank(
      message = "资源池ID不能为空"
   )
   private String resourcePoolId;
   @Schema(
      description = "产品类型：cloud-server-云服务器线路，independent-server-独立服务器机型",
      required = true
   )
   @NotBlank(
      message = "产品类型不能为空"
   )
   private String productType;

   public String getResourcePoolId() {
      return this.resourcePoolId;
   }

   public String getProductType() {
      return this.productType;
   }

   public void setResourcePoolId(final String resourcePoolId) {
      this.resourcePoolId = resourcePoolId;
   }

   public void setProductType(final String productType) {
      this.productType = productType;
   }
}
