package com.rxinyun.admin.business.modular.esc.param;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;

public class ServerBackupIdParam implements Serializable {
   @Schema(
      description = "云服务器备份记录ID"
   )
   private String id;

   public String getId() {
      return this.id;
   }

   public void setId(final String id) {
      this.id = id;
   }
}
