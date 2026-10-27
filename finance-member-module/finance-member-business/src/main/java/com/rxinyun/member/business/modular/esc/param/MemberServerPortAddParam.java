package com.rxinyun.member.business.modular.esc.param;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;

public class MemberServerPortAddParam implements Serializable {
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
      description = "公网端口号，不传时自动分配"
   )
   private Integer publicPort;
   @Schema(
      description = "内网端口号；不传时默认与自动分配的公网端口相同"
   )
   private Integer privatePort;

   public String getPortName() {
      return this.portName;
   }

   public String getServerId() {
      return this.serverId;
   }

   public String getServerName() {
      return this.serverName;
   }

   public Integer getPublicPort() {
      return this.publicPort;
   }

   public Integer getPrivatePort() {
      return this.privatePort;
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

   public void setPublicPort(final Integer publicPort) {
      this.publicPort = publicPort;
   }

   public void setPrivatePort(final Integer privatePort) {
      this.privatePort = privatePort;
   }
}
