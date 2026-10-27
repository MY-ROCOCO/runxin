package com.rxinyun.business.core.pojo;

import java.io.Serializable;
import java.util.List;

public class BaseMemberVhostProvision implements Serializable {
   private String hostId;
   private String masterId;
   private String panelHostId;
   private String ftpAddress;
   private String ftpUser;
   private String ftpPassword;
   private String dbName;
   private String dbUser;
   private String dbPassword;
   private List<String> bindDomains;
   private Boolean running;

   public String getHostId() {
      return this.hostId;
   }

   public String getMasterId() {
      return this.masterId;
   }

   public String getPanelHostId() {
      return this.panelHostId;
   }

   public String getFtpAddress() {
      return this.ftpAddress;
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

   public List<String> getBindDomains() {
      return this.bindDomains;
   }

   public Boolean getRunning() {
      return this.running;
   }

   public void setHostId(final String hostId) {
      this.hostId = hostId;
   }

   public void setMasterId(final String masterId) {
      this.masterId = masterId;
   }

   public void setPanelHostId(final String panelHostId) {
      this.panelHostId = panelHostId;
   }

   public void setFtpAddress(final String ftpAddress) {
      this.ftpAddress = ftpAddress;
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

   public void setBindDomains(final List<String> bindDomains) {
      this.bindDomains = bindDomains;
   }

   public void setRunning(final Boolean running) {
      this.running = running;
   }
}
