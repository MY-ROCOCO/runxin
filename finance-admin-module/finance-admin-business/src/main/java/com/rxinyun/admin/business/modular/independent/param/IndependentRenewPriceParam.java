package com.rxinyun.admin.business.modular.independent.param;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class IndependentRenewPriceParam implements Serializable {
   @Schema(
      description = "独立服务器标识列表"
   )
   private List<String> serverIds;
   @Schema(
      description = "续费价格"
   )
   private BigDecimal renewPrice;

   public List<String> getServerIds() {
      return this.serverIds;
   }

   public BigDecimal getRenewPrice() {
      return this.renewPrice;
   }

   public void setServerIds(final List<String> serverIds) {
      this.serverIds = serverIds;
   }

   public void setRenewPrice(final BigDecimal renewPrice) {
      this.renewPrice = renewPrice;
   }
}
