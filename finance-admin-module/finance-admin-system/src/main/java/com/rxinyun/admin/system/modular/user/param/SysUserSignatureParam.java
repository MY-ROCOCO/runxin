package com.rxinyun.admin.system.modular.user.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;

public class SysUserSignatureParam {
   @Schema(
      description = "signature",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "signature签名图片不能为空"
   )
   private String signature;

   public String getSignature() {
      return this.signature;
   }

   public void setSignature(final String signature) {
      this.signature = signature;
   }
}
