package com.rxinyun.member.business.modular.vhost.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;

public class MemberVhostPhpParam extends MemberVhostIdParam {
   @NotBlank(
      message = "PHP版本不能为空"
   )
   @Schema(
      description = "PHP版本，如 8.1、81、00",
      requiredMode = RequiredMode.REQUIRED
   )
   private String phpVersion;

   public String getPhpVersion() {
      return this.phpVersion;
   }

   public void setPhpVersion(final String phpVersion) {
      this.phpVersion = phpVersion;
   }
}
