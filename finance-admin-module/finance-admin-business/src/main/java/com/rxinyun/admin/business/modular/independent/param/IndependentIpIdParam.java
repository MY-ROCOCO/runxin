package com.rxinyun.admin.business.modular.independent.param;

import io.swagger.v3.oas.annotations.media.Schema;

public class IndependentIpIdParam {
   @Schema(
      description = "服务器ID"
   )
   private String serverId;
   @Schema(
      description = "服务器IP标识"
   )
   private String id;
   @Schema(
      description = "是否主IP 1:是 0:否"
   )
   private Integer isMain;

   public String getServerId() {
      return this.serverId;
   }

   public String getId() {
      return this.id;
   }

   public Integer getIsMain() {
      return this.isMain;
   }

   public void setServerId(final String serverId) {
      this.serverId = serverId;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setIsMain(final Integer isMain) {
      this.isMain = isMain;
   }
}
