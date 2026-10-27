package com.rxinyun.common.qzsystem.result;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;

@Schema(
   description = "云服务器监控统一响应"
)
public class HostMonitorResult {
   @Schema(
      description = "存储使用%"
   )
   @JSONField(
      name = "StorageStats"
   )
   private Integer storageStats = 0;
   @Schema(
      description = "网络监控实时数据"
   )
   @JSONField(
      name = "NetworkStats"
   )
   private HostMonitorResult.NetworkStats networkStats = new HostMonitorResult.NetworkStats();
   @Schema(
      description = "CPU使用%"
   )
   @JSONField(
      name = "CpuStats"
   )
   private Double cpuStats = 0.0;
   @Schema(
      description = "内存使用%"
   )
   @JSONField(
      name = "MemoryStats"
   )
   private Integer memoryStats = 0;
   @Schema(
      description = "虚拟机分配内存，单位MB"
   )
   @JSONField(
      name = "MemoryAssigned"
   )
   private Integer memoryAssigned = 0;
   @Schema(
      description = "虚拟机原始状态"
   )
   @JSONField(
      name = "VmState"
   )
   private Integer vmState = 0;
   @Schema(
      description = "流量统计"
   )
   @JSONField(
      name = "Traffic"
   )
   private HostMonitorResult.Traffic traffic = new HostMonitorResult.Traffic();
   @Schema(
      description = "磁盘IO/磁盘占用数据"
   )
   @JSONField(
      name = "DiskStats"
   )
   private HostMonitorResult.DiskStats diskStats = new HostMonitorResult.DiskStats();
   @Schema(
      description = "RkHyper-v磁盘IO/磁盘占用时间序列"
   )
   @JSONField(
      name = "IOData"
   )
   private List<HostMonitorResult.DiskStats> ioData = new ArrayList<>();
   @Schema(
      description = "RkHyper-v网络时间序列"
   )
   @JSONField(
      name = "NetworkData"
   )
   private List<HostMonitorResult.NetworkData> networkData = new ArrayList<>();
   @Schema(
      description = "RkHyper-v虚拟机状态/CPU/内存时间序列"
   )
   @JSONField(
      name = "StateData"
   )
   private List<HostMonitorResult.StateData> stateData = new ArrayList<>();

   public Integer getStorageStats() {
      return this.storageStats;
   }

   public HostMonitorResult.NetworkStats getNetworkStats() {
      return this.networkStats;
   }

   public Double getCpuStats() {
      return this.cpuStats;
   }

   public Integer getMemoryStats() {
      return this.memoryStats;
   }

   public Integer getMemoryAssigned() {
      return this.memoryAssigned;
   }

   public Integer getVmState() {
      return this.vmState;
   }

   public HostMonitorResult.Traffic getTraffic() {
      return this.traffic;
   }

   public HostMonitorResult.DiskStats getDiskStats() {
      return this.diskStats;
   }

   public List<HostMonitorResult.DiskStats> getIoData() {
      return this.ioData;
   }

   public List<HostMonitorResult.NetworkData> getNetworkData() {
      return this.networkData;
   }

   public List<HostMonitorResult.StateData> getStateData() {
      return this.stateData;
   }

   public void setStorageStats(final Integer storageStats) {
      this.storageStats = storageStats;
   }

   public void setNetworkStats(final HostMonitorResult.NetworkStats networkStats) {
      this.networkStats = networkStats;
   }

   public void setCpuStats(final Double cpuStats) {
      this.cpuStats = cpuStats;
   }

   public void setMemoryStats(final Integer memoryStats) {
      this.memoryStats = memoryStats;
   }

   public void setMemoryAssigned(final Integer memoryAssigned) {
      this.memoryAssigned = memoryAssigned;
   }

   public void setVmState(final Integer vmState) {
      this.vmState = vmState;
   }

   public void setTraffic(final HostMonitorResult.Traffic traffic) {
      this.traffic = traffic;
   }

   public void setDiskStats(final HostMonitorResult.DiskStats diskStats) {
      this.diskStats = diskStats;
   }

   public void setIoData(final List<HostMonitorResult.DiskStats> ioData) {
      this.ioData = ioData;
   }

   public void setNetworkData(final List<HostMonitorResult.NetworkData> networkData) {
      this.networkData = networkData;
   }

   public void setStateData(final List<HostMonitorResult.StateData> stateData) {
      this.stateData = stateData;
   }

   public static class DiskStats {
      @Schema(
         description = "磁盘已使用字节数"
      )
      @JSONField(
         name = "DiskUsedBytes"
      )
      private Long diskUsedBytes = 0L;
      @Schema(
         description = "磁盘已使用GB"
      )
      @JSONField(
         name = "DiskUsedGB"
      )
      private Double diskUsedGb = 0.0;
      @Schema(
         description = "瞬时磁盘读取流量（字节）"
      )
      @JSONField(
         name = "ReadBytes"
      )
      private Long readBytes = 0L;
      @Schema(
         description = "瞬时磁盘写入流量（字节）"
      )
      @JSONField(
         name = "WriteBytes"
      )
      private Long writeBytes = 0L;
      @Schema(
         description = "采集时间戳"
      )
      @JSONField(
         name = "GetTime"
      )
      private String getTime = "";

      public Long getDiskUsedBytes() {
         return this.diskUsedBytes;
      }

      public Double getDiskUsedGb() {
         return this.diskUsedGb;
      }

      public Long getReadBytes() {
         return this.readBytes;
      }

      public Long getWriteBytes() {
         return this.writeBytes;
      }

      public String getGetTime() {
         return this.getTime;
      }

      public void setDiskUsedBytes(final Long diskUsedBytes) {
         this.diskUsedBytes = diskUsedBytes;
      }

      public void setDiskUsedGb(final Double diskUsedGb) {
         this.diskUsedGb = diskUsedGb;
      }

      public void setReadBytes(final Long readBytes) {
         this.readBytes = readBytes;
      }

      public void setWriteBytes(final Long writeBytes) {
         this.writeBytes = writeBytes;
      }

      public void setGetTime(final String getTime) {
         this.getTime = getTime;
      }
   }

   public static class NetworkData {
      @Schema(
         description = "上行带宽/s，内外网发送字节合计"
      )
      @JSONField(
         name = "BytesSentPersec"
      )
      private Integer bytesSentPersec = 0;
      @Schema(
         description = "下行带宽/s，内外网接收字节合计"
      )
      @JSONField(
         name = "BytesReceivedPersec"
      )
      private Integer bytesReceivedPersec = 0;
      @Schema(
         description = "上行流量统计，单位MB"
      )
      @JSONField(
         name = "TrafficOut"
      )
      private Long trafficOut = 0L;
      @Schema(
         description = "下行流量统计，单位MB"
      )
      @JSONField(
         name = "TrafficIn"
      )
      private Long trafficIn = 0L;
      @Schema(
         description = "外网总流量"
      )
      @JSONField(
         name = "ExBytes"
      )
      private Long exBytes = 0L;
      @Schema(
         description = "外网接收字节"
      )
      @JSONField(
         name = "ExBytesReceived"
      )
      private Long exBytesReceived = 0L;
      @Schema(
         description = "外网发送字节"
      )
      @JSONField(
         name = "ExBytesSent"
      )
      private Long exBytesSent = 0L;
      @Schema(
         description = "内网总流量"
      )
      @JSONField(
         name = "InBytes"
      )
      private Long inBytes = 0L;
      @Schema(
         description = "内网接收字节"
      )
      @JSONField(
         name = "InBytesReceived"
      )
      private Long inBytesReceived = 0L;
      @Schema(
         description = "内网发送字节"
      )
      @JSONField(
         name = "InBytesSent"
      )
      private Long inBytesSent = 0L;
      @Schema(
         description = "采集时间戳"
      )
      @JSONField(
         name = "GetTime"
      )
      private String getTime = "";

      public Integer getBytesSentPersec() {
         return this.bytesSentPersec;
      }

      public Integer getBytesReceivedPersec() {
         return this.bytesReceivedPersec;
      }

      public Long getTrafficOut() {
         return this.trafficOut;
      }

      public Long getTrafficIn() {
         return this.trafficIn;
      }

      public Long getExBytes() {
         return this.exBytes;
      }

      public Long getExBytesReceived() {
         return this.exBytesReceived;
      }

      public Long getExBytesSent() {
         return this.exBytesSent;
      }

      public Long getInBytes() {
         return this.inBytes;
      }

      public Long getInBytesReceived() {
         return this.inBytesReceived;
      }

      public Long getInBytesSent() {
         return this.inBytesSent;
      }

      public String getGetTime() {
         return this.getTime;
      }

      public void setBytesSentPersec(final Integer bytesSentPersec) {
         this.bytesSentPersec = bytesSentPersec;
      }

      public void setBytesReceivedPersec(final Integer bytesReceivedPersec) {
         this.bytesReceivedPersec = bytesReceivedPersec;
      }

      public void setTrafficOut(final Long trafficOut) {
         this.trafficOut = trafficOut;
      }

      public void setTrafficIn(final Long trafficIn) {
         this.trafficIn = trafficIn;
      }

      public void setExBytes(final Long exBytes) {
         this.exBytes = exBytes;
      }

      public void setExBytesReceived(final Long exBytesReceived) {
         this.exBytesReceived = exBytesReceived;
      }

      public void setExBytesSent(final Long exBytesSent) {
         this.exBytesSent = exBytesSent;
      }

      public void setInBytes(final Long inBytes) {
         this.inBytes = inBytes;
      }

      public void setInBytesReceived(final Long inBytesReceived) {
         this.inBytesReceived = inBytesReceived;
      }

      public void setInBytesSent(final Long inBytesSent) {
         this.inBytesSent = inBytesSent;
      }

      public void setGetTime(final String getTime) {
         this.getTime = getTime;
      }
   }

   public static class NetworkStats {
      @Schema(
         description = "上行带宽/s"
      )
      @JSONField(
         name = "BytesSentPersec"
      )
      private Integer bytesSentPersec = 0;
      @Schema(
         description = "下行带宽/s"
      )
      @JSONField(
         name = "BytesReceivedPersec"
      )
      private Integer bytesReceivedPersec = 0;

      public Integer getBytesSentPersec() {
         return this.bytesSentPersec;
      }

      public Integer getBytesReceivedPersec() {
         return this.bytesReceivedPersec;
      }

      public void setBytesSentPersec(final Integer bytesSentPersec) {
         this.bytesSentPersec = bytesSentPersec;
      }

      public void setBytesReceivedPersec(final Integer bytesReceivedPersec) {
         this.bytesReceivedPersec = bytesReceivedPersec;
      }
   }

   public static class StateData {
      @Schema(
         description = "CPU使用%"
      )
      @JSONField(
         name = "CPUUSE"
      )
      private Double cpuStats = 0.0;
      @Schema(
         description = "虚拟机分配内存，单位MB"
      )
      @JSONField(
         name = "MemoryAssigned"
      )
      private Integer memoryAssigned = 0;
      @Schema(
         description = "虚拟机原始状态"
      )
      @JSONField(
         name = "State"
      )
      private Integer vmState = 0;
      @Schema(
         description = "采集时间戳"
      )
      @JSONField(
         name = "GetTime"
      )
      private String getTime = "";

      public Double getCpuStats() {
         return this.cpuStats;
      }

      public Integer getMemoryAssigned() {
         return this.memoryAssigned;
      }

      public Integer getVmState() {
         return this.vmState;
      }

      public String getGetTime() {
         return this.getTime;
      }

      public void setCpuStats(final Double cpuStats) {
         this.cpuStats = cpuStats;
      }

      public void setMemoryAssigned(final Integer memoryAssigned) {
         this.memoryAssigned = memoryAssigned;
      }

      public void setVmState(final Integer vmState) {
         this.vmState = vmState;
      }

      public void setGetTime(final String getTime) {
         this.getTime = getTime;
      }
   }

   public static class Traffic {
      @Schema(
         description = "上行流量统计，单位MB"
      )
      @JSONField(
         name = "TrafficOut"
      )
      private Long trafficOut = 0L;
      @Schema(
         description = "下行流量统计，单位MB"
      )
      @JSONField(
         name = "TrafficIn"
      )
      private Long trafficIn = 0L;

      public Long getTrafficOut() {
         return this.trafficOut;
      }

      public Long getTrafficIn() {
         return this.trafficIn;
      }

      public void setTrafficOut(final Long trafficOut) {
         this.trafficOut = trafficOut;
      }

      public void setTrafficIn(final Long trafficIn) {
         this.trafficIn = trafficIn;
      }
   }
}
