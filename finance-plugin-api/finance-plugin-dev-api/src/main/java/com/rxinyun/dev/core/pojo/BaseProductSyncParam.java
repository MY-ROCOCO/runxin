package com.rxinyun.dev.core.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.List;

@Schema(
   description = "产品同步参数"
)
public class BaseProductSyncParam implements Serializable {
   @Schema(
      description = "产品数据列表（JSON格式）"
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
