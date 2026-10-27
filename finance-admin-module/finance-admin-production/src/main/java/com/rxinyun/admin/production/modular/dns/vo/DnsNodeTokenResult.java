package com.rxinyun.admin.production.modular.dns.vo;

import io.swagger.v3.oas.annotations.media.Schema;

public class DnsNodeTokenResult {
   @Schema(
      description = "节点ID"
   )
   private String nodeId;
   @Schema(
      description = "节点API Token，仅返回一次"
   )
   private String apiToken;
   @Schema(
      description = "回调Token，仅返回一次"
   )
   private String callbackToken;

   public String getNodeId() {
      return this.nodeId;
   }

   public String getApiToken() {
      return this.apiToken;
   }

   public String getCallbackToken() {
      return this.callbackToken;
   }

   public void setNodeId(final String nodeId) {
      this.nodeId = nodeId;
   }

   public void setApiToken(final String apiToken) {
      this.apiToken = apiToken;
   }

   public void setCallbackToken(final String callbackToken) {
      this.callbackToken = callbackToken;
   }

   public DnsNodeTokenResult(final String nodeId, final String apiToken, final String callbackToken) {
      this.nodeId = nodeId;
      this.apiToken = apiToken;
      this.callbackToken = callbackToken;
   }
}
