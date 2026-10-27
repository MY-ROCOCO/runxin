package com.rxinyun.admin.business.modular.independent.param;

import io.swagger.v3.oas.annotations.media.Schema;

public class IndependentIpEditParam {
   @Schema(
      description = "服务器IP标识"
   )
   private String id;
   @Schema(
      description = "服务器ID"
   )
   private String serverId;
   @Schema(
      description = "服务器IP"
   )
   private String ip;
   @Schema(
      description = "子网掩码"
   )
   private String mask;
   @Schema(
      description = "网关地址"
   )
   private String gateway;
   @Schema(
      description = "服务器IP类型: 公网：PUBLIC 私网：PRIVATE "
   )
   private String type;
   @Schema(
      description = "VLAN ID"
   )
   private int vlanId;
   @Schema(
      description = "IP别名"
   )
   private String cname;
   @Schema(
      description = "Mac地址"
   )
   private String mac;
   @Schema(
      description = "是否主IP"
   )
   private String isMain;

   public String getId() {
      return this.id;
   }

   public String getServerId() {
      return this.serverId;
   }

   public String getIp() {
      return this.ip;
   }

   public String getMask() {
      return this.mask;
   }

   public String getGateway() {
      return this.gateway;
   }

   public String getType() {
      return this.type;
   }

   public int getVlanId() {
      return this.vlanId;
   }

   public String getCname() {
      return this.cname;
   }

   public String getMac() {
      return this.mac;
   }

   public String getIsMain() {
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

   public void setMask(final String mask) {
      this.mask = mask;
   }

   public void setGateway(final String gateway) {
      this.gateway = gateway;
   }

   public void setType(final String type) {
      this.type = type;
   }

   public void setVlanId(final int vlanId) {
      this.vlanId = vlanId;
   }

   public void setCname(final String cname) {
      this.cname = cname;
   }

   public void setMac(final String mac) {
      this.mac = mac;
   }

   public void setIsMain(final String isMain) {
      this.isMain = isMain;
   }
}
