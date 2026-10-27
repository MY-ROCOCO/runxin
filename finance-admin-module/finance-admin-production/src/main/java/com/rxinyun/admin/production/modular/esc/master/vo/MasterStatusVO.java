package com.rxinyun.admin.production.modular.esc.master.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;

@Schema(
   description = "宿主机状态"
)
public class MasterStatusVO implements Serializable {
   @Schema(
      description = "状态：1=正常 0=异常",
      example = "1"
   )
   private Integer status;

   public Integer getStatus() {
      return this.status;
   }

   public void setStatus(final Integer status) {
      this.status = status;
   }

   public MasterStatusVO() {
   }

   public MasterStatusVO(final Integer status) {
      this.status = status;
   }
}
