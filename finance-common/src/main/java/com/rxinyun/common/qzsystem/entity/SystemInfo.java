package com.rxinyun.common.qzsystem.entity;

import java.util.List;

public class SystemInfo {
   private List<DiskSpace> space;
   private double cpuUsage;
   private double memory;
   private double freeMemory;
   private Integer status;

   public List<DiskSpace> getSpace() {
      return this.space;
   }

   public double getCpuUsage() {
      return this.cpuUsage;
   }

   public double getMemory() {
      return this.memory;
   }

   public double getFreeMemory() {
      return this.freeMemory;
   }

   public Integer getStatus() {
      return this.status;
   }

   public void setSpace(final List<DiskSpace> space) {
      this.space = space;
   }

   public void setCpuUsage(final double cpuUsage) {
      this.cpuUsage = cpuUsage;
   }

   public void setMemory(final double memory) {
      this.memory = memory;
   }

   public void setFreeMemory(final double freeMemory) {
      this.freeMemory = freeMemory;
   }

   public void setStatus(final Integer status) {
      this.status = status;
   }
}
