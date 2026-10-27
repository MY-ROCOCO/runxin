package com.rxinyun.admin.business.modular.esc.param;

import io.swagger.v3.oas.annotations.media.Schema;

public class ServerIpIdParam {
   @Schema(
      description = "服务器IP标识"
   )
   private String id;
   @Schema(
      description = "云服务器ID"
   )
   private String serverId;
   @Schema(
      description = "是否主IP 1:是 0:否"
   )
   private Integer isMain;

   public String getId() {
      return this.id;
   }

   public String getServerId() {
      return this.serverId;
   }

   public Integer getIsMain() {
      return this.isMain;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setServerId(final String serverId) {
      this.serverId = serverId;
   }

   public void setIsMain(final Integer isMain) {
      this.isMain = isMain;
   }
}
