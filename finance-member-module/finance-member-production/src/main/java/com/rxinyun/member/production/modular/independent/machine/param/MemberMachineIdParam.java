package com.rxinyun.member.production.modular.independent.machine.param;

import com.rxinyun.member.production.modular.independent.machine.entity.MemberMachine;
import io.swagger.v3.oas.annotations.media.Schema;

public class MemberMachineIdParam extends MemberMachine {
   @Schema(
      description = "机型ID"
   )
   private String id;
   @Schema(
      description = "线路ID"
   )
   private String lineId;

   @Override
   public String getId() {
      return this.id;
   }

   public String getLineId() {
      return this.lineId;
   }

   @Override
   public void setId(final String id) {
      this.id = id;
   }

   public void setLineId(final String lineId) {
      this.lineId = lineId;
   }
}
