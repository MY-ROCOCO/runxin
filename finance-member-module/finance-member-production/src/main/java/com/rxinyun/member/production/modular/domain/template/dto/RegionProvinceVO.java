package com.rxinyun.member.production.modular.domain.template.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(
   description = "省份项"
)
public class RegionProvinceVO {
   @Schema(
      description = "提交给新网时使用的值，即 publicProvinceUk"
   )
   private String value;
   @Schema(
      description = "展示名称"
   )
   private String label;
   @Schema(
      description = "下属城市列表"
   )
   private List<RegionCityVO> children;

   public String getValue() {
      return this.value;
   }

   public String getLabel() {
      return this.label;
   }

   public List<RegionCityVO> getChildren() {
      return this.children;
   }

   public void setValue(final String value) {
      this.value = value;
   }

   public void setLabel(final String label) {
      this.label = label;
   }

   public void setChildren(final List<RegionCityVO> children) {
      this.children = children;
   }

   public RegionProvinceVO() {
   }

   public RegionProvinceVO(final String value, final String label, final List<RegionCityVO> children) {
      this.value = value;
      this.label = label;
      this.children = children;
   }
}
