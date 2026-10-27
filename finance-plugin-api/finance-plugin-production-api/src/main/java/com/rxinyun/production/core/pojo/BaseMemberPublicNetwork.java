package com.rxinyun.production.core.pojo;

import com.rxinyun.common.pojo.CommonEntity;
import io.swagger.v3.oas.annotations.media.Schema;

public class BaseMemberPublicNetwork extends CommonEntity {
   @Schema(
      description = "主键 ID"
   )
   private String id;
   @Schema(
      description = "线路ID"
   )
   private String lineId;
   @Schema(
      description = "线路名称"
   )
   private String lineName;
   @Schema(
      description = "宿主机ID"
   )
   private String masterId;
   @Schema(
      description = "宿主机IP"
   )
   private String masterIp;
   @Schema(
      description = "IP地址"
   )
   private String ip;
   @Schema(
      description = "网络ID列表"
   )
   private String networkIds;
   @Schema(
      description = "子网掩码"
   )
   private String mask;
   @Schema(
      description = "网关地址"
   )
   private String gateway;
   @Schema(
      description = "VLAN ID"
   )
   private int vlanId;
   @Schema(
      description = "IP别名"
   )
   private String cname;
   @Schema(
      description = "可用性（0 未占用，1 已占用"
   )
   private int enable;
   @Schema(
      description = "备注信息"
   )
   private String remarks;
   @Schema(
      description = "备注信息"
   )
   private int version;

   public String getId() {
      return this.id;
   }

   public String getLineId() {
      return this.lineId;
   }

   public String getLineName() {
      return this.lineName;
   }

   public String getMasterId() {
      return this.masterId;
   }

   public String getMasterIp() {
      return this.masterIp;
   }

   public String getIp() {
      return this.ip;
   }

   public String getNetworkIds() {
      return this.networkIds;
   }

   public String getMask() {
      return this.mask;
   }

   public String getGateway() {
      return this.gateway;
   }

   public int getVlanId() {
      return this.vlanId;
   }

   public String getCname() {
      return this.cname;
   }

   public int getEnable() {
      return this.enable;
   }

   public String getRemarks() {
      return this.remarks;
   }

   public int getVersion() {
      return this.version;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setLineId(final String lineId) {
      this.lineId = lineId;
   }

   public void setLineName(final String lineName) {
      this.lineName = lineName;
   }

   public void setMasterId(final String masterId) {
      this.masterId = masterId;
   }

   public void setMasterIp(final String masterIp) {
      this.masterIp = masterIp;
   }

   public void setIp(final String ip) {
      this.ip = ip;
   }

   public void setNetworkIds(final String networkIds) {
      this.networkIds = networkIds;
   }

   public void setMask(final String mask) {
      this.mask = mask;
   }

   public void setGateway(final String gateway) {
      this.gateway = gateway;
   }

   public void setVlanId(final int vlanId) {
      this.vlanId = vlanId;
   }

   public void setCname(final String cname) {
      this.cname = cname;
   }

   public void setEnable(final int enable) {
      this.enable = enable;
   }

   public void setRemarks(final String remarks) {
      this.remarks = remarks;
   }

   public void setVersion(final int version) {
      this.version = version;
   }
}
