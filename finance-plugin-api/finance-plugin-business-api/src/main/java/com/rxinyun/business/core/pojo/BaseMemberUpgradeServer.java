package com.rxinyun.business.core.pojo;

import io.swagger.v3.oas.annotations.media.Schema;

public class BaseMemberUpgradeServer {
   @Schema(
      description = "云服务器ID"
   )
   private String serverId;
   @Schema(
      description = "核心数量"
   )
   private Integer cpu;
   @Schema(
      description = "实际内存"
   )
   private Integer ram;
   @Schema(
      description = "数据硬盘"
   )
   private Integer hardDisks;
   @Schema(
      description = "带宽"
   )
   private Integer bandwidth;

   public String getServerId() {
      return this.serverId;
   }

   public Integer getCpu() {
      return this.cpu;
   }

   public Integer getRam() {
      return this.ram;
   }

   public Integer getHardDisks() {
      return this.hardDisks;
   }

   public Integer getBandwidth() {
      return this.bandwidth;
   }

   public void setServerId(final String serverId) {
      this.serverId = serverId;
   }

   public void setCpu(final Integer cpu) {
      this.cpu = cpu;
   }

   public void setRam(final Integer ram) {
      this.ram = ram;
   }

   public void setHardDisks(final Integer hardDisks) {
      this.hardDisks = hardDisks;
   }

   public void setBandwidth(final Integer bandwidth) {
      this.bandwidth = bandwidth;
   }
}
