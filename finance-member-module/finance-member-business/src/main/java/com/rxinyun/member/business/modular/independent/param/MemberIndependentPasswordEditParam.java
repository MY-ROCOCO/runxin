package com.rxinyun.member.business.modular.independent.param;

import io.swagger.v3.oas.annotations.media.Schema;

public class MemberIndependentPasswordEditParam {
   @Schema(
      description = "云服务器标识"
   )
   private String id;
   @Schema(
      description = "云服务器名称"
   )
   private String hostName;
   @Schema(
      description = "系统密码"
   )
   private String osPassword;

   public String getId() {
      return this.id;
   }

   public String getHostName() {
      return this.hostName;
   }

   public String getOsPassword() {
      return this.osPassword;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setHostName(final String hostName) {
      this.hostName = hostName;
   }

   public void setOsPassword(final String osPassword) {
      this.osPassword = osPassword;
   }
}
