package com.rxinyun.member.production.modular.domain.template.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(
   description = "国内省市级联"
)
public class ChinaRegionsVO {
   @Schema(
      description = "是否成功"
   )
   private Boolean success;
   @Schema(
      description = "提示信息"
   )
   private String msg;
   @Schema(
      description = "省份列表（每省含 children 城市列表）"
   )
   private List<RegionProvinceVO> regions;

   public Boolean getSuccess() {
      return this.success;
   }

   public String getMsg() {
      return this.msg;
   }

   public List<RegionProvinceVO> getRegions() {
      return this.regions;
   }

   public void setSuccess(final Boolean success) {
      this.success = success;
   }

   public void setMsg(final String msg) {
      this.msg = msg;
   }

   public void setRegions(final List<RegionProvinceVO> regions) {
      this.regions = regions;
   }

   public ChinaRegionsVO() {
   }

   public ChinaRegionsVO(final Boolean success, final String msg, final List<RegionProvinceVO> regions) {
      this.success = success;
      this.msg = msg;
      this.regions = regions;
   }
}
