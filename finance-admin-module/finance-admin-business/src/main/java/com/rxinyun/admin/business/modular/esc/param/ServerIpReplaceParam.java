package com.rxinyun.admin.business.modular.esc.param;

import io.swagger.v3.oas.annotations.media.Schema;

public class ServerIpReplaceParam {
   @Schema(
      description = "云服务器ID"
   )
   private String serverId;
   @Schema(
      description = "新的公网IP池ID"
   )
   private String publicNetworkId;
   @Schema(
      description = "当前主公网IP明细ID，不传时默认取当前主公网IP"
   )
   private String currentServerIpId;

   public String getServerId() {
      return this.serverId;
   }

   public String getPublicNetworkId() {
      return this.publicNetworkId;
   }

   public String getCurrentServerIpId() {
      return this.currentServerIpId;
   }

   public void setServerId(final String serverId) {
      this.serverId = serverId;
   }

   public void setPublicNetworkId(final String publicNetworkId) {
      this.publicNetworkId = publicNetworkId;
   }

   public void setCurrentServerIpId(final String currentServerIpId) {
      this.currentServerIpId = currentServerIpId;
   }
}
