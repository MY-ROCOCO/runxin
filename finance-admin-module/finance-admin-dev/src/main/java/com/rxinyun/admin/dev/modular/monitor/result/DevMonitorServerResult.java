package com.rxinyun.admin.dev.modular.monitor.result;

import io.swagger.v3.oas.annotations.media.Schema;

public class DevMonitorServerResult {
   @Schema(
      description = "CPU信息"
   )
   private DevMonitorServerResult.DevMonitorCpuInfo devMonitorCpuInfo;
   @Schema(
      description = "内存信息"
   )
   private DevMonitorServerResult.DevMonitorMemoryInfo devMonitorMemoryInfo;
   @Schema(
      description = "存储信息"
   )
   private DevMonitorServerResult.DevMonitorStorageInfo devMonitorStorageInfo;
   @Schema(
      description = "网络信息"
   )
   private DevMonitorServerResult.DevMonitorNetworkInfo devMonitorNetworkInfo;
   @Schema(
      description = "服务器信息"
   )
   private DevMonitorServerResult.DevMonitorServerInfo devMonitorServerInfo;
   @Schema(
      description = "JVM信息"
   )
   private DevMonitorServerResult.DevMonitorJvmInfo devMonitorJvmInfo;

   public DevMonitorServerResult.DevMonitorCpuInfo getDevMonitorCpuInfo() {
      return this.devMonitorCpuInfo;
   }

   public DevMonitorServerResult.DevMonitorMemoryInfo getDevMonitorMemoryInfo() {
      return this.devMonitorMemoryInfo;
   }

   public DevMonitorServerResult.DevMonitorStorageInfo getDevMonitorStorageInfo() {
      return this.devMonitorStorageInfo;
   }

   public DevMonitorServerResult.DevMonitorNetworkInfo getDevMonitorNetworkInfo() {
      return this.devMonitorNetworkInfo;
   }

   public DevMonitorServerResult.DevMonitorServerInfo getDevMonitorServerInfo() {
      return this.devMonitorServerInfo;
   }

   public DevMonitorServerResult.DevMonitorJvmInfo getDevMonitorJvmInfo() {
      return this.devMonitorJvmInfo;
   }

   public void setDevMonitorCpuInfo(final DevMonitorServerResult.DevMonitorCpuInfo devMonitorCpuInfo) {
      this.devMonitorCpuInfo = devMonitorCpuInfo;
   }

   public void setDevMonitorMemoryInfo(final DevMonitorServerResult.DevMonitorMemoryInfo devMonitorMemoryInfo) {
      this.devMonitorMemoryInfo = devMonitorMemoryInfo;
   }

   public void setDevMonitorStorageInfo(final DevMonitorServerResult.DevMonitorStorageInfo devMonitorStorageInfo) {
      this.devMonitorStorageInfo = devMonitorStorageInfo;
   }

   public void setDevMonitorNetworkInfo(final DevMonitorServerResult.DevMonitorNetworkInfo devMonitorNetworkInfo) {
      this.devMonitorNetworkInfo = devMonitorNetworkInfo;
   }

   public void setDevMonitorServerInfo(final DevMonitorServerResult.DevMonitorServerInfo devMonitorServerInfo) {
      this.devMonitorServerInfo = devMonitorServerInfo;
   }

   public void setDevMonitorJvmInfo(final DevMonitorServerResult.DevMonitorJvmInfo devMonitorJvmInfo) {
      this.devMonitorJvmInfo = devMonitorJvmInfo;
   }

   public static class DevMonitorCpuInfo {
      @Schema(
         description = "CPU名称"
      )
      private String cpuName;
      @Schema(
         description = "CPU数量"
      )
      private String cpuNum;
      @Schema(
         description = "CPU物理核心数"
      )
      private String cpuPhysicalCoreNum;
      @Schema(
         description = "CPU逻辑核心数"
      )
      private String cpuLogicalCoreNum;
      @Schema(
         description = "CPU系统使用率"
      )
      private String cpuSysUseRate;
      @Schema(
         description = "CPU用户使用率"
      )
      private String cpuUserUseRate;
      @Schema(
         description = "CPU当前总使用率"
      )
      private Double cpuTotalUseRate;
      @Schema(
         description = "CPU当前等待率"
      )
      private String cpuWaitRate;
      @Schema(
         description = "CPU当前空闲率"
      )
      private String cpuFreeRate;

      public String getCpuName() {
         return this.cpuName;
      }

      public String getCpuNum() {
         return this.cpuNum;
      }

      public String getCpuPhysicalCoreNum() {
         return this.cpuPhysicalCoreNum;
      }

      public String getCpuLogicalCoreNum() {
         return this.cpuLogicalCoreNum;
      }

      public String getCpuSysUseRate() {
         return this.cpuSysUseRate;
      }

      public String getCpuUserUseRate() {
         return this.cpuUserUseRate;
      }

      public Double getCpuTotalUseRate() {
         return this.cpuTotalUseRate;
      }

      public String getCpuWaitRate() {
         return this.cpuWaitRate;
      }

      public String getCpuFreeRate() {
         return this.cpuFreeRate;
      }

      public void setCpuName(final String cpuName) {
         this.cpuName = cpuName;
      }

      public void setCpuNum(final String cpuNum) {
         this.cpuNum = cpuNum;
      }

      public void setCpuPhysicalCoreNum(final String cpuPhysicalCoreNum) {
         this.cpuPhysicalCoreNum = cpuPhysicalCoreNum;
      }

      public void setCpuLogicalCoreNum(final String cpuLogicalCoreNum) {
         this.cpuLogicalCoreNum = cpuLogicalCoreNum;
      }

      public void setCpuSysUseRate(final String cpuSysUseRate) {
         this.cpuSysUseRate = cpuSysUseRate;
      }

      public void setCpuUserUseRate(final String cpuUserUseRate) {
         this.cpuUserUseRate = cpuUserUseRate;
      }

      public void setCpuTotalUseRate(final Double cpuTotalUseRate) {
         this.cpuTotalUseRate = cpuTotalUseRate;
      }

      public void setCpuWaitRate(final String cpuWaitRate) {
         this.cpuWaitRate = cpuWaitRate;
      }

      public void setCpuFreeRate(final String cpuFreeRate) {
         this.cpuFreeRate = cpuFreeRate;
      }
   }

   public static class DevMonitorJvmInfo {
      @Schema(
         description = "JVM名称"
      )
      private String jvmName;
      @Schema(
         description = "JVM版本"
      )
      private String jvmVersion;
      @Schema(
         description = "JVM总分配内存"
      )
      private String jvmMemoryTotal;
      @Schema(
         description = "JVM已用内存"
      )
      private String jvmMemoryUsed;
      @Schema(
         description = "JVM剩余内存"
      )
      private String jvmMemoryFree;
      @Schema(
         description = "JVM内存使用率"
      )
      private Double jvmUseRate;
      @Schema(
         description = "JVM启动时间"
      )
      private String jvmStartTime;
      @Schema(
         description = "JVM运行时长"
      )
      private String jvmRunTime;
      @Schema(
         description = "Java版本"
      )
      private String javaVersion;
      @Schema(
         description = "Java安装路径"
      )
      private String javaPath;

      public String getJvmName() {
         return this.jvmName;
      }

      public String getJvmVersion() {
         return this.jvmVersion;
      }

      public String getJvmMemoryTotal() {
         return this.jvmMemoryTotal;
      }

      public String getJvmMemoryUsed() {
         return this.jvmMemoryUsed;
      }

      public String getJvmMemoryFree() {
         return this.jvmMemoryFree;
      }

      public Double getJvmUseRate() {
         return this.jvmUseRate;
      }

      public String getJvmStartTime() {
         return this.jvmStartTime;
      }

      public String getJvmRunTime() {
         return this.jvmRunTime;
      }

      public String getJavaVersion() {
         return this.javaVersion;
      }

      public String getJavaPath() {
         return this.javaPath;
      }

      public void setJvmName(final String jvmName) {
         this.jvmName = jvmName;
      }

      public void setJvmVersion(final String jvmVersion) {
         this.jvmVersion = jvmVersion;
      }

      public void setJvmMemoryTotal(final String jvmMemoryTotal) {
         this.jvmMemoryTotal = jvmMemoryTotal;
      }

      public void setJvmMemoryUsed(final String jvmMemoryUsed) {
         this.jvmMemoryUsed = jvmMemoryUsed;
      }

      public void setJvmMemoryFree(final String jvmMemoryFree) {
         this.jvmMemoryFree = jvmMemoryFree;
      }

      public void setJvmUseRate(final Double jvmUseRate) {
         this.jvmUseRate = jvmUseRate;
      }

      public void setJvmStartTime(final String jvmStartTime) {
         this.jvmStartTime = jvmStartTime;
      }

      public void setJvmRunTime(final String jvmRunTime) {
         this.jvmRunTime = jvmRunTime;
      }

      public void setJavaVersion(final String javaVersion) {
         this.javaVersion = javaVersion;
      }

      public void setJavaPath(final String javaPath) {
         this.javaPath = javaPath;
      }
   }

   public static class DevMonitorMemoryInfo {
      @Schema(
         description = "内存总量"
      )
      private String memoryTotal;
      @Schema(
         description = "内存已用"
      )
      private String memoryUsed;
      @Schema(
         description = "内存剩余"
      )
      private String memoryFree;
      @Schema(
         description = "内存使用率"
      )
      private Double memoryUseRate;

      public String getMemoryTotal() {
         return this.memoryTotal;
      }

      public String getMemoryUsed() {
         return this.memoryUsed;
      }

      public String getMemoryFree() {
         return this.memoryFree;
      }

      public Double getMemoryUseRate() {
         return this.memoryUseRate;
      }

      public void setMemoryTotal(final String memoryTotal) {
         this.memoryTotal = memoryTotal;
      }

      public void setMemoryUsed(final String memoryUsed) {
         this.memoryUsed = memoryUsed;
      }

      public void setMemoryFree(final String memoryFree) {
         this.memoryFree = memoryFree;
      }

      public void setMemoryUseRate(final Double memoryUseRate) {
         this.memoryUseRate = memoryUseRate;
      }
   }

   public static class DevMonitorNetworkInfo {
      @Schema(
         description = "上行速率"
      )
      private String upLinkRate;
      @Schema(
         description = "下行速率"
      )
      private String downLinkRate;

      public String getUpLinkRate() {
         return this.upLinkRate;
      }

      public String getDownLinkRate() {
         return this.downLinkRate;
      }

      public void setUpLinkRate(final String upLinkRate) {
         this.upLinkRate = upLinkRate;
      }

      public void setDownLinkRate(final String downLinkRate) {
         this.downLinkRate = downLinkRate;
      }
   }

   public static class DevMonitorServerInfo {
      @Schema(
         description = "服务器名称"
      )
      private String serverName;
      @Schema(
         description = "服务器操作系统"
      )
      private String serverOs;
      @Schema(
         description = "服务器IP"
      )
      private String serverIp;
      @Schema(
         description = "服务器架构"
      )
      private String serverArchitecture;

      public String getServerName() {
         return this.serverName;
      }

      public String getServerOs() {
         return this.serverOs;
      }

      public String getServerIp() {
         return this.serverIp;
      }

      public String getServerArchitecture() {
         return this.serverArchitecture;
      }

      public void setServerName(final String serverName) {
         this.serverName = serverName;
      }

      public void setServerOs(final String serverOs) {
         this.serverOs = serverOs;
      }

      public void setServerIp(final String serverIp) {
         this.serverIp = serverIp;
      }

      public void setServerArchitecture(final String serverArchitecture) {
         this.serverArchitecture = serverArchitecture;
      }
   }

   public static class DevMonitorStorageInfo {
      @Schema(
         description = "存储总量"
      )
      private String storageTotal;
      @Schema(
         description = "存储已用"
      )
      private String storageUsed;
      @Schema(
         description = "存储剩余"
      )
      private String storageFree;
      @Schema(
         description = "存储使用率"
      )
      private Double storageUseRate;

      public String getStorageTotal() {
         return this.storageTotal;
      }

      public String getStorageUsed() {
         return this.storageUsed;
      }

      public String getStorageFree() {
         return this.storageFree;
      }

      public Double getStorageUseRate() {
         return this.storageUseRate;
      }

      public void setStorageTotal(final String storageTotal) {
         this.storageTotal = storageTotal;
      }

      public void setStorageUsed(final String storageUsed) {
         this.storageUsed = storageUsed;
      }

      public void setStorageFree(final String storageFree) {
         this.storageFree = storageFree;
      }

      public void setStorageUseRate(final Double storageUseRate) {
         this.storageUseRate = storageUseRate;
      }
   }
}
