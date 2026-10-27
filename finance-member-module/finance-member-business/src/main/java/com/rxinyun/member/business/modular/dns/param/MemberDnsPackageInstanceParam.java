package com.rxinyun.member.business.modular.dns.param;

import io.swagger.v3.oas.annotations.media.Schema;

public class MemberDnsPackageInstanceParam {
   public static class PageParam {
      @Schema(
         description = "当前页码"
      )
      private Integer current;
      @Schema(
         description = "每页条数"
      )
      private Integer size;
      @Schema(
         description = "排序字段，字段驼峰名称"
      )
      private String sortField;
      @Schema(
         description = "排序方式，升序：ASCEND；降序：DESCEND"
      )
      private String sortOrder;
      @Schema(
         description = "关键词，匹配套餐名称"
      )
      private String searchKey;
      @Schema(
         description = "套餐模板ID"
      )
      private String packageId;
      @Schema(
         description = "状态：1有效 0停用 -1到期"
      )
      private Integer status;

      public Integer getCurrent() {
         return this.current;
      }

      public Integer getSize() {
         return this.size;
      }

      public String getSortField() {
         return this.sortField;
      }

      public String getSortOrder() {
         return this.sortOrder;
      }

      public String getSearchKey() {
         return this.searchKey;
      }

      public String getPackageId() {
         return this.packageId;
      }

      public Integer getStatus() {
         return this.status;
      }

      public void setCurrent(final Integer current) {
         this.current = current;
      }

      public void setSize(final Integer size) {
         this.size = size;
      }

      public void setSortField(final String sortField) {
         this.sortField = sortField;
      }

      public void setSortOrder(final String sortOrder) {
         this.sortOrder = sortOrder;
      }

      public void setSearchKey(final String searchKey) {
         this.searchKey = searchKey;
      }

      public void setPackageId(final String packageId) {
         this.packageId = packageId;
      }

      public void setStatus(final Integer status) {
         this.status = status;
      }
   }
}
