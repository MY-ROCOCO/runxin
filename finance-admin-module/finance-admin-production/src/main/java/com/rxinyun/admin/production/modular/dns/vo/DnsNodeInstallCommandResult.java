package com.rxinyun.admin.production.modular.dns.vo;

import io.swagger.v3.oas.annotations.media.Schema;

public class DnsNodeInstallCommandResult {
   @Schema(
      description = "节点ID"
   )
   private String nodeId;
   @Schema(
      description = "节点安装命令"
   )
   private String installCommand;
   @Schema(
      description = "本次生成的回调Token，仅返回一次"
   )
   private String callbackToken;

   public String getNodeId() {
      return this.nodeId;
   }

   public String getInstallCommand() {
      return this.installCommand;
   }

   public String getCallbackToken() {
      return this.callbackToken;
   }

   public void setNodeId(final String nodeId) {
      this.nodeId = nodeId;
   }

   public void setInstallCommand(final String installCommand) {
      this.installCommand = installCommand;
   }

   public void setCallbackToken(final String callbackToken) {
      this.callbackToken = callbackToken;
   }

   public DnsNodeInstallCommandResult(final String nodeId, final String installCommand, final String callbackToken) {
      this.nodeId = nodeId;
      this.installCommand = installCommand;
      this.callbackToken = callbackToken;
   }
}
