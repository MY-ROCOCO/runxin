package com.rxinyun.member.business.modular.esc.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class MemberServerTransferRecordIdParam {
   @NotBlank
   @Schema(
      description = "转让服务器操作记录标识"
   )
   private String id;
   @NotNull
   @Positive
   @Schema(
      description = "操作状态标识,1为通过,2为拒绝"
   )
   private Integer status;

   public String getId() {
      return this.id;
   }

   public Integer getStatus() {
      return this.status;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setStatus(final Integer status) {
      this.status = status;
   }
}
