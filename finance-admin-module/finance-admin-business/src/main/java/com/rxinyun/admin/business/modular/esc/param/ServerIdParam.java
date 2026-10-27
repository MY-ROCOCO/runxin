package com.rxinyun.admin.business.modular.esc.param;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;

public class ServerIdParam implements Serializable {
   @Schema(
      description = "云服务器标识"
   )
   private String id;
   @Schema(
      description = "RkHyper-v重建方式：0=挂原盘，1=全新重建；轻舟等其它受控不传"
   )
   private Integer fullRebuild;

   public String getId() {
      return this.id;
   }

   public Integer getFullRebuild() {
      return this.fullRebuild;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setFullRebuild(final Integer fullRebuild) {
      this.fullRebuild = fullRebuild;
   }
}
