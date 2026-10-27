package com.rxinyun.business.core.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class BaseMemberIndependentServer implements Serializable {
   @Schema(
      description = "服务器标识"
   )
   private String id;
   @Schema(
      description = "线路ID"
   )
   private String lineId;
   @Schema(
      description = "机型ID"
   )
   private String machineId;
   @Schema(
      description = "机型名称"
   )
   private String machineName;
   @Schema(
      description = "服务器名称"
   )
   private String serverName;
   @Schema(
      description = "远程IP"
   )
   private String serverIp;
   @Schema(
      description = "远程端口"
   )
   private Integer port;
   @Schema(
      description = "远程登录用户名"
   )
   private String systemUserName;
   @Schema(
      description = "备注"
   )
   private String remark;
   @Schema(
      description = "主机状态: CREATING 创建中,  RUNNING 运行中, STOP 已关机, LOCKED 锁定, REINSTALLING 系统重装中"
   )
   private String serverStatus;
   @Schema(
      description = "业务状态: 1: 正常, -1: 过期"
   )
   private Integer businessStatus;
   @Schema(
      description = "系统密码"
   )
   private String systemPassword;
   @Schema(
      description = "资源类型: 0 自生产 1 资源池"
   )
   private Integer type;
   @Schema(
      description = "资源池ID"
   )
   private String resourcePoolId;
   @Schema(
      description = "上游(A)侧独立服务器主键，与 B 表 UPSTREAM_SERVER_ID 一致"
   )
   private String upstreamServerId;
   @Schema(
      description = "自动化: 0 库存自动化 1 无库存人工服务机型"
   )
   private Integer automation;
   @Schema(
      description = "cpu规格信息"
   )
   private String cpuInfo;
   @Schema(
      description = "内存规格信息"
   )
   private String ramInfo;
   @Schema(
      description = "硬盘规格信息"
   )
   private String disksInfo;
   @Schema(
      description = "ip数量"
   )
   private Integer ipSize;
   @Schema(
      description = "防御"
   )
   private Integer defense;
   @Schema(
      description = "带宽"
   )
   private Integer bandwidth;
   @Schema(
      description = "升级 0不支持  1支持"
   )
   private Integer upgrades;
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
      description = "会员编号"
   )
   private Long memberNo;
   @Schema(
      description = "是否自动续费"
   )
   private Integer renewAuto;
   @Schema(
      description = "续费价格"
   )
   private BigDecimal renewPrice;
   @Schema(
      description = "用户备注"
   )
   private String memberRemark;
   @Schema(
      description = "操作系统分类ID"
   )
   private String systemClassId;
   @Schema(
      description = "操作类型"
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

   public String getId() {
      return this.id;
   }

   public String getLineId() {
      return this.lineId;
   }

   public String getMachineId() {
      return this.machineId;
   }

   public String getMachineName() {
      return this.machineName;
   }

   public String getServerName() {
      return this.serverName;
   }

   public String getServerIp() {
      return this.serverIp;
   }

   public Integer getPort() {
      return this.port;
   }

   public String getSystemUserName() {
      return this.systemUserName;
   }

   public String getRemark() {
      return this.remark;
   }

   public String getServerStatus() {
      return this.serverStatus;
   }

   public Integer getBusinessStatus() {
      return this.businessStatus;
   }

   public String getSystemPassword() {
      return this.systemPassword;
   }

   public Integer getType() {
      return this.type;
   }

   public String getResourcePoolId() {
      return this.resourcePoolId;
   }

   public String getUpstreamServerId() {
      return this.upstreamServerId;
   }

   public Integer getAutomation() {
      return this.automation;
   }

   public String getCpuInfo() {
      return this.cpuInfo;
   }

   public String getRamInfo() {
      return this.ramInfo;
   }

   public String getDisksInfo() {
      return this.disksInfo;
   }

   public Integer getIpSize() {
      return this.ipSize;
   }

   public Integer getDefense() {
      return this.defense;
   }

   public Integer getBandwidth() {
      return this.bandwidth;
   }

   public Integer getUpgrades() {
      return this.upgrades;
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

   public Long getMemberNo() {
      return this.memberNo;
   }

   public Integer getRenewAuto() {
      return this.renewAuto;
   }

   public BigDecimal getRenewPrice() {
      return this.renewPrice;
   }

   public String getMemberRemark() {
      return this.memberRemark;
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

   public void setId(final String id) {
      this.id = id;
   }

   public void setLineId(final String lineId) {
      this.lineId = lineId;
   }

   public void setMachineId(final String machineId) {
      this.machineId = machineId;
   }

   public void setMachineName(final String machineName) {
      this.machineName = machineName;
   }

   public void setServerName(final String serverName) {
      this.serverName = serverName;
   }

   public void setServerIp(final String serverIp) {
      this.serverIp = serverIp;
   }

   public void setPort(final Integer port) {
      this.port = port;
   }

   public void setSystemUserName(final String systemUserName) {
      this.systemUserName = systemUserName;
   }

   public void setRemark(final String remark) {
      this.remark = remark;
   }

   public void setServerStatus(final String serverStatus) {
      this.serverStatus = serverStatus;
   }

   public void setBusinessStatus(final Integer businessStatus) {
      this.businessStatus = businessStatus;
   }

   public void setSystemPassword(final String systemPassword) {
      this.systemPassword = systemPassword;
   }

   public void setType(final Integer type) {
      this.type = type;
   }

   public void setResourcePoolId(final String resourcePoolId) {
      this.resourcePoolId = resourcePoolId;
   }

   public void setUpstreamServerId(final String upstreamServerId) {
      this.upstreamServerId = upstreamServerId;
   }

   public void setAutomation(final Integer automation) {
      this.automation = automation;
   }

   public void setCpuInfo(final String cpuInfo) {
      this.cpuInfo = cpuInfo;
   }

   public void setRamInfo(final String ramInfo) {
      this.ramInfo = ramInfo;
   }

   public void setDisksInfo(final String disksInfo) {
      this.disksInfo = disksInfo;
   }

   public void setIpSize(final Integer ipSize) {
      this.ipSize = ipSize;
   }

   public void setDefense(final Integer defense) {
      this.defense = defense;
   }

   public void setBandwidth(final Integer bandwidth) {
      this.bandwidth = bandwidth;
   }

   public void setUpgrades(final Integer upgrades) {
      this.upgrades = upgrades;
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

   public void setMemberNo(final Long memberNo) {
      this.memberNo = memberNo;
   }

   public void setRenewAuto(final Integer renewAuto) {
      this.renewAuto = renewAuto;
   }

   public void setRenewPrice(final BigDecimal renewPrice) {
      this.renewPrice = renewPrice;
   }

   public void setMemberRemark(final String memberRemark) {
      this.memberRemark = memberRemark;
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
}
