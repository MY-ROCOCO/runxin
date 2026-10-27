package com.rxinyun.production.api.dto;

import java.io.Serializable;

public class DnsPackageInstancePageParam implements Serializable {
   private Integer current;
   private Integer size;
   private String sortField;
   private String sortOrder;
   private String searchKey;
   private String memberId;
   private String packageId;
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

   public String getMemberId() {
      return this.memberId;
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

   public void setMemberId(final String memberId) {
      this.memberId = memberId;
   }

   public void setPackageId(final String packageId) {
      this.packageId = packageId;
   }

   public void setStatus(final Integer status) {
      this.status = status;
   }
}
