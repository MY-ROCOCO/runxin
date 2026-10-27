package com.rxinyun.admin.production.modular.api.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

@Schema(
   description = "产品同步参数"
)
public class ProductSyncParam {
   @Schema(
      description = "产品数据列表（JSON格式）"
   )
   @NotEmpty(
      message = "产品数据不能为空"
   )
   private List<Object> products;
   @Schema(
      description = "资源池ID（当从资源池同步时，需要设置此字段）"
   )
   private String resourcePoolId;

   public List<Object> getProducts() {
      return this.products;
   }

   public String getResourcePoolId() {
      return this.resourcePoolId;
   }

   public void setProducts(final List<Object> products) {
      this.products = products;
   }

   public void setResourcePoolId(final String resourcePoolId) {
      this.resourcePoolId = resourcePoolId;
   }
}
