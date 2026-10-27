package com.rxinyun.member.business.modular.independent.param;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;

public class MemberIndependentRenewAutoParam implements Serializable {
   @Schema(
      description = "独立服务器ID"
   )
   private String id;
   @Schema(
      description = "是否自动续费： 0 不自动续费 1 自动续费"
   )
   private Integer renewAuto;

   public String getId() {
      return this.id;
   }

   public Integer getRenewAuto() {
      return this.renewAuto;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setRenewAuto(final Integer renewAuto) {
      this.renewAuto = renewAuto;
   }
}
