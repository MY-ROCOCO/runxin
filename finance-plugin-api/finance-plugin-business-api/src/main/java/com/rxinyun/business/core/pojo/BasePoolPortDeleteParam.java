package com.rxinyun.business.core.pojo;

import io.swagger.v3.oas.annotations.media.Schema;

public class BasePoolPortDeleteParam {
   @Schema(
      description = "云服务器ID"
   )
   private String serverId;
   @Schema(
      description = "内网端口"
   )
   private Integer privatePort;
   @Schema(
      description = "公网端口（可选，用于精确定位）"
   )
   private Integer publicPort;

   public String getServerId() {
      return this.serverId;
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

   public void setPrivatePort(final Integer privatePort) {
      this.privatePort = privatePort;
   }

   public void setPublicPort(final Integer publicPort) {
      this.publicPort = publicPort;
   }
}
