package com.rxinyun.admin.production.modular.independent.machine.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;

@TableName(
   value = "INDEPENDENT_LINE_MACHINE",
   autoResultMap = true
)
public class MachineLine {
   @Schema(
      description = "关联表ID"
   )
   private String id;
   @Schema(
      description = "线路ID"
   )
   private String lineId;
   @Schema(
      description = "机型ID"
   )
   private String machineId;

   public void setId(final String id) {
      this.id = id;
   }

   public void setLineId(final String lineId) {
      this.lineId = lineId;
   }

   public void setMachineId(final String machineId) {
      this.machineId = machineId;
   }

   public String getId() {
      return this.id;
   }

   public String getLineId() {
      return this.lineId;
   }

   public String getMachineId() {
      return this.machineId;
   }
}
