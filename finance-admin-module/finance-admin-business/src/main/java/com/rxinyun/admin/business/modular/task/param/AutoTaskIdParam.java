package com.rxinyun.admin.business.modular.task.param;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
   description = "自动化任务添加参数"
)
public class AutoTaskIdParam {
   @Schema(
      description = "任务ID"
   )
   private String id;

   public String getId() {
      return this.id;
   }

   public void setId(final String id) {
      this.id = id;
   }
}
