package com.rxinyun.member.production.modular.vhost.version.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public class MemberVhostVersionPageParam {
   @Schema(
      description = "线路ID"
   )
   @NotBlank(
      message = "线路不能为空"
   )
   private String lineId;
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

   public String getLineId() {
      return this.lineId;
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

   public void setLineId(final String lineId) {
      this.lineId = lineId;
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
