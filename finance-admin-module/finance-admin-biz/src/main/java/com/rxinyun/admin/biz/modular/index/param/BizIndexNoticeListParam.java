package com.rxinyun.admin.biz.modular.index.param;

import io.swagger.v3.oas.annotations.media.Schema;

public class BizIndexNoticeListParam {
   @Schema(
      description = "条数"
   )
   private Integer limit;

   public Integer getLimit() {
      return this.limit;
   }

   public void setLimit(final Integer limit) {
      this.limit = limit;
   }
}
