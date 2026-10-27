package com.rxinyun.member.business.modular.vhost.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;

public class MemberVhostDomainParam extends MemberVhostIdParam {
   @NotBlank(
      message = "域名不能为空"
   )
   @Schema(
      description = "要添加或删除的域名，80端口写 example.com，非80写 example.com:81",
      requiredMode = RequiredMode.REQUIRED
   )
   private String domain;

   public String getDomain() {
      return this.domain;
   }

   public void setDomain(final String domain) {
      this.domain = domain;
   }
}
