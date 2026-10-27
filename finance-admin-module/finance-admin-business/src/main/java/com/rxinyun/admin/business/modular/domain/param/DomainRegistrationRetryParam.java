package com.rxinyun.admin.business.modular.domain.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;

@Schema(
   description = "重新发起域名注册参数"
)
public class DomainRegistrationRetryParam implements Serializable {
   @NotBlank(
      message = "记录 id 不能为空"
   )
   @Schema(
      description = "上游记录主键 id",
      requiredMode = RequiredMode.REQUIRED
   )
   private String id;

   public String getId() {
      return this.id;
   }

   public void setId(final String id) {
      this.id = id;
   }
}
