package com.rxinyun.admin.production.modular.esc.region.param;

import io.swagger.v3.oas.annotations.media.Schema;

public class ServerRegionEditSortParam {
   @Schema(
      description = "地域标识"
   )
   private String id;
   @Schema(
      description = "排序"
   )
   private Integer sortId;

   public String getId() {
      return this.id;
   }

   public Integer getSortId() {
      return this.sortId;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setSortId(final Integer sortId) {
      this.sortId = sortId;
   }
}
