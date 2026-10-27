package com.rxinyun.admin.business.modular.esc.param;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public class ServerAddEndDaysParam {
   @Schema(
      description = "云服务器标识列表"
   )
   private List<String> serverIds;
   @Schema(
      description = "增加的到期天数"
   )
   private Integer days;

   public List<String> getServerIds() {
      return this.serverIds;
   }

   public Integer getDays() {
      return this.days;
   }

   public void setServerIds(final List<String> serverIds) {
      this.serverIds = serverIds;
   }

   public void setDays(final Integer days) {
      this.days = days;
   }
}
