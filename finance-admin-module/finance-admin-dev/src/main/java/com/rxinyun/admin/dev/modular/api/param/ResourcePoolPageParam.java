package com.rxinyun.admin.dev.modular.api.param;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
   description = "资源池分页查询参数"
)
public class ResourcePoolPageParam {
   @Schema(
      description = "当前页码"
   )
   private Integer current;
   @Schema(
      description = "每页条数"
   )
   private Integer size;
   @Schema(
      description = "排序字段，字段驼峰名称，如：createTime"
   )
   private String sortField;
   @Schema(
      description = "排序方式，升序：ASCEND；降序：DESCEND"
   )
   private String sortOrder;
   @Schema(
      description = "资源池类型"
   )
   private String poolType;
   @Schema(
      description = "资源池名称关键词搜索"
   )
   private String searchKey;
   @Schema(
      description = "状态：0-禁用，1-启用"
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

   public String getPoolType() {
      return this.poolType;
   }

   public String getSearchKey() {
      return this.searchKey;
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

   public void setPoolType(final String poolType) {
      this.poolType = poolType;
   }

   public void setSearchKey(final String searchKey) {
      this.searchKey = searchKey;
   }

   public void setStatus(final Integer status) {
      this.status = status;
   }
}
