package com.rxinyun.admin.business.modular.vhost.param;

import io.swagger.v3.oas.annotations.media.Schema;

public class AdminVhostRemarkParam extends AdminVhostIdParam {
   @Schema(
      description = "管理员备注"
   )
   private String remark;

   public String getRemark() {
      return this.remark;
   }

   public void setRemark(final String remark) {
      this.remark = remark;
   }
}
