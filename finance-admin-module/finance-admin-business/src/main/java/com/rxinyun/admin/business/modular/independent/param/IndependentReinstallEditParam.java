package com.rxinyun.admin.business.modular.independent.param;

import io.swagger.v3.oas.annotations.media.Schema;

public class IndependentReinstallEditParam {
   @Schema(
      description = "独立服务器主键 ID"
   )
   private String id;
   @Schema(
      description = "操作系统分类 ID（osClassId）"
   )
   private String osClassId;
   @Schema(
      description = "操作系统版本 ID（osVersionId）"
   )
   private String osVersionId;
   @Schema(
      description = "系统密码"
   )
   private String osPassword;

   public String getId() {
      return this.id;
   }

   public String getOsClassId() {
      return this.osClassId;
   }

   public String getOsVersionId() {
      return this.osVersionId;
   }

   public String getOsPassword() {
      return this.osPassword;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setOsClassId(final String osClassId) {
      this.osClassId = osClassId;
   }

   public void setOsVersionId(final String osVersionId) {
      this.osVersionId = osVersionId;
   }

   public void setOsPassword(final String osPassword) {
      this.osPassword = osPassword;
   }
}
