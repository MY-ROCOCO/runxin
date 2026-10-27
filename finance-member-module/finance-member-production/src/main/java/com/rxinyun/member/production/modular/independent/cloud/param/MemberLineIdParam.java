package com.rxinyun.member.production.modular.independent.cloud.param;

import io.swagger.v3.oas.annotations.media.Schema;

public class MemberLineIdParam {
   @Schema(
      description = "线路ID"
   )
   private String id;
   @Schema(
      description = "地域ID"
   )
   private String regionId;

   public String getId() {
      return this.id;
   }

   public String getRegionId() {
      return this.regionId;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setRegionId(final String regionId) {
      this.regionId = regionId;
   }
}
