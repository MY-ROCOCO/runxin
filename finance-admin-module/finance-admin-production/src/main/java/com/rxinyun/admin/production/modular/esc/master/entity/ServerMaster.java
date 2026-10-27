package com.rxinyun.admin.production.modular.esc.master.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.rxinyun.common.pojo.CommonEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@TableName(
   value = "SERVER_MASTER",
   autoResultMap = true
)
public class ServerMaster extends CommonEntity {
   @Schema(
      description = "宿主机标识符"
   )
   private Integer id;
   @Schema(
      description = "线路名称"
   )
   @TableField(
      exist = false
   )
   private String lineName;
   @Schema(
      description = "绑定线路ID列表"
   )
   @TableField(
      exist = false
   )
   private List<String> lineIds;
   @Schema(
      description = "绑定线路名称，逗号分隔"
   )
   @TableField(
      exist = false
   )
   private String lineNames;
   @Schema(
      description = "宿主机名称"
   )
   private String masterName;
   @Schema(
      description = "虚拟化被控版本类型：1=轻舟 Hyper‑V受控 2=魔方云受控 3=RkHyper-v受控",
      example = "1"
   )
   private String guardVersion;
   @Schema(
      description = "BMC IP"
   )
   private String localIp;
   @Schema(
      description = "网关IP地址"
   )
   private String gatewayIp;
   @Schema(
      description = "交换机类型"
   )
   private String switchType;
   @Schema(
      description = "IP或域名"
   )
   private String ip;
   @Schema(
      description = "通信端口"
   )
   private Integer apiPort;
   @Schema(
      description = "通信密码"
   )
   private String apiPassword;
   @Schema(
      description = "魔方云登陆账号"
   )
   private String mfcLoginAccount;
   @Schema(
      description = "魔方云登陆密码"
   )
   private String mfcLoginPassword;
   @Schema(
      description = "魔方云节点ID"
   )
   private String mfcNodeId;
   @Schema(
      description = "宿主机状态: 1 启用 0 禁用"
   )
   private Integer status;
   @Schema(
      description = "分配权重，默认分配规则下权重越大分配占比越高"
   )
   private Integer sortId;
   @Schema(
      description = "当前云服务器实例数量"
   )
   private Integer instanceCount;
   @Schema(
      description = "当前宿主机绑定公网IP数量"
   )
   @TableField(
      exist = false
   )
   private Integer publicIpCount;
   @Schema(
      description = "剩余内存(GB)"
   )
   private Integer ramRemaining;
   @Schema(
      description = "剩余磁盘(GB)"
   )
   private Long diskRemaining;
   @Schema(
      description = "备注"
   )
   private String remarks;
   @Schema(
      description = "存储类型"
   )
   private String storeType;
   @Schema(
      description = "存储ID"
   )
   private String storeId;
   @Schema(
      description = "Root端口"
   )
   private Integer rootPort;
   @Schema(
      description = "Root密码"
   )
   private String rootPassword;
   @Schema(
      description = "系统盘磁盘文件存放目录"
   )
   private String osPath;
   @Schema(
      description = "系统盘IOPS设置"
   )
   private Integer osMaxiops;
   @Schema(
      description = "数据盘磁盘文件存放目录"
   )
   private String disksPath;
   @Schema(
      description = "虚拟机配置保存目录"
   )
   private String vmConfigPath;
   @Schema(
      description = "数据盘IOPS设置"
   )
   private Integer disksMaxiops;
   @Schema(
      description = "最小IOPS"
   )
   private Long minimumIops;
   @Schema(
      description = "最大IOPS"
   )
   private Long maximumIops;
   @Schema(
      description = "CPU上限"
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
      description = "内存总大小"
   )
   private Integer ramSize;
   @Schema(
      description = "内存限制大小"
   )
   private Integer ramLimit;
   @Schema(
      description = "硬盘总大小"
   )
   private Integer harddiskSize;
   @Schema(
      description = "硬盘限制大小"
   )
   private Integer harddiskLimit;
   @Schema(
      description = "虚拟化存储类型"
   )
   private String hvStoreType;
   @Schema(
      description = "虚拟化存储ID"
   )
   private String hvStoreId;
   @Schema(
      description = "节点名称"
   )
   private String nodeName;
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
      description = "VNC地址"
   )
   private String vncUrl;
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
      description = "是否共享IP"
   )
   private Integer shareIp;
   @Schema(
      description = "镜像文件存放目录"
   )
   private String mirrorPath;
   @Schema(
      description = "备份文件存放目录"
   )
   private String backupPath;
   @Schema(
      description = "任务并发数"
   )
   private int taskConcurrency;
   @Schema(
      description = "密码"
   )
   private String password;

   public Integer getId() {
      return this.id;
   }

   public String getLineName() {
      return this.lineName;
   }

   public List<String> getLineIds() {
      return this.lineIds;
   }

   public String getLineNames() {
      return this.lineNames;
   }

   public String getMasterName() {
      return this.masterName;
   }

   public String getGuardVersion() {
      return this.guardVersion;
   }

   public String getLocalIp() {
      return this.localIp;
   }

   public String getGatewayIp() {
      return this.gatewayIp;
   }

   public String getSwitchType() {
      return this.switchType;
   }

   public String getIp() {
      return this.ip;
   }

   public Integer getApiPort() {
      return this.apiPort;
   }

   public String getApiPassword() {
      return this.apiPassword;
   }

   public String getMfcLoginAccount() {
      return this.mfcLoginAccount;
   }

   public String getMfcLoginPassword() {
      return this.mfcLoginPassword;
   }

   public String getMfcNodeId() {
      return this.mfcNodeId;
   }

   public Integer getStatus() {
      return this.status;
   }

   public Integer getSortId() {
      return this.sortId;
   }

   public Integer getInstanceCount() {
      return this.instanceCount;
   }

   public Integer getPublicIpCount() {
      return this.publicIpCount;
   }

   public Integer getRamRemaining() {
      return this.ramRemaining;
   }

   public Long getDiskRemaining() {
      return this.diskRemaining;
   }

   public String getRemarks() {
      return this.remarks;
   }

   public String getStoreType() {
      return this.storeType;
   }

   public String getStoreId() {
      return this.storeId;
   }

   public Integer getRootPort() {
      return this.rootPort;
   }

   public String getRootPassword() {
      return this.rootPassword;
   }

   public String getOsPath() {
      return this.osPath;
   }

   public Integer getOsMaxiops() {
      return this.osMaxiops;
   }

   public String getDisksPath() {
      return this.disksPath;
   }

   public String getVmConfigPath() {
      return this.vmConfigPath;
   }

   public Integer getDisksMaxiops() {
      return this.disksMaxiops;
   }

   public Long getMinimumIops() {
      return this.minimumIops;
   }

   public Long getMaximumIops() {
      return this.maximumIops;
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

   public Integer getRamSize() {
      return this.ramSize;
   }

   public Integer getRamLimit() {
      return this.ramLimit;
   }

   public Integer getHarddiskSize() {
      return this.harddiskSize;
   }

   public Integer getHarddiskLimit() {
      return this.harddiskLimit;
   }

   public String getHvStoreType() {
      return this.hvStoreType;
   }

   public String getHvStoreId() {
      return this.hvStoreId;
   }

   public String getNodeName() {
      return this.nodeName;
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

   public String getVncUrl() {
      return this.vncUrl;
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

   public Integer getShareIp() {
      return this.shareIp;
   }

   public String getMirrorPath() {
      return this.mirrorPath;
   }

   public String getBackupPath() {
      return this.backupPath;
   }

   public int getTaskConcurrency() {
      return this.taskConcurrency;
   }

   public String getPassword() {
      return this.password;
   }

   public void setId(final Integer id) {
      this.id = id;
   }

   public void setLineName(final String lineName) {
      this.lineName = lineName;
   }

   public void setLineIds(final List<String> lineIds) {
      this.lineIds = lineIds;
   }

   public void setLineNames(final String lineNames) {
      this.lineNames = lineNames;
   }

   public void setMasterName(final String masterName) {
      this.masterName = masterName;
   }

   public void setGuardVersion(final String guardVersion) {
      this.guardVersion = guardVersion;
   }

   public void setLocalIp(final String localIp) {
      this.localIp = localIp;
   }

   public void setGatewayIp(final String gatewayIp) {
      this.gatewayIp = gatewayIp;
   }

   public void setSwitchType(final String switchType) {
      this.switchType = switchType;
   }

   public void setIp(final String ip) {
      this.ip = ip;
   }

   public void setApiPort(final Integer apiPort) {
      this.apiPort = apiPort;
   }

   public void setApiPassword(final String apiPassword) {
      this.apiPassword = apiPassword;
   }

   public void setMfcLoginAccount(final String mfcLoginAccount) {
      this.mfcLoginAccount = mfcLoginAccount;
   }

   public void setMfcLoginPassword(final String mfcLoginPassword) {
      this.mfcLoginPassword = mfcLoginPassword;
   }

   public void setMfcNodeId(final String mfcNodeId) {
      this.mfcNodeId = mfcNodeId;
   }

   public void setStatus(final Integer status) {
      this.status = status;
   }

   public void setSortId(final Integer sortId) {
      this.sortId = sortId;
   }

   public void setInstanceCount(final Integer instanceCount) {
      this.instanceCount = instanceCount;
   }

   public void setPublicIpCount(final Integer publicIpCount) {
      this.publicIpCount = publicIpCount;
   }

   public void setRamRemaining(final Integer ramRemaining) {
      this.ramRemaining = ramRemaining;
   }

   public void setDiskRemaining(final Long diskRemaining) {
      this.diskRemaining = diskRemaining;
   }

   public void setRemarks(final String remarks) {
      this.remarks = remarks;
   }

   public void setStoreType(final String storeType) {
      this.storeType = storeType;
   }

   public void setStoreId(final String storeId) {
      this.storeId = storeId;
   }

   public void setRootPort(final Integer rootPort) {
      this.rootPort = rootPort;
   }

   public void setRootPassword(final String rootPassword) {
      this.rootPassword = rootPassword;
   }

   public void setOsPath(final String osPath) {
      this.osPath = osPath;
   }

   public void setOsMaxiops(final Integer osMaxiops) {
      this.osMaxiops = osMaxiops;
   }

   public void setDisksPath(final String disksPath) {
      this.disksPath = disksPath;
   }

   public void setVmConfigPath(final String vmConfigPath) {
      this.vmConfigPath = vmConfigPath;
   }

   public void setDisksMaxiops(final Integer disksMaxiops) {
      this.disksMaxiops = disksMaxiops;
   }

   public void setMinimumIops(final Long minimumIops) {
      this.minimumIops = minimumIops;
   }

   public void setMaximumIops(final Long maximumIops) {
      this.maximumIops = maximumIops;
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

   public void setRamSize(final Integer ramSize) {
      this.ramSize = ramSize;
   }

   public void setRamLimit(final Integer ramLimit) {
      this.ramLimit = ramLimit;
   }

   public void setHarddiskSize(final Integer harddiskSize) {
      this.harddiskSize = harddiskSize;
   }

   public void setHarddiskLimit(final Integer harddiskLimit) {
      this.harddiskLimit = harddiskLimit;
   }

   public void setHvStoreType(final String hvStoreType) {
      this.hvStoreType = hvStoreType;
   }

   public void setHvStoreId(final String hvStoreId) {
      this.hvStoreId = hvStoreId;
   }

   public void setNodeName(final String nodeName) {
      this.nodeName = nodeName;
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

   public void setVncUrl(final String vncUrl) {
      this.vncUrl = vncUrl;
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

   public void setShareIp(final Integer shareIp) {
      this.shareIp = shareIp;
   }

   public void setMirrorPath(final String mirrorPath) {
      this.mirrorPath = mirrorPath;
   }

   public void setBackupPath(final String backupPath) {
      this.backupPath = backupPath;
   }

   public void setTaskConcurrency(final int taskConcurrency) {
      this.taskConcurrency = taskConcurrency;
   }

   public void setPassword(final String password) {
      this.password = password;
   }
}
