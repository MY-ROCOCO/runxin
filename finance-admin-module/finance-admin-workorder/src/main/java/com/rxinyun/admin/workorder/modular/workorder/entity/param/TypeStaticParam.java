package com.rxinyun.admin.workorder.modular.workorder.entity.param;

import io.swagger.v3.oas.annotations.media.Schema;

public class TypeStaticParam {
   @Schema(
      description = "工单类型ID"
   )
   private String id;
   @Schema(
      description = "工单类型状态  传入当前正确状态"
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
