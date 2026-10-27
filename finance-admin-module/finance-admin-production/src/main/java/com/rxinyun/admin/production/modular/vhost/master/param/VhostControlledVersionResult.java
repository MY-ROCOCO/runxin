package com.rxinyun.admin.production.modular.vhost.master.param;

import io.swagger.v3.oas.annotations.media.Schema;

public class VhostControlledVersionResult {
   @Schema(
      description = "受控版本编码"
   )
   private String value;
   @Schema(
      description = "受控版本名称"
   )
   private String label;

   public String getValue() {
      return this.value;
   }

   public String getLabel() {
      return this.label;
   }

   public void setValue(final String value) {
      this.value = value;
   }

   public void setLabel(final String label) {
      this.label = label;
   }
}
