package com.rxinyun.member.production.modular.vhost.line.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public class MemberVhostLinePageParam {
   @Schema(
      description = "地域ID"
   )
   @NotBlank(
      message = "地域不能为空"
   )
   private String regionId;
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

   public String getRegionId() {
      return this.regionId;
   }

   public String getSearchKey() {
      return this.searchKey;
   }

   public String getSortField() {
      return this.sortField;
   }

   public String getSortOrder() {
      return this.sortOrder;
   }

   public void setRegionId(final String regionId) {
      this.regionId = regionId;
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
