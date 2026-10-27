package com.rxinyun.admin.dev.modular.monitor.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.system.JvmInfo;
import cn.hutool.system.OsInfo;
import cn.hutool.system.RuntimeInfo;
import cn.hutool.system.SystemUtil;
import com.rxinyun.admin.dev.modular.monitor.result.DevMonitorServerResult;
import com.rxinyun.admin.dev.modular.monitor.service.DevMonitorService;
import com.rxinyun.common.util.CommonNetWorkInfoUtil;
import com.rxinyun.common.util.TimeUtil;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Service;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.CentralProcessor.TickType;
import oshi.software.os.FileSystem;
import oshi.software.os.OperatingSystem;
import oshi.util.Util;

@Service
public class DevMonitorServiceImpl implements DevMonitorService {
   @Override
   public DevMonitorServerResult serverInfo() {
      DevMonitorServerResult devMonitorServerResult = new DevMonitorServerResult();
      SystemInfo si = new SystemInfo();
      HardwareAbstractionLayer hal = si.getHardware();
      CentralProcessor cpu = hal.getProcessor();
      DevMonitorServerResult.DevMonitorCpuInfo devMonitorCpuInfo = new DevMonitorServerResult.DevMonitorCpuInfo();
      devMonitorCpuInfo.setCpuName(StrUtil.trim(cpu.getProcessorIdentifier().getName()));
      devMonitorCpuInfo.setCpuNum(cpu.getPhysicalPackageCount() + "颗物理CPU");
      devMonitorCpuInfo.setCpuPhysicalCoreNum(cpu.getPhysicalProcessorCount() + "个物理核心");
      devMonitorCpuInfo.setCpuLogicalCoreNum(cpu.getLogicalProcessorCount() + "个逻辑核心");
      long[] prevTicks = cpu.getSystemCpuLoadTicks();
      Util.sleep(1000L);
      long[] ticks = cpu.getSystemCpuLoadTicks();
      long nice = ticks[TickType.NICE.getIndex()] - prevTicks[TickType.NICE.getIndex()];
      long irq = ticks[TickType.IRQ.getIndex()] - prevTicks[TickType.IRQ.getIndex()];
      long softIrq = ticks[TickType.SOFTIRQ.getIndex()] - prevTicks[TickType.SOFTIRQ.getIndex()];
      long steal = ticks[TickType.STEAL.getIndex()] - prevTicks[TickType.STEAL.getIndex()];
      long sys = ticks[TickType.SYSTEM.getIndex()] - prevTicks[TickType.SYSTEM.getIndex()];
      long user = ticks[TickType.USER.getIndex()] - prevTicks[TickType.USER.getIndex()];
      long ioWait = ticks[TickType.IOWAIT.getIndex()] - prevTicks[TickType.IOWAIT.getIndex()];
      long idle = ticks[TickType.IDLE.getIndex()] - prevTicks[TickType.IDLE.getIndex()];
      long totalCpu = user + nice + sys + idle + ioWait + irq + softIrq + steal;
      devMonitorCpuInfo.setCpuSysUseRate(NumberUtil.div(NumberUtil.mul((float)sys, 100.0F), (float)totalCpu, 2) + "%");
      devMonitorCpuInfo.setCpuUserUseRate(NumberUtil.div(NumberUtil.mul((float)user, 100.0F), (float)totalCpu, 2) + "%");
      devMonitorCpuInfo.setCpuTotalUseRate(NumberUtil.div(NumberUtil.mul(NumberUtil.add((float)sys, (float)user), 100.0F), (float)totalCpu, 2));
      devMonitorCpuInfo.setCpuWaitRate(NumberUtil.div(NumberUtil.mul((float)ioWait, 100.0F), (float)totalCpu, 2) + "%");
      devMonitorCpuInfo.setCpuFreeRate(NumberUtil.div(NumberUtil.mul((float)idle, 100.0F), (float)totalCpu, 2) + "%");
      devMonitorServerResult.setDevMonitorCpuInfo(devMonitorCpuInfo);
      GlobalMemory memory = hal.getMemory();
      DevMonitorServerResult.DevMonitorMemoryInfo devMonitorMemoryInfo = new DevMonitorServerResult.DevMonitorMemoryInfo();
      long used = memory.getTotal() - memory.getAvailable();
      devMonitorMemoryInfo.setMemoryTotal(FileUtil.readableFileSize(memory.getTotal()));
      devMonitorMemoryInfo.setMemoryUsed(FileUtil.readableFileSize(used));
      devMonitorMemoryInfo.setMemoryFree(FileUtil.readableFileSize(memory.getAvailable()));
      devMonitorMemoryInfo.setMemoryUseRate(NumberUtil.mul(NumberUtil.div((float)used, (float)memory.getTotal(), 4), 100.0F));
      devMonitorServerResult.setDevMonitorMemoryInfo(devMonitorMemoryInfo);
      DevMonitorServerResult.DevMonitorStorageInfo devMonitorStorageInfo = new DevMonitorServerResult.DevMonitorStorageInfo();
      OperatingSystem operatingSystem = si.getOperatingSystem();
      FileSystem fileSystem = operatingSystem.getFileSystem();
      AtomicLong storageTotal = new AtomicLong();
      AtomicLong storageUsed = new AtomicLong();
      AtomicLong storageFree = new AtomicLong();
      fileSystem.getFileStores().forEach(osFileStore -> {
         long totalSpace = osFileStore.getTotalSpace();
         long usableSpace = osFileStore.getUsableSpace();
         long freeSpace = osFileStore.getFreeSpace();
         long usedSpace = totalSpace - usableSpace;
         storageTotal.addAndGet(totalSpace);
         storageUsed.addAndGet(usedSpace);
         storageFree.addAndGet(freeSpace);
      });
      devMonitorStorageInfo.setStorageTotal(FileUtil.readableFileSize(storageTotal.get()));
      devMonitorStorageInfo.setStorageUsed(FileUtil.readableFileSize(storageUsed.get()));
      devMonitorStorageInfo.setStorageFree(FileUtil.readableFileSize(storageFree.get()));
      devMonitorStorageInfo.setStorageUseRate(NumberUtil.mul(NumberUtil.div(storageUsed.doubleValue(), storageTotal.doubleValue(), 4), 100.0F));
      devMonitorServerResult.setDevMonitorStorageInfo(devMonitorStorageInfo);
      OsInfo osInfo = SystemUtil.getOsInfo();
      DevMonitorServerResult.DevMonitorServerInfo devMonitorServerInfo = new DevMonitorServerResult.DevMonitorServerInfo();
      devMonitorServerInfo.setServerName(NetUtil.getLocalHostName());
      devMonitorServerInfo.setServerOs(osInfo.getName());
      devMonitorServerInfo.setServerIp(NetUtil.getLocalhostStr());
      devMonitorServerInfo.setServerArchitecture(osInfo.getArch());
      devMonitorServerResult.setDevMonitorServerInfo(devMonitorServerInfo);
      DevMonitorServerResult.DevMonitorJvmInfo devMonitorJvmInfo = new DevMonitorServerResult.DevMonitorJvmInfo();
      RuntimeInfo runtimeInfo = SystemUtil.getRuntimeInfo();
      JvmInfo jvmInfo = SystemUtil.getJvmInfo();
      devMonitorJvmInfo.setJvmName(jvmInfo.getName());
      devMonitorJvmInfo.setJvmVersion(jvmInfo.getVersion());
      long totalMemory = runtimeInfo.getTotalMemory();
      devMonitorJvmInfo.setJvmMemoryTotal(FileUtil.readableFileSize(totalMemory));
      devMonitorJvmInfo.setJvmMemoryFree(FileUtil.readableFileSize(runtimeInfo.getFreeMemory()));
      long jvmMemoryUsed = NumberUtil.sub(new BigDecimal(runtimeInfo.getTotalMemory()), new BigDecimal(runtimeInfo.getFreeMemory())).longValue();
      devMonitorJvmInfo.setJvmMemoryUsed(FileUtil.readableFileSize(jvmMemoryUsed));
      double jvmUseRate = NumberUtil.mul(NumberUtil.div((float)jvmMemoryUsed, (float)totalMemory, 4), 100.0F);
      devMonitorJvmInfo.setJvmUseRate(jvmUseRate);
      RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
      LocalDateTime startTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(runtimeMXBean.getStartTime()), ZoneId.systemDefault());
      devMonitorJvmInfo.setJvmStartTime(TimeUtil.formatDateTime(TimeUtil.toDate(startTime)));
      devMonitorJvmInfo.setJvmRunTime(TimeUtil.formatBetween(TimeUtil.toDate(startTime), TimeUtil.toDate(TimeUtil.now())));
      devMonitorJvmInfo.setJavaVersion(SystemUtil.get("java.version", false));
      devMonitorJvmInfo.setJavaPath(SystemUtil.get("java.home", false));
      devMonitorServerResult.setDevMonitorJvmInfo(devMonitorJvmInfo);
      return devMonitorServerResult;
   }

   @Override
   public DevMonitorServerResult networkInfo() {
      DevMonitorServerResult devMonitorServerResult = new DevMonitorServerResult();
      DevMonitorServerResult.DevMonitorNetworkInfo devMonitorNetworkInfo = new DevMonitorServerResult.DevMonitorNetworkInfo();
      Map<String, String> networkUpRate = CommonNetWorkInfoUtil.getNetworkUpRate();
      devMonitorNetworkInfo.setUpLinkRate(networkUpRate.get("UP"));
      devMonitorNetworkInfo.setDownLinkRate(networkUpRate.get("DOWN"));
      devMonitorServerResult.setDevMonitorNetworkInfo(devMonitorNetworkInfo);
      return devMonitorServerResult;
   }
}
