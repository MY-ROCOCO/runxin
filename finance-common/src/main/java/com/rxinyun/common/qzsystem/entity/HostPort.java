package com.rxinyun.common.qzsystem.entity;

import io.swagger.v3.oas.annotations.media.Schema;

public class HostPort {
   @Schema(
      description = "主机名称"
   )
   private String hostName;
   @Schema(
      description = "宿主机IP"
   )
   private String masterIp;
   @Schema(
      description = "受控密钥"
   )
   private String apiKey;
   @Schema(
      description = "受控端口号"
   )
   private Integer apiPort;
   @Schema(
      description = "公网端口号"
   )
   private Integer publicPort;
   @Schema(
      description = "内网端口号"
   )
   private Integer privatePort;
   @Schema(
      description = "公网IP"
   )
   private String publicIp;
   @Schema(
      description = "内网IP"
   )
   private String privateIp;

   public String getHostName() {
      return this.hostName;
   }

   public String getMasterIp() {
      return this.masterIp;
   }

   public String getApiKey() {
      return this.apiKey;
   }

   public Integer getApiPort() {
      return this.apiPort;
   }

   public Integer getPublicPort() {
      return this.publicPort;
   }

   public Integer getPrivatePort() {
      return this.privatePort;
   }

   public String getPublicIp() {
      return this.publicIp;
   }

   public String getPrivateIp() {
      return this.privateIp;
   }

   public void setHostName(final String hostName) {
      this.hostName = hostName;
   }

   public void setMasterIp(final String masterIp) {
      this.masterIp = masterIp;
   }

   public void setApiKey(final String apiKey) {
      this.apiKey = apiKey;
   }

   public void setApiPort(final Integer apiPort) {
      this.apiPort = apiPort;
   }

   public void setPublicPort(final Integer publicPort) {
      this.publicPort = publicPort;
   }

   public void setPrivatePort(final Integer privatePort) {
      this.privatePort = privatePort;
   }

   public void setPublicIp(final String publicIp) {
      this.publicIp = publicIp;
   }

   public void setPrivateIp(final String privateIp) {
      this.privateIp = privateIp;
   }
}
