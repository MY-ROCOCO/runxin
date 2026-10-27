package com.rxinyun.admin.dev.modular.api.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(
   description = "资源池状态参数"
)
public class ResourcePoolStatusParam {
   @Schema(
      description = "主键ID"
   )
   @NotBlank(
      message = "ID不能为空"
   )
   private String id;
   @Schema(
      description = "状态：0-禁用，1-启用"
   )
   @NotNull(
      message = "状态不能为空"
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
