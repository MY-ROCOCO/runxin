package com.rxinyun.admin.auth.modular.login.result;

import io.swagger.v3.oas.annotations.media.Schema;

public class AuthPicValidCodeResult {
   @Schema(
      description = "验证码图片，Base64"
   )
   private String validCodeBase64;
   @Schema(
      description = "验证码请求号"
   )
   private String validCodeReqNo;

   public String getValidCodeBase64() {
      return this.validCodeBase64;
   }

   public String getValidCodeReqNo() {
      return this.validCodeReqNo;
   }

   public void setValidCodeBase64(final String validCodeBase64) {
      this.validCodeBase64 = validCodeBase64;
   }

   public void setValidCodeReqNo(final String validCodeReqNo) {
      this.validCodeReqNo = validCodeReqNo;
   }
}
