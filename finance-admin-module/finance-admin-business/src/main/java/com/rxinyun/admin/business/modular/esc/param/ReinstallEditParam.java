package com.rxinyun.admin.business.modular.esc.param;

import io.swagger.v3.oas.annotations.media.Schema;

public class ReinstallEditParam {
   @Schema(
      description = "云服务器标识"
   )
   private String id;
   @Schema(
      description = "云服务器名称"
   )
   private String hostName;
   @Schema(
      description = "操作系统类型ID"
   )
   private String systemClassId;
   @Schema(
      description = "操作系统版本ID"
   )
   private String systemId;
   @Schema(
      description = "操作系统版本名称"
   )
   private String systemName;
   @Schema(
      description = "系统密码"
   )
   private String systemPassword;
   @Schema(
      description = "远程端口（魔方云重装使用）"
   )
   private Integer remotePort;
   @Schema(
      description = "是否格式化数据盘（魔方云重装使用）：0=不格式化，1=格式化"
   )
   private Integer formatDataDisk;

   public String getId() {
      return this.id;
   }

   public String getHostName() {
      return this.hostName;
   }

   public String getSystemClassId() {
      return this.systemClassId;
   }

   public String getSystemId() {
      return this.systemId;
   }

   public String getSystemName() {
      return this.systemName;
   }

   public String getSystemPassword() {
      return this.systemPassword;
   }

   public Integer getRemotePort() {
      return this.remotePort;
   }

   public Integer getFormatDataDisk() {
      return this.formatDataDisk;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setHostName(final String hostName) {
      this.hostName = hostName;
   }

   public void setSystemClassId(final String systemClassId) {
      this.systemClassId = systemClassId;
   }

   public void setSystemId(final String systemId) {
      this.systemId = systemId;
   }

   public void setSystemName(final String systemName) {
      this.systemName = systemName;
   }

   public void setSystemPassword(final String systemPassword) {
      this.systemPassword = systemPassword;
   }

   public void setRemotePort(final Integer remotePort) {
      this.remotePort = remotePort;
   }

   public void setFormatDataDisk(final Integer formatDataDisk) {
      this.formatDataDisk = formatDataDisk;
   }
}
