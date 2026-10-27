package com.rxinyun.admin.business.modular.esc.param;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;

public class ServerEndTimeParam {
   @Schema(
      description = "云服务器标识"
   )
   private String id;
   @Schema(
      description = "到期日期"
   )
   private Date endTime;

   public String getId() {
      return this.id;
   }

   public Date getEndTime() {
      return this.endTime;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setEndTime(final Date endTime) {
      this.endTime = endTime;
   }
}
