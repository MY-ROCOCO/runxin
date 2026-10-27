package com.rxinyun.member.business.modular.vhost.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;

public class MemberVhostIdParam implements Serializable {
   @NotBlank(
      message = "虚拟主机ID不能为空"
   )
   @Schema(
      description = "虚拟主机ID"
   )
   private String id;

   public String getId() {
      return this.id;
   }

   public void setId(final String id) {
      this.id = id;
   }
}
