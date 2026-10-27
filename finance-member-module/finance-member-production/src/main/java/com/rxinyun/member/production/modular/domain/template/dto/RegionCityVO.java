package com.rxinyun.member.production.modular.domain.template.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
   description = "城市项"
)
public class RegionCityVO {
   @Schema(
      description = "提交给新网时使用的值，即 publicCityUk"
   )
   private String value;
   @Schema(
      description = "展示名称"
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

   public RegionCityVO() {
   }

   public RegionCityVO(final String value, final String label) {
      this.value = value;
      this.label = label;
   }
}
