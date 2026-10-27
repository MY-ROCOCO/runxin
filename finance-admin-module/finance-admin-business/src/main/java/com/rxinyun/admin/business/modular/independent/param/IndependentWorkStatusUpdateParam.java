package com.rxinyun.admin.business.modular.independent.param;

import io.swagger.v3.oas.annotations.media.Schema;

public class IndependentWorkStatusUpdateParam {
   @Schema(
      description = "独立服务器人工处理工单ID"
   )
   private String id;
   @Schema(
      description = "处理状态：0.未处理 1.处理中 2.处理完成 3.处理失败"
   )
   private Integer businessStatus;

   public String getId() {
      return this.id;
   }

   public Integer getBusinessStatus() {
      return this.businessStatus;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setBusinessStatus(final Integer businessStatus) {
      this.businessStatus = businessStatus;
   }
}
