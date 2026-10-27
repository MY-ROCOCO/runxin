package com.rxinyun.business.core.pojo;

import io.swagger.v3.oas.annotations.media.Schema;

public class BasePoolPortAddParam {
   @Schema(
      description = "云服务器ID（A 系统 business_server.id）"
   )
   private String serverId;
   @Schema(
      description = "端口名称"
   )
   private String portName;
   @Schema(
      description = "内网端口；不传时默认与自动分配的公网端口相同"
   )
   private Integer privatePort;
   @Schema(
      description = "公网端口，0 或不传表示由上游自动分配"
   )
   private Integer publicPort;

   public String getServerId() {
      return this.serverId;
   }

   public String getPortName() {
      return this.portName;
   }

   public Integer getPrivatePort() {
      return this.privatePort;
   }

   public Integer getPublicPort() {
      return this.publicPort;
   }

   public void setServerId(final String serverId) {
      this.serverId = serverId;
   }

   public void setPortName(final String portName) {
      this.portName = portName;
   }

   public void setPrivatePort(final Integer privatePort) {
      this.privatePort = privatePort;
   }

   public void setPublicPort(final Integer publicPort) {
      this.publicPort = publicPort;
   }
}
