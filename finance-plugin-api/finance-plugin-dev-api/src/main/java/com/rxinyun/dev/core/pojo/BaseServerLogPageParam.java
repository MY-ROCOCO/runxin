package com.rxinyun.dev.core.pojo;

import io.swagger.v3.oas.annotations.media.Schema;

public class BaseServerLogPageParam {
   @Schema(
      description = "当前页码"
   )
   private Integer current;
   @Schema(
      description = "每页条数"
   )
   private Integer size;
   @Schema(
      description = "排序字段，字段驼峰名称，如：operationTime"
   )
   private String sortField;
   @Schema(
      description = "排序方式，升序：ASCEND；降序：DESCEND"
   )
   private String sortOrder;
   @Schema(
      description = "日志分类"
   )
   private String category;
   @Schema(
      description = "会员编号，会员端调用时传当前会员编号"
   )
   private Long memberNo;
   @Schema(
      description = "日志名称关键词"
   )
   private String searchKey;
   @Schema(
      description = "云服务器ID"
   )
   private String serverId;

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

   public Long getMemberNo() {
      return this.memberNo;
   }

   public String getSearchKey() {
      return this.searchKey;
   }

   public String getServerId() {
      return this.serverId;
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

   public void setMemberNo(final Long memberNo) {
      this.memberNo = memberNo;
   }

   public void setSearchKey(final String searchKey) {
      this.searchKey = searchKey;
   }

   public void setServerId(final String serverId) {
      this.serverId = serverId;
   }
}
