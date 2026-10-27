package com.rxinyun.common.vhost.result;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

public class VhostSystemTotal {
   @Schema(
      description = "操作系统信息"
   )
   private String system;
   @Schema(
      description = "面板版本"
   )
   private String version;
   @Schema(
      description = "上次开机到现在经过的时间"
   )
   private String time;
   @Schema(
      description = "CPU核心数"
   )
   private Integer cpuNum;
   @Schema(
      description = "CPU使用率（百分比）"
   )
   private BigDecimal cpuRealUsed;
   @Schema(
      description = "物理内存容量（MB）"
   )
   private Long memTotal;
   @Schema(
      description = "已使用的内存（MB）"
   )
   private Long memRealUsed;
   @Schema(
      description = "可用的物理内存（MB）"
   )
   private Long memFree;
   @Schema(
      description = "缓存化的内存（MB）"
   )
   private Long memCached;
   @Schema(
      description = "系统缓冲（MB）"
   )
   private Long memBuffers;

   public String getSystem() {
      return this.system;
   }

   public String getVersion() {
      return this.version;
   }

   public String getTime() {
      return this.time;
   }

   public Integer getCpuNum() {
      return this.cpuNum;
   }

   public BigDecimal getCpuRealUsed() {
      return this.cpuRealUsed;
   }

   public Long getMemTotal() {
      return this.memTotal;
   }

   public Long getMemRealUsed() {
      return this.memRealUsed;
   }

   public Long getMemFree() {
      return this.memFree;
   }

   public Long getMemCached() {
      return this.memCached;
   }

   public Long getMemBuffers() {
      return this.memBuffers;
   }

   public void setSystem(final String system) {
      this.system = system;
   }

   public void setVersion(final String version) {
      this.version = version;
   }

   public void setTime(final String time) {
      this.time = time;
   }

   public void setCpuNum(final Integer cpuNum) {
      this.cpuNum = cpuNum;
   }

   public void setCpuRealUsed(final BigDecimal cpuRealUsed) {
      this.cpuRealUsed = cpuRealUsed;
   }

   public void setMemTotal(final Long memTotal) {
      this.memTotal = memTotal;
   }

   public void setMemRealUsed(final Long memRealUsed) {
      this.memRealUsed = memRealUsed;
   }

   public void setMemFree(final Long memFree) {
      this.memFree = memFree;
   }

   public void setMemCached(final Long memCached) {
      this.memCached = memCached;
   }

   public void setMemBuffers(final Long memBuffers) {
      this.memBuffers = memBuffers;
   }
}
