package com.rxinyun.member.production.modular.independent.machine.param;

import io.swagger.v3.oas.annotations.media.Schema;

public class MemberMachineLineQueryParam {
   @Schema(
      description = "线路ID"
   )
   private String lineId;
   @Schema(
      description = "机型名称"
   )
   private String name;
   @Schema(
      description = "CPU规格信息"
   )
   private String cpuInfo;
   @Schema(
      description = "内存规格信息"
   )
   private String ramInfo;

   public String getLineId() {
      return this.lineId;
   }

   public String getName() {
      return this.name;
   }

   public String getCpuInfo() {
      return this.cpuInfo;
   }

   public String getRamInfo() {
      return this.ramInfo;
   }

   public void setLineId(final String lineId) {
      this.lineId = lineId;
   }

   public void setName(final String name) {
      this.name = name;
   }

   public void setCpuInfo(final String cpuInfo) {
      this.cpuInfo = cpuInfo;
   }

   public void setRamInfo(final String ramInfo) {
      this.ramInfo = ramInfo;
   }
}
