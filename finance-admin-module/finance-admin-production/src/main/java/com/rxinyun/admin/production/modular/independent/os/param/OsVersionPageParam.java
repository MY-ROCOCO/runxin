package com.rxinyun.admin.production.modular.independent.os.param;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;

public class OsVersionPageParam implements Serializable {
   @Schema(
      description = "标识"
   )
   private Long id;
   @Schema(
      description = "操作系统类型ID"
   )
   private String osClassId;
   @Schema(
      description = "操作系统名称"
   )
   private String osName;
   @Schema(
      description = "线路ID"
   )
   private String lineId;
   @Schema(
      description = "状态: 0 禁用 1 启用 默认1"
   )
   private Integer status;
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
      description = "账号、姓名关键词"
   )
   private String searchKey;

   public Long getId() {
      return this.id;
   }

   public String getOsClassId() {
      return this.osClassId;
   }

   public String getOsName() {
      return this.osName;
   }

   public String getLineId() {
      return this.lineId;
   }

   public Integer getStatus() {
      return this.status;
   }

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

   public void setId(final Long id) {
      this.id = id;
   }

   public void setOsClassId(final String osClassId) {
      this.osClassId = osClassId;
   }

   public void setOsName(final String osName) {
      this.osName = osName;
   }

   public void setLineId(final String lineId) {
      this.lineId = lineId;
   }

   public void setStatus(final Integer status) {
      this.status = status;
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
}
