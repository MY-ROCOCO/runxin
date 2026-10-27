package com.rxinyun.common.qzsystem.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;
import java.util.List;

public class HostParam {
   @Schema(
      description = "主机名称"
   )
   private String hostName;
   @Schema(
      description = "宿主机IP"
   )
   private String masterIp;
   @Schema(
      description = "受控密钥"
   )
   private String apiKey;
   @Schema(
      description = "受控端口号"
   )
   private Integer apiPort;
   @Schema(
      description = "到期日期"
   )
   private Date endTime;
   @Schema(
      description = "系统盘磁盘文件存放目录"
   )
   private String osPath;
   @Schema(
      description = "数据盘磁盘文件存放目录"
   )
   private String disksPath;
   @Schema(
      description = "虚拟机配置保存目录"
   )
   private String vmConfigPath;
   @Schema(
      description = "镜像存放目录"
   )
   private String imagePath;
   @Schema(
      description = "镜像目录名称"
   )
   private String imageDirectoryName;
   @Schema(
      description = "公网交换机名称"
   )
   private String publicSwitchName;
   @Schema(
      description = "私网交换机名称"
   )
   private String privateSwitchName;
   @Schema(
      description = "NAT交换机名称"
   )
   private String natSwitchName;
   @Schema(
      description = "系统密码"
   )
   private String systemPassword;
   @Schema(
      description = "核心数量"
   )
   private Integer cpu;
   @Schema(
      description = "CPU使用率"
   )
   private Integer cpuLimit;
   @Schema(
      description = "CPU预留百分比"
   )
   private Integer cpuReservePercentage;
   @Schema(
      description = "CPU相对权重"
   )
   private Integer cpuRelativeWeight;
   @Schema(
      description = "实际内存"
   )
   private Integer ram;
   @Schema(
      description = "开启动态内存: 0 关闭, 1 开启"
   )
   private Integer memoryDynamic;
   @Schema(
      description = "开启动态内存时的最小内存"
   )
   private Integer ramStart;
   @Schema(
      description = "动态内存缓冲百分比"
   )
   private Integer memoryBufferPercentage;
   @Schema(
      description = "内存优先级"
   )
   private Integer memoryPriority;
   @Schema(
      description = "上行带宽"
   )
   private Integer bandwidth;
   @Schema(
      description = "下行带宽"
   )
   private Integer bandwidthIn;
   @Schema(
      description = "操作类型"
   )
   private String osClass;
   @Schema(
      description = "线路客户机DNS"
   )
   private List<String> autoDns;
   @Schema(
      description = "流量"
   )
   private Integer traffic;
   @Schema(
      description = "系统盘"
   )
   private Integer osDisk;
   @Schema(
      description = "系统盘最高IOPS设置"
   )
   private Integer osMaxIops;
   @Schema(
      description = "数据盘最高IOPS设置"
   )
   private Integer disksMaxIops;
   @Schema(
      description = "最小IOPS"
   )
   private Long minimumIops;
   @Schema(
      description = "最大IOPS"
   )
   private Long maximumIops;
   @Schema(
      description = "数据硬盘"
   )
   private Integer hardDisks;
   @Schema(
      description = "数据硬盘数组"
   )
   private List<Integer> dataDisks;
   @Schema(
      description = "IP数量"
   )
   private Integer ipNum;
   @Schema(
      description = "续费月份"
   )
   private Integer months;
   @Schema(
      description = "防御"
   )
   private Integer defense;
   @Schema(
      description = "公网IP"
   )
   private String publicIp;
   @Schema(
      description = "公网网关"
   )
   private String publicGateway;
   @Schema(
      description = "公网子网掩码"
   )
   private String publicNetmask;
   @Schema(
      description = "公网Mac地址"
   )
   private String publicMac;
   @Schema(
      description = "公网VLAN ID"
   )
   private Integer publicVlanId;
   @Schema(
      description = "NatIP"
   )
   private String natIp;
   @Schema(
      description = "Nat网关"
   )
   private String natGateway;
   @Schema(
      description = "Nat子网掩码"
   )
   private String natNetmask;
   @Schema(
      description = "NatMac地址"
   )
   private String natMac;
   @Schema(
      description = "NatVLAN ID"
   )
   private Integer natVlanId;
   @Schema(
      description = "私网IP"
   )
   private String privateIp;
   @Schema(
      description = "私网网关"
   )
   private String privateGateway;
   @Schema(
      description = "私网子网掩码"
   )
   private String privateNetmask;
   @Schema(
      description = "私网Mac地址"
   )
   private String privateMac;
   @Schema(
      description = "私网VLAN ID"
   )
   private Integer privateVlanId;
   @Schema(
      description = "服务器ID"
   )
   private String serverId;
   @Schema(
      description = "异步队列ID，用于受控回调关联AUTO_TASK"
   )
   private String queueId;
   @Schema(
      description = "备份目录"
   )
   private String backupPath;
   @Schema(
      description = "是否重建: 0 创建, 1重建"
   )
   private Integer rebuildFlag;
   @Schema(
      description = "RkHyper-v重建方式：0=挂原盘，1=全新重建；轻舟等其它受控不传"
   )
   private Integer fullRebuild;
   @Schema(
      description = "是否共享IP"
   )
   private Integer shareIp;
   @Schema(
      description = "是否断网: 1打开 2关闭"
   )
   private Integer disconnect;
   @Schema(
      description = "IP地址数组"
   )
   private List<String> ipList;
   @Schema(
      description = "轻舟 Hyper-V data.otherip：附属公网 IP 英文逗号拼接（不含主 IP）；与 attachip 列表对应时可只填其一由受控解析"
   )
   private String ipStr;
   @Schema(
      description = "IP地址"
   )
   private List<HostIp> attachIps;
   @Schema(
      description = "魔方云节点ID"
   )
   private String mfcNodeId;
   @Schema(
      description = "魔方云系统ID(操作系统版本中设置)，创建机器时传 os"
   )
   private String mfcSystemId;
   @Schema(
      description = "魔方云登录账号"
   )
   private String mfcLoginAccount;
   @Schema(
      description = "魔方云登录密码"
   )
   private String mfcLoginPassword;
   @Schema(
      description = "操作系统默认端口(魔方云创建机器用)"
   )
   private Integer port;
   @Schema(
      description = "是否格式化数据盘(魔方云重装使用)：0=不格式化，1=格式化"
   )
   private Integer formatDataDisk;
   @Schema(
      description = "数据盘1大小(G)，多块数据盘时使用"
   )
   private Integer data1DiskSize;
   @Schema(
      description = "数据盘2大小(G)，多块数据盘时使用"
   )
   private Integer data2DiskSize;
   @Schema(
      description = "数据盘3大小(G)，多块数据盘时使用"
   )
   private Integer data3DiskSize;
   @Schema(
      description = "魔方云实例ID(数据库MFC_CLOUD_ID)，修改密码等接口用"
   )
   private String mfcCloudId;
   @Schema(
      description = "监控查询日期类型：1秒、2分、3时、4日、5月、6年"
   )
   private Integer dateType;
   @Schema(
      description = "监控查询最小时间，格式：yyyyMMddHHmmss"
   )
   private String minDateTime;
   @Schema(
      description = "监控查询最大时间，格式：yyyyMMddHHmmss"
   )
   private String maxDateTime;
   @Schema(
      description = "监控查询排序方式：desc降序、asc升序"
   )
   private String sort;

   public String getHostName() {
      return this.hostName;
   }

   public String getMasterIp() {
      return this.masterIp;
   }

   public String getApiKey() {
      return this.apiKey;
   }

   public Integer getApiPort() {
      return this.apiPort;
   }

   public Date getEndTime() {
      return this.endTime;
   }

   public String getOsPath() {
      return this.osPath;
   }

   public String getDisksPath() {
      return this.disksPath;
   }

   public String getVmConfigPath() {
      return this.vmConfigPath;
   }

   public String getImagePath() {
      return this.imagePath;
   }

   public String getImageDirectoryName() {
      return this.imageDirectoryName;
   }

   public String getPublicSwitchName() {
      return this.publicSwitchName;
   }

   public String getPrivateSwitchName() {
      return this.privateSwitchName;
   }

   public String getNatSwitchName() {
      return this.natSwitchName;
   }

   public String getSystemPassword() {
      return this.systemPassword;
   }

   public Integer getCpu() {
      return this.cpu;
   }

   public Integer getCpuLimit() {
      return this.cpuLimit;
   }

   public Integer getCpuReservePercentage() {
      return this.cpuReservePercentage;
   }

   public Integer getCpuRelativeWeight() {
      return this.cpuRelativeWeight;
   }

   public Integer getRam() {
      return this.ram;
   }

   public Integer getMemoryDynamic() {
      return this.memoryDynamic;
   }

   public Integer getRamStart() {
      return this.ramStart;
   }

   public Integer getMemoryBufferPercentage() {
      return this.memoryBufferPercentage;
   }

   public Integer getMemoryPriority() {
      return this.memoryPriority;
   }

   public Integer getBandwidth() {
      return this.bandwidth;
   }

   public Integer getBandwidthIn() {
      return this.bandwidthIn;
   }

   public String getOsClass() {
      return this.osClass;
   }

   public List<String> getAutoDns() {
      return this.autoDns;
   }

   public Integer getTraffic() {
      return this.traffic;
   }

   public Integer getOsDisk() {
      return this.osDisk;
   }

   public Integer getOsMaxIops() {
      return this.osMaxIops;
   }

   public Integer getDisksMaxIops() {
      return this.disksMaxIops;
   }

   public Long getMinimumIops() {
      return this.minimumIops;
   }

   public Long getMaximumIops() {
      return this.maximumIops;
   }

   public Integer getHardDisks() {
      return this.hardDisks;
   }

   public List<Integer> getDataDisks() {
      return this.dataDisks;
   }

   public Integer getIpNum() {
      return this.ipNum;
   }

   public Integer getMonths() {
      return this.months;
   }

   public Integer getDefense() {
      return this.defense;
   }

   public String getPublicIp() {
      return this.publicIp;
   }

   public String getPublicGateway() {
      return this.publicGateway;
   }

   public String getPublicNetmask() {
      return this.publicNetmask;
   }

   public String getPublicMac() {
      return this.publicMac;
   }

   public Integer getPublicVlanId() {
      return this.publicVlanId;
   }

   public String getNatIp() {
      return this.natIp;
   }

   public String getNatGateway() {
      return this.natGateway;
   }

   public String getNatNetmask() {
      return this.natNetmask;
   }

   public String getNatMac() {
      return this.natMac;
   }

   public Integer getNatVlanId() {
      return this.natVlanId;
   }

   public String getPrivateIp() {
      return this.privateIp;
   }

   public String getPrivateGateway() {
      return this.privateGateway;
   }

   public String getPrivateNetmask() {
      return this.privateNetmask;
   }

   public String getPrivateMac() {
      return this.privateMac;
   }

   public Integer getPrivateVlanId() {
      return this.privateVlanId;
   }

   public String getServerId() {
      return this.serverId;
   }

   public String getQueueId() {
      return this.queueId;
   }

   public String getBackupPath() {
      return this.backupPath;
   }

   public Integer getRebuildFlag() {
      return this.rebuildFlag;
   }

   public Integer getFullRebuild() {
      return this.fullRebuild;
   }

   public Integer getShareIp() {
      return this.shareIp;
   }

   public Integer getDisconnect() {
      return this.disconnect;
   }

   public List<String> getIpList() {
      return this.ipList;
   }

   public String getIpStr() {
      return this.ipStr;
   }

   public List<HostIp> getAttachIps() {
      return this.attachIps;
   }

   public String getMfcNodeId() {
      return this.mfcNodeId;
   }

   public String getMfcSystemId() {
      return this.mfcSystemId;
   }

   public String getMfcLoginAccount() {
      return this.mfcLoginAccount;
   }

   public String getMfcLoginPassword() {
      return this.mfcLoginPassword;
   }

   public Integer getPort() {
      return this.port;
   }

   public Integer getFormatDataDisk() {
      return this.formatDataDisk;
   }

   public Integer getData1DiskSize() {
      return this.data1DiskSize;
   }

   public Integer getData2DiskSize() {
      return this.data2DiskSize;
   }

   public Integer getData3DiskSize() {
      return this.data3DiskSize;
   }

   public String getMfcCloudId() {
      return this.mfcCloudId;
   }

   public Integer getDateType() {
      return this.dateType;
   }

   public String getMinDateTime() {
      return this.minDateTime;
   }

   public String getMaxDateTime() {
      return this.maxDateTime;
   }

   public String getSort() {
      return this.sort;
   }

   public void setHostName(final String hostName) {
      this.hostName = hostName;
   }

   public void setMasterIp(final String masterIp) {
      this.masterIp = masterIp;
   }

   public void setApiKey(final String apiKey) {
      this.apiKey = apiKey;
   }

   public void setApiPort(final Integer apiPort) {
      this.apiPort = apiPort;
   }

   public void setEndTime(final Date endTime) {
      this.endTime = endTime;
   }

   public void setOsPath(final String osPath) {
      this.osPath = osPath;
   }

   public void setDisksPath(final String disksPath) {
      this.disksPath = disksPath;
   }

   public void setVmConfigPath(final String vmConfigPath) {
      this.vmConfigPath = vmConfigPath;
   }

   public void setImagePath(final String imagePath) {
      this.imagePath = imagePath;
   }

   public void setImageDirectoryName(final String imageDirectoryName) {
      this.imageDirectoryName = imageDirectoryName;
   }

   public void setPublicSwitchName(final String publicSwitchName) {
      this.publicSwitchName = publicSwitchName;
   }

   public void setPrivateSwitchName(final String privateSwitchName) {
      this.privateSwitchName = privateSwitchName;
   }

   public void setNatSwitchName(final String natSwitchName) {
      this.natSwitchName = natSwitchName;
   }

   public void setSystemPassword(final String systemPassword) {
      this.systemPassword = systemPassword;
   }

   public void setCpu(final Integer cpu) {
      this.cpu = cpu;
   }

   public void setCpuLimit(final Integer cpuLimit) {
      this.cpuLimit = cpuLimit;
   }

   public void setCpuReservePercentage(final Integer cpuReservePercentage) {
      this.cpuReservePercentage = cpuReservePercentage;
   }

   public void setCpuRelativeWeight(final Integer cpuRelativeWeight) {
      this.cpuRelativeWeight = cpuRelativeWeight;
   }

   public void setRam(final Integer ram) {
      this.ram = ram;
   }

   public void setMemoryDynamic(final Integer memoryDynamic) {
      this.memoryDynamic = memoryDynamic;
   }

   public void setRamStart(final Integer ramStart) {
      this.ramStart = ramStart;
   }

   public void setMemoryBufferPercentage(final Integer memoryBufferPercentage) {
      this.memoryBufferPercentage = memoryBufferPercentage;
   }

   public void setMemoryPriority(final Integer memoryPriority) {
      this.memoryPriority = memoryPriority;
   }

   public void setBandwidth(final Integer bandwidth) {
      this.bandwidth = bandwidth;
   }

   public void setBandwidthIn(final Integer bandwidthIn) {
      this.bandwidthIn = bandwidthIn;
   }

   public void setOsClass(final String osClass) {
      this.osClass = osClass;
   }

   public void setAutoDns(final List<String> autoDns) {
      this.autoDns = autoDns;
   }

   public void setTraffic(final Integer traffic) {
      this.traffic = traffic;
   }

   public void setOsDisk(final Integer osDisk) {
      this.osDisk = osDisk;
   }

   public void setOsMaxIops(final Integer osMaxIops) {
      this.osMaxIops = osMaxIops;
   }

   public void setDisksMaxIops(final Integer disksMaxIops) {
      this.disksMaxIops = disksMaxIops;
   }

   public void setMinimumIops(final Long minimumIops) {
      this.minimumIops = minimumIops;
   }

   public void setMaximumIops(final Long maximumIops) {
      this.maximumIops = maximumIops;
   }

   public void setHardDisks(final Integer hardDisks) {
      this.hardDisks = hardDisks;
   }

   public void setDataDisks(final List<Integer> dataDisks) {
      this.dataDisks = dataDisks;
   }

   public void setIpNum(final Integer ipNum) {
      this.ipNum = ipNum;
   }

   public void setMonths(final Integer months) {
      this.months = months;
   }

   public void setDefense(final Integer defense) {
      this.defense = defense;
   }

   public void setPublicIp(final String publicIp) {
      this.publicIp = publicIp;
   }

   public void setPublicGateway(final String publicGateway) {
      this.publicGateway = publicGateway;
   }

   public void setPublicNetmask(final String publicNetmask) {
      this.publicNetmask = publicNetmask;
   }

   public void setPublicMac(final String publicMac) {
      this.publicMac = publicMac;
   }

   public void setPublicVlanId(final Integer publicVlanId) {
      this.publicVlanId = publicVlanId;
   }

   public void setNatIp(final String natIp) {
      this.natIp = natIp;
   }

   public void setNatGateway(final String natGateway) {
      this.natGateway = natGateway;
   }

   public void setNatNetmask(final String natNetmask) {
      this.natNetmask = natNetmask;
   }

   public void setNatMac(final String natMac) {
      this.natMac = natMac;
   }

   public void setNatVlanId(final Integer natVlanId) {
      this.natVlanId = natVlanId;
   }

   public void setPrivateIp(final String privateIp) {
      this.privateIp = privateIp;
   }

   public void setPrivateGateway(final String privateGateway) {
      this.privateGateway = privateGateway;
   }

   public void setPrivateNetmask(final String privateNetmask) {
      this.privateNetmask = privateNetmask;
   }

   public void setPrivateMac(final String privateMac) {
      this.privateMac = privateMac;
   }

   public void setPrivateVlanId(final Integer privateVlanId) {
      this.privateVlanId = privateVlanId;
   }

   public void setServerId(final String serverId) {
      this.serverId = serverId;
   }

   public void setQueueId(final String queueId) {
      this.queueId = queueId;
   }

   public void setBackupPath(final String backupPath) {
      this.backupPath = backupPath;
   }

   public void setRebuildFlag(final Integer rebuildFlag) {
      this.rebuildFlag = rebuildFlag;
   }

   public void setFullRebuild(final Integer fullRebuild) {
      this.fullRebuild = fullRebuild;
   }

   public void setShareIp(final Integer shareIp) {
      this.shareIp = shareIp;
   }

   public void setDisconnect(final Integer disconnect) {
      this.disconnect = disconnect;
   }

   public void setIpList(final List<String> ipList) {
      this.ipList = ipList;
   }

   public void setIpStr(final String ipStr) {
      this.ipStr = ipStr;
   }

   public void setAttachIps(final List<HostIp> attachIps) {
      this.attachIps = attachIps;
   }

   public void setMfcNodeId(final String mfcNodeId) {
      this.mfcNodeId = mfcNodeId;
   }

   public void setMfcSystemId(final String mfcSystemId) {
      this.mfcSystemId = mfcSystemId;
   }

   public void setMfcLoginAccount(final String mfcLoginAccount) {
      this.mfcLoginAccount = mfcLoginAccount;
   }

   public void setMfcLoginPassword(final String mfcLoginPassword) {
      this.mfcLoginPassword = mfcLoginPassword;
   }

   public void setPort(final Integer port) {
      this.port = port;
   }

   public void setFormatDataDisk(final Integer formatDataDisk) {
      this.formatDataDisk = formatDataDisk;
   }

   public void setData1DiskSize(final Integer data1DiskSize) {
      this.data1DiskSize = data1DiskSize;
   }

   public void setData2DiskSize(final Integer data2DiskSize) {
      this.data2DiskSize = data2DiskSize;
   }

   public void setData3DiskSize(final Integer data3DiskSize) {
      this.data3DiskSize = data3DiskSize;
   }

   public void setMfcCloudId(final String mfcCloudId) {
      this.mfcCloudId = mfcCloudId;
   }

   public void setDateType(final Integer dateType) {
      this.dateType = dateType;
   }

   public void setMinDateTime(final String minDateTime) {
      this.minDateTime = minDateTime;
   }

   public void setMaxDateTime(final String maxDateTime) {
      this.maxDateTime = maxDateTime;
   }

   public void setSort(final String sort) {
      this.sort = sort;
   }
}
