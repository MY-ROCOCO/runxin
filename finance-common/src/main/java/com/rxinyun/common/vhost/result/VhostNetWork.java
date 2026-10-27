package com.rxinyun.common.vhost.result;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.List;

public class VhostNetWork {
   @Schema(
      description = "总接收（字节数）"
   )
   private Long downTotal;
   @Schema(
      description = "总发送（字节数）"
   )
   private Long upTotal;
   @Schema(
      description = "总收包（个）"
   )
   private Long downPackets;
   @Schema(
      description = "总发包（个）"
   )
   private Long upPackets;
   @Schema(
      description = "下行流量（KB）"
   )
   private BigDecimal down;
   @Schema(
      description = "上行流量（KB）"
   )
   private BigDecimal up;
   @Schema(
      description = "CPU实时信息 [使用率, 核心数]"
   )
   private List<BigDecimal> cpu;
   @Schema(
      description = "内存实时信息"
   )
   private VhostNetWork.Mem mem;

   public Long getDownTotal() {
      return this.downTotal;
   }

   public Long getUpTotal() {
      return this.upTotal;
   }

   public Long getDownPackets() {
      return this.downPackets;
   }

   public Long getUpPackets() {
      return this.upPackets;
   }

   public BigDecimal getDown() {
      return this.down;
   }

   public BigDecimal getUp() {
      return this.up;
   }

   public List<BigDecimal> getCpu() {
      return this.cpu;
   }

   public VhostNetWork.Mem getMem() {
      return this.mem;
   }

   public void setDownTotal(final Long downTotal) {
      this.downTotal = downTotal;
   }

   public void setUpTotal(final Long upTotal) {
      this.upTotal = upTotal;
   }

   public void setDownPackets(final Long downPackets) {
      this.downPackets = downPackets;
   }

   public void setUpPackets(final Long upPackets) {
      this.upPackets = upPackets;
   }

   public void setDown(final BigDecimal down) {
      this.down = down;
   }

   public void setUp(final BigDecimal up) {
      this.up = up;
   }

   public void setCpu(final List<BigDecimal> cpu) {
      this.cpu = cpu;
   }

   public void setMem(final VhostNetWork.Mem mem) {
      this.mem = mem;
   }

   public static class Mem {
      @Schema(
         description = "可用的物理内存（MB）"
      )
      private Long memFree;
      @Schema(
         description = "物理内存容量（MB）"
      )
      private Long memTotal;
      @Schema(
         description = "缓存化的内存（MB）"
      )
      private Long memCached;
      @Schema(
         description = "系统缓冲（MB）"
      )
      private Long memBuffers;
      @Schema(
         description = "已使用的内存（MB）"
      )
      private Long memRealUsed;

      public Long getMemFree() {
         return this.memFree;
      }

      public Long getMemTotal() {
         return this.memTotal;
      }

      public Long getMemCached() {
         return this.memCached;
      }

      public Long getMemBuffers() {
         return this.memBuffers;
      }

      public Long getMemRealUsed() {
         return this.memRealUsed;
      }

      public void setMemFree(final Long memFree) {
         this.memFree = memFree;
      }

      public void setMemTotal(final Long memTotal) {
         this.memTotal = memTotal;
      }

      public void setMemCached(final Long memCached) {
         this.memCached = memCached;
      }

      public void setMemBuffers(final Long memBuffers) {
         this.memBuffers = memBuffers;
      }

      public void setMemRealUsed(final Long memRealUsed) {
         this.memRealUsed = memRealUsed;
      }
   }
}
