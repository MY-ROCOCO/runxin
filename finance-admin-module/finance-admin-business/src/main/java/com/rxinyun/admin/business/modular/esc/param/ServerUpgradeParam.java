package com.rxinyun.admin.business.modular.esc.param;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public class ServerUpgradeParam {
   @Schema(
      description = "升级项目 (1: 升级核心配置, 2: 升级带宽, 3: 升级防御, 4: 升级磁盘, 5: 升级IP, 6: 升级流量)"
   )
   private Integer upgradeItem;
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
   private List<Integer> dataDisks;
   @Schema(
      description = "带宽"
   )
   private Integer bandwidth;

   public Integer getUpgradeItem() {
      return this.upgradeItem;
   }

   public String getServerId() {
      return this.serverId;
   }

   public Integer getCpu() {
      return this.cpu;
   }

   public Integer getRam() {
      return this.ram;
   }

   public List<Integer> getDataDisks() {
      return this.dataDisks;
   }

   public Integer getBandwidth() {
      return this.bandwidth;
   }

   public void setUpgradeItem(final Integer upgradeItem) {
      this.upgradeItem = upgradeItem;
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

   public void setDataDisks(final List<Integer> dataDisks) {
      this.dataDisks = dataDisks;
   }

   public void setBandwidth(final Integer bandwidth) {
      this.bandwidth = bandwidth;
   }
}
