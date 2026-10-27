package com.rxinyun.admin.dev.modular.log.result;

import io.swagger.v3.oas.annotations.media.Schema;

public class DevLogOpPieChartDataResult {
   @Schema(
      description = "类型"
   )
   private String type;
   @Schema(
      description = "数量"
   )
   private Long value;

   public String getType() {
      return this.type;
   }

   public Long getValue() {
      return this.value;
   }

   public void setType(final String type) {
      this.type = type;
   }

   public void setValue(final Long value) {
      this.value = value;
   }
}
