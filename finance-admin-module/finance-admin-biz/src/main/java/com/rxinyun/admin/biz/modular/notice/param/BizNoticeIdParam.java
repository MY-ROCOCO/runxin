package com.rxinyun.admin.biz.modular.notice.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public class BizNoticeIdParam {
   @Schema(
      description = "主键"
   )
   @NotBlank(
      message = "id不能为空"
   )
   private String id;

   public String getId() {
      return this.id;
   }

   public void setId(final String id) {
      this.id = id;
   }
}
