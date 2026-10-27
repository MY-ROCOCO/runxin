package com.rxinyun.common.vhost.result;

public class VhostCreateResult {
   private boolean success;
   private String message;
   private String panelHostId;
   private String domain;
   private String ftpUser;
   private String ftpPassword;
   private String dbName;
   private String dbUser;
   private String dbPassword;

   public boolean accountsReady() {
      return this.panelHostId != null
         && !this.panelHostId.isBlank()
         && this.ftpUser != null
         && !this.ftpUser.isBlank()
         && this.ftpPassword != null
         && !this.ftpPassword.isBlank()
         && this.dbUser != null
         && !this.dbUser.isBlank()
         && this.dbPassword != null
         && !this.dbPassword.isBlank();
   }

   public static VhostCreateResult fail(String message) {
      VhostCreateResult result = new VhostCreateResult();
      result.setSuccess(false);
      result.setMessage(message);
      return result;
   }

   public boolean isSuccess() {
      return this.success;
   }

   public String getMessage() {
      return this.message;
   }

   public String getPanelHostId() {
      return this.panelHostId;
   }

   public String getDomain() {
      return this.domain;
   }

   public String getFtpUser() {
      return this.ftpUser;
   }

   public String getFtpPassword() {
      return this.ftpPassword;
   }

   public String getDbName() {
      return this.dbName;
   }

   public String getDbUser() {
      return this.dbUser;
   }

   public String getDbPassword() {
      return this.dbPassword;
   }

   public void setSuccess(final boolean success) {
      this.success = success;
   }

   public void setMessage(final String message) {
      this.message = message;
   }

   public void setPanelHostId(final String panelHostId) {
      this.panelHostId = panelHostId;
   }

   public void setDomain(final String domain) {
      this.domain = domain;
   }

   public void setFtpUser(final String ftpUser) {
      this.ftpUser = ftpUser;
   }

   public void setFtpPassword(final String ftpPassword) {
      this.ftpPassword = ftpPassword;
   }

   public void setDbName(final String dbName) {
      this.dbName = dbName;
   }

   public void setDbUser(final String dbUser) {
      this.dbUser = dbUser;
   }

   public void setDbPassword(final String dbPassword) {
      this.dbPassword = dbPassword;
   }
}
