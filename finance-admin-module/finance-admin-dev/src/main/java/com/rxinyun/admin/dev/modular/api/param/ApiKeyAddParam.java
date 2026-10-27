package com.rxinyun.admin.dev.modular.api.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotNull;

public class ApiKeyAddParam {
   @Schema(
      description = "会员编号（关联client_user.id）",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotNull(
      message = "会员编号不能为空"
   )
   private Long memberNo;
   @Schema(
      description = "密钥名称/备注，如「B平台生产环境」"
   )
   private String name;

   public Long getMemberNo() {
      return this.memberNo;
   }

   public String getName() {
      return this.name;
   }

   public void setMemberNo(final Long memberNo) {
      this.memberNo = memberNo;
   }

   public void setName(final String name) {
      this.name = name;
   }
}
