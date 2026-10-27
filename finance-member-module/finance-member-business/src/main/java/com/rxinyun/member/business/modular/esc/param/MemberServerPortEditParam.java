package com.rxinyun.member.business.modular.esc.param;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;

public class MemberServerPortEditParam implements Serializable {
   private String id;
   @Schema(
      description = "端口名称"
   )
   private String portName;
   @Schema(
      description = "服务器ID"
   )
   private String serverId;
   @Schema(
      description = "服务器名称"
   )
   private String serverName;
   @Schema(
      description = "公网IP"
   )
   private String publicIp;
   @Schema(
      description = "内网IP"
   )
   private String privateIp;
   @Schema(
      description = "公网端口号"
   )
   private Integer publicPort;
   @Schema(
      description = "内网端口号"
   )
   private Integer privatePort;

   public String getId() {
      return this.id;
   }

   public String getPortName() {
      return this.portName;
   }

   public String getServerId() {
      return this.serverId;
   }

   public String getServerName() {
      return this.serverName;
   }

   public String getPublicIp() {
      return this.publicIp;
   }

   public String getPrivateIp() {
      return this.privateIp;
   }

   public Integer getPublicPort() {
      return this.publicPort;
   }

   public Integer getPrivatePort() {
      return this.privatePort;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setPortName(final String portName) {
      this.portName = portName;
   }

   public void setServerId(final String serverId) {
      this.serverId = serverId;
   }

   public void setServerName(final String serverName) {
      this.serverName = serverName;
   }

   public void setPublicIp(final String publicIp) {
      this.publicIp = publicIp;
   }

   public void setPrivateIp(final String privateIp) {
      this.privateIp = privateIp;
   }

   public void setPublicPort(final Integer publicPort) {
      this.publicPort = publicPort;
   }

   public void setPrivatePort(final Integer privatePort) {
      this.privatePort = privatePort;
   }
}
