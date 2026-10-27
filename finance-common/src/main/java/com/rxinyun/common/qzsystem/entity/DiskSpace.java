package com.rxinyun.common.qzsystem.entity;

public class DiskSpace {
   private String name;
   private double size;
   private double freeSpace;

   public String getName() {
      return this.name;
   }

   public double getSize() {
      return this.size;
   }

   public double getFreeSpace() {
      return this.freeSpace;
   }

   public void setName(final String name) {
      this.name = name;
   }

   public void setSize(final double size) {
      this.size = size;
   }

   public void setFreeSpace(final double freeSpace) {
      this.freeSpace = freeSpace;
   }
}
