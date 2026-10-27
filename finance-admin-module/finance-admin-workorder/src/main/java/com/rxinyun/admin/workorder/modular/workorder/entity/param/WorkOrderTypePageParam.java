package com.rxinyun.admin.workorder.modular.workorder.entity.param;

import io.swagger.v3.oas.annotations.media.Schema;

public class WorkOrderTypePageParam {
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
      description = "工单类型ID"
   )
   private String id;
   @Schema(
      description = "工单类型名称"
   )
   private String typeName;
   @Schema(
      description = "工单是否管理产品  (0:不关联(默认)  ,1:关联 ) "
   )
   private Integer isRelevance;
   @Schema(
      description = "工单类型状态  (0:不可见(默认) , 1:可见)"
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

   public String getId() {
      return this.id;
   }

   public String getTypeName() {
      return this.typeName;
   }

   public Integer getIsRelevance() {
      return this.isRelevance;
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

   public void setId(final String id) {
      this.id = id;
   }

   public void setTypeName(final String typeName) {
      this.typeName = typeName;
   }

   public void setIsRelevance(final Integer isRelevance) {
      this.isRelevance = isRelevance;
   }

   public void setStatus(final Integer status) {
      this.status = status;
   }
}
