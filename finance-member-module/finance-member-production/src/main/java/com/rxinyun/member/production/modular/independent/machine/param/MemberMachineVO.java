package com.rxinyun.member.production.modular.independent.machine.param;

import com.rxinyun.member.production.modular.independent.machine.entity.MemberMachine;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public class MemberMachineVO extends MemberMachine {
   @Schema(
      description = "机型 绑定的 线路名集合"
   )
   private List<String> lineName;
   @Schema(
      description = "机型 绑定的 地域名集合"
   )
   private List<String> regionName;

   public List<String> getLineName() {
      return this.lineName;
   }

   public List<String> getRegionName() {
      return this.regionName;
   }

   public void setLineName(final List<String> lineName) {
      this.lineName = lineName;
   }

   public void setRegionName(final List<String> regionName) {
      this.regionName = regionName;
   }
}
