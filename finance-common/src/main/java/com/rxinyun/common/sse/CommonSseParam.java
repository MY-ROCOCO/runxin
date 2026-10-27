package com.rxinyun.common.sse;

public class CommonSseParam {
   private String clientId;
   private String loginId;

   public String getClientId() {
      return this.clientId;
   }

   public String getLoginId() {
      return this.loginId;
   }

   public void setClientId(final String clientId) {
      this.clientId = clientId;
   }

   public void setLoginId(final String loginId) {
      this.loginId = loginId;
   }
}
