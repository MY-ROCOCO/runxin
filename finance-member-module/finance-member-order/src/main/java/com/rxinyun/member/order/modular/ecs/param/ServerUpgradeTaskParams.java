package com.rxinyun.member.order.modular.ecs.param;

public class ServerUpgradeTaskParams {
   private String upgradeItem;
   private Integer cpu;
   private Integer ram;
   private Integer bandwidth;
   private Integer harddisks;
   private Integer defense;

   public String getUpgradeItem() {
      return this.upgradeItem;
   }

   public Integer getCpu() {
      return this.cpu;
   }

   public Integer getRam() {
      return this.ram;
   }

   public Integer getBandwidth() {
      return this.bandwidth;
   }

   public Integer getHarddisks() {
      return this.harddisks;
   }

   public Integer getDefense() {
      return this.defense;
   }

   public void setUpgradeItem(final String upgradeItem) {
      this.upgradeItem = upgradeItem;
   }

   public void setCpu(final Integer cpu) {
      this.cpu = cpu;
   }

   public void setRam(final Integer ram) {
      this.ram = ram;
   }

   public void setBandwidth(final Integer bandwidth) {
      this.bandwidth = bandwidth;
   }

   public void setHarddisks(final Integer harddisks) {
      this.harddisks = harddisks;
   }

   public void setDefense(final Integer defense) {
      this.defense = defense;
   }
}
