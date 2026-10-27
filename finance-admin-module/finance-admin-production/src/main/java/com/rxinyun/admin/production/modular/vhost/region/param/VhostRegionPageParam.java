package com.rxinyun.admin.production.modular.vhost.region.param;

import io.swagger.v3.oas.annotations.media.Schema;

public class VhostRegionPageParam {
   @Schema(
      description = "名称关键词"
   )
   private String searchKey;
   @Schema(
      description = "状态：1启用 0禁用"
   )
   private Integer status;
   @Schema(
      description = "排序字段，驼峰名称"
   )
   private String sortField;
   @Schema(
      description = "排序方式：ASCEND 或 DESCEND"
   )
   private String sortOrder;

   public String getSearchKey() {
      return this.searchKey;
   }

   public Integer getStatus() {
      return this.status;
   }

   public String getSortField() {
      return this.sortField;
   }

   public String getSortOrder() {
      return this.sortOrder;
   }

   public void setSearchKey(final String searchKey) {
      this.searchKey = searchKey;
   }

   public void setStatus(final Integer status) {
      this.status = status;
   }

   public void setSortField(final String sortField) {
      this.sortField = sortField;
   }

   public void setSortOrder(final String sortOrder) {
      this.sortOrder = sortOrder;
   }
}
