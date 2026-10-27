package com.rxinyun.admin.dev.modular.api.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

@Schema(
   description = "资源池同步选中产品参数"
)
public class ResourcePoolSyncSelectedParam {
   @Schema(
      description = "资源池ID",
      required = true
   )
   @NotBlank(
      message = "资源池ID不能为空"
   )
   private String resourcePoolId;
   @Schema(
      description = "产品类型：cloud-server-region-云服务器地域，cloud-server-available-region-云服务器可用区，independent-server-region-独立服务器地域，independent-server-line-独立服务器线路，cloud-server-line-云服务器线路，independent-server-machine-独立服务器机型，cloud-server-os-class-云服务器操作系统类别，cloud-server-os-version-云服务器操作系统版本，independent-server-os-class-独立服务器操作系统类别，independent-server-os-version-独立服务器操作系统版本",
      required = true
   )
   @NotBlank(
      message = "产品类型不能为空"
   )
   private String productType;
   @Schema(
      description = "选中的产品数据列表（JSON对象列表）",
      required = true
   )
   @NotEmpty(
      message = "选中的产品数据不能为空"
   )
   private List<Object> selectedProducts;

   public String getResourcePoolId() {
      return this.resourcePoolId;
   }

   public String getProductType() {
      return this.productType;
   }

   public List<Object> getSelectedProducts() {
      return this.selectedProducts;
   }

   public void setResourcePoolId(final String resourcePoolId) {
      this.resourcePoolId = resourcePoolId;
   }

   public void setProductType(final String productType) {
      this.productType = productType;
   }

   public void setSelectedProducts(final List<Object> selectedProducts) {
      this.selectedProducts = selectedProducts;
   }
}
