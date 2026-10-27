package com.rxinyun.member.business.modular.independent.param;

import io.swagger.v3.oas.annotations.media.Schema;

public class ReinstallEditParam {
   @Schema(
      description = "独立服务器标识"
   )
   private String id;
   @Schema(
      description = "独立服务器名称"
   )
   private String hostName;
   @Schema(
      description = "操作系统类型ID"
   )
   private String osClassId;
   @Schema(
      description = "操作系统版本ID"
   )
   private String osVersionId;
   @Schema(
      description = "操作系统版本名称"
   )
   private String osName;
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

   public String getOsClassId() {
      return this.osClassId;
   }

   public String getOsVersionId() {
      return this.osVersionId;
   }

   public String getOsName() {
      return this.osName;
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

   public void setOsClassId(final String osClassId) {
      this.osClassId = osClassId;
   }

   public void setOsVersionId(final String osVersionId) {
      this.osVersionId = osVersionId;
   }

   public void setOsName(final String osName) {
      this.osName = osName;
   }

   public void setOsPassword(final String osPassword) {
      this.osPassword = osPassword;
   }
}
