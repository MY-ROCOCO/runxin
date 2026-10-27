package com.rxinyun.admin.dev.modular.telegram.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public class DevTelegramBindTokenParam {
   @Schema(
      description = "用户类型：MEMBER/ADMIN"
   )
   @NotBlank(
      message = "用户类型不能为空"
   )
   private String userType;
   @Schema(
      description = "用户ID"
   )
   @NotBlank(
      message = "用户ID不能为空"
   )
   private String userId;

   public String getUserType() {
      return this.userType;
   }

   public String getUserId() {
      return this.userId;
   }

   public void setUserType(final String userType) {
      this.userType = userType;
   }

   public void setUserId(final String userId) {
      this.userId = userId;
   }
}
