package com.rxinyun.member.business.modular.vhost.param;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;

public class MemberVhostPageParam implements Serializable {
   @Schema(
      description = "主机名称，模糊查询"
   )
   private String hostName;
   @Schema(
      description = "关键字，匹配主机名称或订单号"
   )
   private String searchKey;
   @Schema(
      description = "主机状态：CREATING创建中 RUNNING运行中 STOP已停止"
   )
   private String hostStatus;
   @Schema(
      description = "业务状态：1正常 -1过期 -2删除"
   )
   private Integer businessStatus;
   @Schema(
      description = "排序字段：createTime、endTime、hostName"
   )
   private String sortField;
   @Schema(
      description = "排序方式：ASCEND、DESCEND"
   )
   private String sortOrder;

   public String getHostName() {
      return this.hostName;
   }

   public String getSearchKey() {
      return this.searchKey;
   }

   public String getHostStatus() {
      return this.hostStatus;
   }

   public Integer getBusinessStatus() {
      return this.businessStatus;
   }

   public String getSortField() {
      return this.sortField;
   }

   public String getSortOrder() {
      return this.sortOrder;
   }

   public void setHostName(final String hostName) {
      this.hostName = hostName;
   }

   public void setSearchKey(final String searchKey) {
      this.searchKey = searchKey;
   }

   public void setHostStatus(final String hostStatus) {
      this.hostStatus = hostStatus;
   }

   public void setBusinessStatus(final Integer businessStatus) {
      this.businessStatus = businessStatus;
   }

   public void setSortField(final String sortField) {
      this.sortField = sortField;
   }

   public void setSortOrder(final String sortOrder) {
      this.sortOrder = sortOrder;
   }
}
