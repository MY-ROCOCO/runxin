package com.rxinyun.member.production.modular.ecs.region.param;

import io.swagger.v3.oas.annotations.media.Schema;

public class MemberRegionAddParam {
   @Schema(
      description = "地域名称"
   )
   private String name;
   @Schema(
      description = "备注"
   )
   private String remark;
   @Schema(
      description = "状态：1 显示, 0 隐藏"
   )
   private Integer status;

   public String getName() {
      return this.name;
   }

   public String getRemark() {
      return this.remark;
   }

   public Integer getStatus() {
      return this.status;
   }

   public void setName(final String name) {
      this.name = name;
   }

   public void setRemark(final String remark) {
      this.remark = remark;
   }

   public void setStatus(final Integer status) {
      this.status = status;
   }
}
