package com.rxinyun.member.business.modular.independent.param;

import io.swagger.v3.oas.annotations.media.Schema;

public class MemberIndependentIpParam {
   @Schema(
      description = "服务器标识"
   )
   private String id;
   @Schema(
      description = "增加服务器的数量"
   )
   private String count;

   public String getId() {
      return this.id;
   }

   public String getCount() {
      return this.count;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setCount(final String count) {
      this.count = count;
   }
}
