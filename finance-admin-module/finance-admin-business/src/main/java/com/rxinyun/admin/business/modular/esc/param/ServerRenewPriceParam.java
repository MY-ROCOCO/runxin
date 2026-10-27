package com.rxinyun.admin.business.modular.esc.param;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.List;

public class ServerRenewPriceParam {
   @Schema(
      description = "服务器标识列表"
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
