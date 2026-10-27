package com.rxinyun.member.production.modular.vhost.region.param;

import io.swagger.v3.oas.annotations.media.Schema;

public class MemberVhostRegionPageParam {
   @Schema(
      description = "名称关键词"
   )
   private String searchKey;
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

   public String getSortField() {
      return this.sortField;
   }

   public String getSortOrder() {
      return this.sortOrder;
   }

   public void setSearchKey(final String searchKey) {
      this.searchKey = searchKey;
   }

   public void setSortField(final String sortField) {
      this.sortField = sortField;
   }

   public void setSortOrder(final String sortOrder) {
      this.sortOrder = sortOrder;
   }
}
