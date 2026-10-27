package com.rxinyun.admin.business.modular.esc.param;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;

public class ServerRemarkEditParam implements Serializable {
   @Schema(
      description = "云服务器ID"
   )
   String id;
   @Schema(
      description = "管理员备注"
   )
   String remark;

   public String getId() {
      return this.id;
   }

   public String getRemark() {
      return this.remark;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setRemark(final String remark) {
      this.remark = remark;
   }
}
