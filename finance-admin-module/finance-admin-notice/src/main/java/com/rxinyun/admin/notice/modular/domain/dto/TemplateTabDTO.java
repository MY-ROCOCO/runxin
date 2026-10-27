package com.rxinyun.admin.notice.modular.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public class TemplateTabDTO {
   @Schema(
      description = "模板标签标识"
   )
   private String id;
   @NotBlank(
      message = "标签名称不能为空"
   )
   @Schema(
      description = "标签名称"
   )
   private String name;

   public String getId() {
      return this.id;
   }

   public String getName() {
      return this.name;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setName(final String name) {
      this.name = name;
   }
}
