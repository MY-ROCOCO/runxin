package com.rxinyun.admin.dev.modular.api.param;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
   description = "资源池请求分页查询参数"
)
public class ResourcePoolRequestPageParam {
   @Schema(
      description = "当前页码"
   )
   private Integer current;
   @Schema(
      description = "每页条数"
   )
   private Integer size;
   @Schema(
      description = "排序字段，驼峰，如：createTime"
   )
   private String sortField;
   @Schema(
      description = "排序方式：ASCEND / DESCEND"
   )
   private String sortOrder;
   @Schema(
      description = "订单号（模糊）"
   )
   private String orderNo;
   @Schema(
      description = "资源池ID"
   )
   private String resourcePoolId;
   @Schema(
      description = "操作类型：CREATE/RENEW/UPGRADE_DISK/UPGRADE_BANDWIDTH/UPGRADE_CPU_RAM/UPGRADE_DEFENSE/DELETE"
   )
   private String operationType;
   @Schema(
      description = "请求状态：0-待发送，1-已发送，2-成功，3-失败"
   )
   private Integer requestStatus;
   @Schema(
      description = "会员编号"
   )
   private Long memberNo;
   @Schema(
      description = "A系统服务器ID"
   )
   private String aServerId;

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

   public String getOrderNo() {
      return this.orderNo;
   }

   public String getResourcePoolId() {
      return this.resourcePoolId;
   }

   public String getOperationType() {
      return this.operationType;
   }

   public Integer getRequestStatus() {
      return this.requestStatus;
   }

   public Long getMemberNo() {
      return this.memberNo;
   }

   public String getAServerId() {
      return this.aServerId;
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

   public void setOrderNo(final String orderNo) {
      this.orderNo = orderNo;
   }

   public void setResourcePoolId(final String resourcePoolId) {
      this.resourcePoolId = resourcePoolId;
   }

   public void setOperationType(final String operationType) {
      this.operationType = operationType;
   }

   public void setRequestStatus(final Integer requestStatus) {
      this.requestStatus = requestStatus;
   }

   public void setMemberNo(final Long memberNo) {
      this.memberNo = memberNo;
   }

   public void setAServerId(final String aServerId) {
      this.aServerId = aServerId;
   }
}
