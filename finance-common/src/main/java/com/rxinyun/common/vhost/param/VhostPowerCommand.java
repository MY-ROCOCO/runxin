package com.rxinyun.common.vhost.param;

public class VhostPowerCommand {
   private String panelUrl;
   private String apiKey;
   private String panelHostId;
   private String domain;
   private String phpVersion;
   private String bindDomain;

   public String getPanelUrl() {
      return this.panelUrl;
   }

   public String getApiKey() {
      return this.apiKey;
   }

   public String getPanelHostId() {
      return this.panelHostId;
   }

   public String getDomain() {
      return this.domain;
   }

   public String getPhpVersion() {
      return this.phpVersion;
   }

   public String getBindDomain() {
      return this.bindDomain;
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

   public void setDomain(final String domain) {
      this.domain = domain;
   }

   public void setPhpVersion(final String phpVersion) {
      this.phpVersion = phpVersion;
   }

   public void setBindDomain(final String bindDomain) {
      this.bindDomain = bindDomain;
   }
}
