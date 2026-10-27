package com.rxinyun.common.vhost.result;

import io.swagger.v3.oas.annotations.media.Schema;

public class VhostPhpVersion {
   @Schema(
      description = "面板版本号，00 表示纯静态"
   )
   private String version;
   @Schema(
      description = "显示名称"
   )
   private String name;
   @Schema(
      description = "面板是否正在运行"
   )
   private Boolean status;
   @Schema(
      description = "当前套餐是否允许切换到该版本"
   )
   private Boolean allowed;
   @Schema(
      description = "是否为当前网站使用的版本"
   )
   private Boolean current;

   public String getVersion() {
      return this.version;
   }

   public String getName() {
      return this.name;
   }

   public Boolean getStatus() {
      return this.status;
   }

   public Boolean getAllowed() {
      return this.allowed;
   }

   public Boolean getCurrent() {
      return this.current;
   }

   public void setVersion(final String version) {
      this.version = version;
   }

   public void setName(final String name) {
      this.name = name;
   }

   public void setStatus(final Boolean status) {
      this.status = status;
   }

   public void setAllowed(final Boolean allowed) {
      this.allowed = allowed;
   }

   public void setCurrent(final Boolean current) {
      this.current = current;
   }
}
