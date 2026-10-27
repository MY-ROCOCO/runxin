package com.rxinyun.common.vhost.param;

public class VhostCreateCommand {
   private String panelUrl;
   private String apiKey;
   private String hostName;
   private String domain;
   private String phpVersion;
   private String remark;
   private String ftpUser;
   private String ftpPassword;
   private String dbUser;
   private String dbPassword;

   public String getPanelUrl() {
      return this.panelUrl;
   }

   public String getApiKey() {
      return this.apiKey;
   }

   public String getHostName() {
      return this.hostName;
   }

   public String getDomain() {
      return this.domain;
   }

   public String getPhpVersion() {
      return this.phpVersion;
   }

   public String getRemark() {
      return this.remark;
   }

   public String getFtpUser() {
      return this.ftpUser;
   }

   public String getFtpPassword() {
      return this.ftpPassword;
   }

   public String getDbUser() {
      return this.dbUser;
   }

   public String getDbPassword() {
      return this.dbPassword;
   }

   public void setPanelUrl(final String panelUrl) {
      this.panelUrl = panelUrl;
   }

   public void setApiKey(final String apiKey) {
      this.apiKey = apiKey;
   }

   public void setHostName(final String hostName) {
      this.hostName = hostName;
   }

   public void setDomain(final String domain) {
      this.domain = domain;
   }

   public void setPhpVersion(final String phpVersion) {
      this.phpVersion = phpVersion;
   }

   public void setRemark(final String remark) {
      this.remark = remark;
   }

   public void setFtpUser(final String ftpUser) {
      this.ftpUser = ftpUser;
   }

   public void setFtpPassword(final String ftpPassword) {
      this.ftpPassword = ftpPassword;
   }

   public void setDbUser(final String dbUser) {
      this.dbUser = dbUser;
   }

   public void setDbPassword(final String dbPassword) {
      this.dbPassword = dbPassword;
   }
}
