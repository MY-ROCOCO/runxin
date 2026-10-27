package com.rxinyun.common.vhost.param;

import java.util.Date;

public class VhostExpireCommand {
   private String panelUrl;
   private String apiKey;
   private String panelHostId;
   private Date endTime;

   public String getPanelUrl() {
      return this.panelUrl;
   }

   public String getApiKey() {
      return this.apiKey;
   }

   public String getPanelHostId() {
      return this.panelHostId;
   }

   public Date getEndTime() {
      return this.endTime;
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

   public void setEndTime(final Date endTime) {
      this.endTime = endTime;
   }
}
