package com.rxinyun.common.vhost.param;

public class VhostPasswordCommand {
   private String panelUrl;
   private String apiKey;
   private String panelHostId;
   private String hostName;
   private String domain;
   private String username;
   private String password;

   public String getPanelUrl() {
      return this.panelUrl;
   }

   public String getApiKey() {
      return this.apiKey;
   }

   public String getPanelHostId() {
      return this.panelHostId;
   }

   public String getHostName() {
      return this.hostName;
   }

   public String getDomain() {
      return this.domain;
   }

   public String getUsername() {
      return this.username;
   }

   public String getPassword() {
      return this.password;
   }

   public void setPanelUrl(final String panelUrl) {
      this.panelUrl = panelUrl;
   }

   public void setApiKey(final String apiKey) {
      this.apiKey = apiKey;
   }

   public void setPanelHostId(final String panelHostId) {
      this.panelHostId = panelHostId;
   }

   public void setHostName(final String hostName) {
      this.hostName = hostName;
   }

   public void setDomain(final String domain) {
      this.domain = domain;
   }

   public void setUsername(final String username) {
      this.username = username;
   }

   public void setPassword(final String password) {
      this.password = password;
   }
}
