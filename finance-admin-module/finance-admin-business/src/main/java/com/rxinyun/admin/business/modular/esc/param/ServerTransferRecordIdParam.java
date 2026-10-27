package com.rxinyun.admin.business.modular.esc.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class ServerTransferRecordIdParam {
   @NotBlank(
      message = "转让服务器操作记录标识不能为空"
   )
   @Schema(
      description = "转让服务器操作记录标识",
      required = true
   )
   private String id;
   @NotNull
   @Positive(
      message = "只能是1 / 2"
   )
   @Schema(
      description = "操作状态标识,1为通过,2为拒绝"
   )
   private Integer status;
   @Schema(
      description = "密码",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "密码不能为空"
   )
   private String adminPassword;

   public String getId() {
      return this.id;
   }

   public Integer getStatus() {
      return this.status;
   }

   public String getAdminPassword() {
      return this.adminPassword;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setStatus(final Integer status) {
      this.status = status;
   }

   public void setAdminPassword(final String adminPassword) {
      this.adminPassword = adminPassword;
   }
}
