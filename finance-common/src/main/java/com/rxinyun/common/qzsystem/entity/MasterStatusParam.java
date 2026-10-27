package com.rxinyun.common.qzsystem.entity;

public class MasterStatusParam {
   private String bindId;
   private String ip;
   private Integer apiPort;
   private String apiPassword;
   private String mfcNodeId;
   private String mfcLoginAccount;
   private String mfcLoginPassword;

   public String getBindId() {
      return this.bindId;
   }

   public String getIp() {
      return this.ip;
   }

   public Integer getApiPort() {
      return this.apiPort;
   }

   public String getApiPassword() {
      return this.apiPassword;
   }

   public String getMfcNodeId() {
      return this.mfcNodeId;
   }

   public String getMfcLoginAccount() {
      return this.mfcLoginAccount;
   }

   public String getMfcLoginPassword() {
      return this.mfcLoginPassword;
   }

   public void setBindId(final String bindId) {
      this.bindId = bindId;
   }

   public void setIp(final String ip) {
      this.ip = ip;
   }

   public void setApiPort(final Integer apiPort) {
      this.apiPort = apiPort;
   }

   public void setApiPassword(final String apiPassword) {
      this.apiPassword = apiPassword;
   }

   public void setMfcNodeId(final String mfcNodeId) {
      this.mfcNodeId = mfcNodeId;
   }

   public void setMfcLoginAccount(final String mfcLoginAccount) {
      this.mfcLoginAccount = mfcLoginAccount;
   }

   public void setMfcLoginPassword(final String mfcLoginPassword) {
      this.mfcLoginPassword = mfcLoginPassword;
   }
}
