package com.rxinyun.admin.dev.modular.message.param;

import io.swagger.v3.oas.annotations.media.Schema;

public class DevMessagePageParam {
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
      description = "站内信分类"
   )
   private String category;
   @Schema(
      description = "主题关键词"
   )
   private String searchKey;
   @Schema(
      description = "接收用户id"
   )
   private String receiveUserId;

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

   public String getCategory() {
      return this.category;
   }

   public String getSearchKey() {
      return this.searchKey;
   }

   public String getReceiveUserId() {
      return this.receiveUserId;
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

   public void setCategory(final String category) {
      this.category = category;
   }

   public void setSearchKey(final String searchKey) {
      this.searchKey = searchKey;
   }

   public void setReceiveUserId(final String receiveUserId) {
      this.receiveUserId = receiveUserId;
   }
}
