package com.rxinyun.admin.dev.modular.slideshow.param;

import io.swagger.v3.oas.annotations.media.Schema;

public class DevSlideshowPageParam {
   @Schema(
      description = "当前页码"
   )
   private Integer current;
   @Schema(
      description = "每页条数"
   )
   private Integer size;
   @Schema(
      description = "排序字段，字段驼峰名称，如：userName"
   )
   private String sortField;
   @Schema(
      description = "排序方式，升序：ASCEND；降序：DESCEND"
   )
   private String sortOrder;
   @Schema(
      description = "关键词"
   )
   private String searchKey;
   @Schema(
      description = "标题"
   )
   private String title;
   @Schema(
      description = "状态"
   )
   private String status;

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

   public String getTitle() {
      return this.title;
   }

   public String getStatus() {
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

   public void setTitle(final String title) {
      this.title = title;
   }

   public void setStatus(final String status) {
      this.status = status;
   }
}
