package com.rxinyun.member.production.modular.ecs.region.param;

import io.swagger.v3.oas.annotations.media.Schema;

public class MemberRegionEditParam {
   @Schema(
      description = "地域标识"
   )
   private String id;
   @Schema(
      description = "地域名称"
   )
   private String name;
   @Schema(
      description = "备注"
   )
   private String remark;
   @Schema(
      description = "状态"
   )
   private Integer status;

   public String getId() {
      return this.id;
   }

   public String getName() {
      return this.name;
   }

   public String getRemark() {
      return this.remark;
   }

   public Integer getStatus() {
      return this.status;
   }

   public void setId(final String id) {
      this.id = id;
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
