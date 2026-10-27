package com.rxinyun.admin.production.modular.esc.master.param;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;

public class ServerMasterPageParam implements Serializable {
   @Schema(
      description = "宿主机标识"
   )
   private String id;
   @Schema(
      description = "线路ID"
   )
   private String lineId;
   @Schema(
      description = "宿主机名称"
   )
   private String masterName;
   @Schema(
      description = "NAT交换机名称"
   )
   private String natSwitchName;
   @Schema(
      description = "是否共享IP"
   )
   private Integer shareIp;
   @Schema(
      description = "状态"
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

   public String getId() {
      return this.id;
   }

   public String getLineId() {
      return this.lineId;
   }

   public String getMasterName() {
      return this.masterName;
   }

   public String getNatSwitchName() {
      return this.natSwitchName;
   }

   public Integer getShareIp() {
      return this.shareIp;
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

   public void setId(final String id) {
      this.id = id;
   }

   public void setLineId(final String lineId) {
      this.lineId = lineId;
   }

   public void setMasterName(final String masterName) {
      this.masterName = masterName;
   }

   public void setNatSwitchName(final String natSwitchName) {
      this.natSwitchName = natSwitchName;
   }

   public void setShareIp(final Integer shareIp) {
      this.shareIp = shareIp;
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
