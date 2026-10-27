package com.rxinyun.common.qzsystem.entity;

import io.swagger.v3.oas.annotations.media.Schema;

public class HostStrategy {
   @Schema(
      description = "策略名称"
   )
   private String strategyName;
   @Schema(
      description = "云服务器ID"
   )
   private String serverId;
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
      description = "策略：1允许通过，2拒绝通过"
   )
   private Integer action;
   @Schema(
      description = "方向：1流入，2流出"
   )
   private String direction;
   @Schema(
      description = "IP地址"
   )
   private String ip;
   @Schema(
      description = "端口"
   )
   private Integer port;
   @Schema(
      description = "协议类型：any tcp udp icmp"
   )
   private String protocol;
   @Schema(
      description = "优先级：1-10000"
   )
   private Integer weight;
   @Schema(
      description = "原优先级"
   )
   private Integer oldWeight;

   public String getStrategyName() {
      return this.strategyName;
   }

   public String getServerId() {
      return this.serverId;
   }

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

   public Integer getAction() {
      return this.action;
   }

   public String getDirection() {
      return this.direction;
   }

   public String getIp() {
      return this.ip;
   }

   public Integer getPort() {
      return this.port;
   }

   public String getProtocol() {
      return this.protocol;
   }

   public Integer getWeight() {
      return this.weight;
   }

   public Integer getOldWeight() {
      return this.oldWeight;
   }

   public void setStrategyName(final String strategyName) {
      this.strategyName = strategyName;
   }

   public void setServerId(final String serverId) {
      this.serverId = serverId;
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

   public void setAction(final Integer action) {
      this.action = action;
   }

   public void setDirection(final String direction) {
      this.direction = direction;
   }

   public void setIp(final String ip) {
      this.ip = ip;
   }

   public void setPort(final Integer port) {
      this.port = port;
   }

   public void setProtocol(final String protocol) {
      this.protocol = protocol;
   }

   public void setWeight(final Integer weight) {
      this.weight = weight;
   }

   public void setOldWeight(final Integer oldWeight) {
      this.oldWeight = oldWeight;
   }
}
