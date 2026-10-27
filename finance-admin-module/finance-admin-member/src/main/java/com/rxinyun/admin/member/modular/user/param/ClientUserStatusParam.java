package com.rxinyun.admin.member.modular.user.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ClientUserStatusParam {
   @Schema(
      description = "id",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotNull(
      message = "id不能为空"
   )
   private Long id;
   @Schema(
      description = "用户状态: 启用： ENABLE 禁用：DISABLED"
   )
   @NotBlank(
      message = "用户状态不能为空"
   )
   private String userStatus;

   public Long getId() {
      return this.id;
   }

   public String getUserStatus() {
      return this.userStatus;
   }

   public void setId(final Long id) {
      this.id = id;
   }

   public void setUserStatus(final String userStatus) {
      this.userStatus = userStatus;
   }
}
