package com.rxinyun.admin.business.modular.vhost.param;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;

public class AdminVhostPageParam implements Serializable {
   @Schema(
      description = "会员编号"
   )
   private Long memberNo;
   @Schema(
      description = "来源订单号"
   )
   private String orderNo;
   @Schema(
      description = "主控ID，按主控筛选其下的虚拟主机"
   )
   private String masterId;
   @Schema(
      description = "主机名称，模糊查询"
   )
   private String hostName;
   @Schema(
      description = "关键字，匹配主机名称、订单号或会员编号"
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

   public Long getMemberNo() {
      return this.memberNo;
   }

   public String getOrderNo() {
      return this.orderNo;
   }

   public String getMasterId() {
      return this.masterId;
   }

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

   public void setMemberNo(final Long memberNo) {
      this.memberNo = memberNo;
   }

   public void setOrderNo(final String orderNo) {
      this.orderNo = orderNo;
   }

   public void setMasterId(final String masterId) {
      this.masterId = masterId;
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
