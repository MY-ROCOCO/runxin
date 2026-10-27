package com.rxinyun.admin.biz.modular.notice.param;

import io.swagger.v3.oas.annotations.media.Schema;

public class BizNoticePageParam {
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
      description = "类型"
   )
   private String type;
   @Schema(
      description = "发布位置"
   )
   private String place;
   @Schema(
      description = "状态"
   )
   private String status;
   @Schema(
      description = "创建时间开始"
   )
   private String startCreateTime;
   @Schema(
      description = "创建时间结束"
   )
   private String endCreateTime;

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

   public String getType() {
      return this.type;
   }

   public String getPlace() {
      return this.place;
   }

   public String getStatus() {
      return this.status;
   }

   public String getStartCreateTime() {
      return this.startCreateTime;
   }

   public String getEndCreateTime() {
      return this.endCreateTime;
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

   public void setType(final String type) {
      this.type = type;
   }

   public void setPlace(final String place) {
      this.place = place;
   }

   public void setStatus(final String status) {
      this.status = status;
   }

   public void setStartCreateTime(final String startCreateTime) {
      this.startCreateTime = startCreateTime;
   }

   public void setEndCreateTime(final String endCreateTime) {
      this.endCreateTime = endCreateTime;
   }
}
