package com.rxinyun.business.core.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;

public class BaseIndependentIp implements Serializable {
   @Schema(
      description = "主键"
   )
   private String id;
   @Schema(
      description = "独立服务器 ID"
   )
   private String serverId;
   @Schema(
      description = "IP 地址"
   )
   private String ip;
   @Schema(
      description = "类型：PUBLIC / PRIVATE（与云机 ServerIp 一致）"
   )
   private String type;
   @Schema(
      description = "子网掩码"
   )
   private String mask;
   @Schema(
      description = "网关"
   )
   private String gateway;
   @Schema(
      description = "VLAN ID"
   )
   private Integer vlanId;
   @Schema(
      description = "别名"
   )
   private String cname;
   @Schema(
      description = "MAC"
   )
   private String mac;
   @Schema(
      description = "是否主 IP：1 是 0 否"
   )
   private Integer isMain;

   public String getId() {
      return this.id;
   }

   public String getServerId() {
      return this.serverId;
   }

   public String getIp() {
      return this.ip;
   }

   public String getType() {
      return this.type;
   }

   public String getMask() {
      return this.mask;
   }

   public String getGateway() {
      return this.gateway;
   }

   public Integer getVlanId() {
      return this.vlanId;
   }

   public String getCname() {
      return this.cname;
   }

   public String getMac() {
      return this.mac;
   }

   public Integer getIsMain() {
      return this.isMain;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setServerId(final String serverId) {
      this.serverId = serverId;
   }

   public void setIp(final String ip) {
      this.ip = ip;
   }

   public void setType(final String type) {
      this.type = type;
   }

   public void setMask(final String mask) {
      this.mask = mask;
   }

   public void setGateway(final String gateway) {
      this.gateway = gateway;
   }

   public void setVlanId(final Integer vlanId) {
      this.vlanId = vlanId;
   }

   public void setCname(final String cname) {
      this.cname = cname;
   }

   public void setMac(final String mac) {
      this.mac = mac;
   }

   public void setIsMain(final Integer isMain) {
      this.isMain = isMain;
   }
}
