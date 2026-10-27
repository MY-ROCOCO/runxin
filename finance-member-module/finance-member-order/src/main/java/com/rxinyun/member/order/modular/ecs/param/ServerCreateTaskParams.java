package com.rxinyun.member.order.modular.ecs.param;

public class ServerCreateTaskParams {
   private Integer cpu;
   private Integer ram;
   private Integer bandwidth;
   private Integer harddisks;
   private Integer defense;
   private String loginPassword;
   private Integer months;
   private String lineId;
   private String systemVersionId;

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

   public String getLoginPassword() {
      return this.loginPassword;
   }

   public Integer getMonths() {
      return this.months;
   }

   public String getLineId() {
      return this.lineId;
   }

   public String getSystemVersionId() {
      return this.systemVersionId;
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

   public void setLoginPassword(final String loginPassword) {
      this.loginPassword = loginPassword;
   }

   public void setMonths(final Integer months) {
      this.months = months;
   }

   public void setLineId(final String lineId) {
      this.lineId = lineId;
   }

   public void setSystemVersionId(final String systemVersionId) {
      this.systemVersionId = systemVersionId;
   }
}
