package com.rxinyun.member.business.modular.esc.param;

import io.swagger.v3.oas.annotations.media.Schema;

public class MemberServerIpAddParam {
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
   private Integer vlanId;
   @Schema(
      description = "IP别名"
   )
   private String cname;
   @Schema(
      description = "是否主IP"
   )
   private Integer isMain;

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

   public Integer getVlanId() {
      return this.vlanId;
   }

   public String getCname() {
      return this.cname;
   }

   public Integer getIsMain() {
      return this.isMain;
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

   public void setVlanId(final Integer vlanId) {
      this.vlanId = vlanId;
   }

   public void setCname(final String cname) {
      this.cname = cname;
   }

   public void setIsMain(final Integer isMain) {
      this.isMain = isMain;
   }
}
