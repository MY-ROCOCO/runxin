package com.rxinyun.member.production.modular.ecs.line.param;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;

public class MemberLineSkuIdParam implements Serializable {
   @Schema(
      description = "区域标识"
   )
   private String id;
   @Schema(
      description = "线路ID"
   )
   private String lineId;
   @Schema(
      description = "CPU核心数"
   )
   private int cpu;
   @Schema(
      description = "内存大小(单位: GB)"
   )
   private int ram;

   public String getId() {
      return this.id;
   }

   public String getLineId() {
      return this.lineId;
   }

   public int getCpu() {
      return this.cpu;
   }

   public int getRam() {
      return this.ram;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setLineId(final String lineId) {
      this.lineId = lineId;
   }

   public void setCpu(final int cpu) {
      this.cpu = cpu;
   }

   public void setRam(final int ram) {
      this.ram = ram;
   }
}
