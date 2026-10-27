package com.rxinyun.business.core.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class BaseMemberAddServer implements Serializable {
   @Schema(
      description = "ID"
   )
   private String id;
   @Schema(
      description = "订单号"
   )
   private String orderNo;
   @Schema(
      description = "宿主机ID"
   )
   private String masterId;
   @Schema(
      description = "主机名称"
   )
   private String hostName;
   @Schema(
      description = "可用区域ID"
   )
   private String availableId;
   @Schema(
      description = "线路ID"
   )
   private String lineId;
   @Schema(
      description = "操作系统分类ID"
   )
   private String systemClassId;
   @Schema(
      description = "操作系统分类名称"
   )
   private String systemClass;
   @Schema(
      description = "操作系统ID"
   )
   private String systemId;
   @Schema(
      description = "操作系统"
   )
   private String systemName;
   @Schema(
      description = "远程登录用户名"
   )
   private String systemUserName;
   @Schema(
      description = "远程端口"
   )
   private Integer port;
   @Schema(
      description = "镜像目录名称"
   )
   private String imageDirectoryName;
   @Schema(
      description = "备注"
   )
   private String remark;
   @Schema(
      description = "主机状态: 创建中 运行中 关机 锁定"
   )
   private Integer status;
   @Schema(
      description = "业务状态: 1: 正常, -1: 过期"
   )
   private Integer businessStatus;
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
      description = "实际内存"
   )
   private Integer ram;
   @Schema(
      description = "上行带宽"
   )
   private Integer bandwidth;
   @Schema(
      description = "下行带宽"
   )
   private Integer bandwidthIn;
   @Schema(
      description = "防御峰值"
   )
   private Integer defense;
   @Schema(
      description = "流量"
   )
   private Integer traffic;
   @Schema(
      description = "系统盘"
   )
   private Integer systemDisk;
   @Schema(
      description = "数据硬盘"
   )
   private List<Integer> dataDisks;
   @Schema(
      description = "IP数量"
   )
   private Integer ipNum;
   @Schema(
      description = "公网IP"
   )
   private String publicIp;
   @Schema(
      description = "公网网关"
   )
   private String publicGateway;
   @Schema(
      description = "公网DNS"
   )
   private String publicNetmask;
   @Schema(
      description = "公网Mac地址"
   )
   private String publicMac;
   @Schema(
      description = "私网IP"
   )
   private String privateIp;
   @Schema(
      description = "私网网关"
   )
   private String privateGateway;
   @Schema(
      description = "私网DNS"
   )
   private String privateNetmask;
   @Schema(
      description = "私网Mac地址"
   )
   private String privateMac;
   @Schema(
      description = "是否同步物理机时间：1：是, 0: 否"
   )
   private Integer syncTimeFlag;
   @Schema(
      description = "开通时间"
   )
   private Date buyTime;
   @Schema(
      description = "到期日期"
   )
   private Date endTime;
   @Schema(
      description = "到期删除时间"
   )
   private Date deleteTime;
   @Schema(
      description = "系统盘最高IOPS设置"
   )
   private Integer osMaxIops;
   @Schema(
      description = "数据盘最高IOPS设置"
   )
   private Integer disksMaxIops;
   @Schema(
      description = "会员编号"
   )
   private Long memberNo;
   @Schema(
      description = "续费价格"
   )
   private BigDecimal renewPrice;

   public String getId() {
      return this.id;
   }

   public String getOrderNo() {
      return this.orderNo;
   }

   public String getMasterId() {
      return this.masterId;
   }

   public String getHostName() {
      return this.hostName;
   }

   public String getAvailableId() {
      return this.availableId;
   }

   public String getLineId() {
      return this.lineId;
   }

   public String getSystemClassId() {
      return this.systemClassId;
   }

   public String getSystemClass() {
      return this.systemClass;
   }

   public String getSystemId() {
      return this.systemId;
   }

   public String getSystemName() {
      return this.systemName;
   }

   public String getSystemUserName() {
      return this.systemUserName;
   }

   public Integer getPort() {
      return this.port;
   }

   public String getImageDirectoryName() {
      return this.imageDirectoryName;
   }

   public String getRemark() {
      return this.remark;
   }

   public Integer getStatus() {
      return this.status;
   }

   public Integer getBusinessStatus() {
      return this.businessStatus;
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

   public Integer getRam() {
      return this.ram;
   }

   public Integer getBandwidth() {
      return this.bandwidth;
   }

   public Integer getBandwidthIn() {
      return this.bandwidthIn;
   }

   public Integer getDefense() {
      return this.defense;
   }

   public Integer getTraffic() {
      return this.traffic;
   }

   public Integer getSystemDisk() {
      return this.systemDisk;
   }

   public List<Integer> getDataDisks() {
      return this.dataDisks;
   }

   public Integer getIpNum() {
      return this.ipNum;
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

   public Integer getSyncTimeFlag() {
      return this.syncTimeFlag;
   }

   public Date getBuyTime() {
      return this.buyTime;
   }

   public Date getEndTime() {
      return this.endTime;
   }

   public Date getDeleteTime() {
      return this.deleteTime;
   }

   public Integer getOsMaxIops() {
      return this.osMaxIops;
   }

   public Integer getDisksMaxIops() {
      return this.disksMaxIops;
   }

   public Long getMemberNo() {
      return this.memberNo;
   }

   public BigDecimal getRenewPrice() {
      return this.renewPrice;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setOrderNo(final String orderNo) {
      this.orderNo = orderNo;
   }

   public void setMasterId(final String masterId) {
      this.masterId = masterId;
   }

   public void setHostName(final String hostName) {
      this.hostName = hostName;
   }

   public void setAvailableId(final String availableId) {
      this.availableId = availableId;
   }

   public void setLineId(final String lineId) {
      this.lineId = lineId;
   }

   public void setSystemClassId(final String systemClassId) {
      this.systemClassId = systemClassId;
   }

   public void setSystemClass(final String systemClass) {
      this.systemClass = systemClass;
   }

   public void setSystemId(final String systemId) {
      this.systemId = systemId;
   }

   public void setSystemName(final String systemName) {
      this.systemName = systemName;
   }

   public void setSystemUserName(final String systemUserName) {
      this.systemUserName = systemUserName;
   }

   public void setPort(final Integer port) {
      this.port = port;
   }

   public void setImageDirectoryName(final String imageDirectoryName) {
      this.imageDirectoryName = imageDirectoryName;
   }

   public void setRemark(final String remark) {
      this.remark = remark;
   }

   public void setStatus(final Integer status) {
      this.status = status;
   }

   public void setBusinessStatus(final Integer businessStatus) {
      this.businessStatus = businessStatus;
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

   public void setRam(final Integer ram) {
      this.ram = ram;
   }

   public void setBandwidth(final Integer bandwidth) {
      this.bandwidth = bandwidth;
   }

   public void setBandwidthIn(final Integer bandwidthIn) {
      this.bandwidthIn = bandwidthIn;
   }

   public void setDefense(final Integer defense) {
      this.defense = defense;
   }

   public void setTraffic(final Integer traffic) {
      this.traffic = traffic;
   }

   public void setSystemDisk(final Integer systemDisk) {
      this.systemDisk = systemDisk;
   }

   public void setDataDisks(final List<Integer> dataDisks) {
      this.dataDisks = dataDisks;
   }

   public void setIpNum(final Integer ipNum) {
      this.ipNum = ipNum;
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

   public void setSyncTimeFlag(final Integer syncTimeFlag) {
      this.syncTimeFlag = syncTimeFlag;
   }

   public void setBuyTime(final Date buyTime) {
      this.buyTime = buyTime;
   }

   public void setEndTime(final Date endTime) {
      this.endTime = endTime;
   }

   public void setDeleteTime(final Date deleteTime) {
      this.deleteTime = deleteTime;
   }

   public void setOsMaxIops(final Integer osMaxIops) {
      this.osMaxIops = osMaxIops;
   }

   public void setDisksMaxIops(final Integer disksMaxIops) {
      this.disksMaxIops = disksMaxIops;
   }

   public void setMemberNo(final Long memberNo) {
      this.memberNo = memberNo;
   }

   public void setRenewPrice(final BigDecimal renewPrice) {
      this.renewPrice = renewPrice;
   }
}
