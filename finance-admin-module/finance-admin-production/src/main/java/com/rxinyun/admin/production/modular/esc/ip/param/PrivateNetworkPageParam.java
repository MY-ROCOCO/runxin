package com.rxinyun.admin.production.modular.esc.ip.param;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;

public class PrivateNetworkPageParam implements Serializable {
   @Schema(
      description = "私网IP标识"
   )
   private String id;
   @Schema(
      description = "宿主机"
   )
   private String masterId;
   @Schema(
      description = "线路ID"
   )
   private String lineId;
   @Schema(
      description = "可用性（0 未占用，1 已占用"
   )
   private Integer enable;
   @Schema(
      description = "名称"
   )
   private String name;
   @Schema(
      description = "状态"
   )
   private Integer status;
   @Schema(
      description = "私网IP"
   )
   private String ip;
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

   public String getId() {
      return this.id;
   }

   public String getMasterId() {
      return this.masterId;
   }

   public String getLineId() {
      return this.lineId;
   }

   public Integer getEnable() {
      return this.enable;
   }

   public String getName() {
      return this.name;
   }

   public Integer getStatus() {
      return this.status;
   }

   public String getIp() {
      return this.ip;
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

   public void setId(final String id) {
      this.id = id;
   }

   public void setMasterId(final String masterId) {
      this.masterId = masterId;
   }

   public void setLineId(final String lineId) {
      this.lineId = lineId;
   }

   public void setEnable(final Integer enable) {
      this.enable = enable;
   }

   public void setName(final String name) {
      this.name = name;
   }

   public void setStatus(final Integer status) {
      this.status = status;
   }

   public void setIp(final String ip) {
      this.ip = ip;
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
